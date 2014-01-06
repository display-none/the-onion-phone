package org.theonionphone.protocol;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.util.Random;

import org.theonionphone.audio.codecs.Codec;
import org.theonionphone.common.exceptions.AudioManagerException;
import org.theonionphone.common.exceptions.ProtocolException;

public class SrtpProtocol {

	private static final int SALT_SIZE = 14;
	private static final int SRTP_OVERHEAD_IN_BYTES = 16;	//12-byte header plus 4-byte authorization tag
	private int packetSizeInBytes;
	private int payloadSizeInBytes;
	private boolean srtpInitialized = false;
	
	private SrtpWrapper wrapper;
	private SrtpUnwrapper unwrapper;

	public void initiateOutgoingSession(byte[] txKey, byte[] rxKey, Codec codec, InputStream networkInputStream, OutputStream networkOutputStream) {
		packetSizeInBytes = SRTP_OVERHEAD_IN_BYTES + roundUpToMultipleOf4(codec.getBytesSize());
		payloadSizeInBytes = codec.getBytesSize();
		
		byte[] senderSalt = getRandomSalt();
		
		initSender(txKey, senderSalt, codec.getCodecType(), codec.getSamplesSize());
		int senderSsrc = getSsrc();
		
		sendSsrcAndSalt(networkOutputStream, senderSalt, senderSsrc);
		
		int receiverSsrc = receiveSsrc(networkInputStream);
		byte[] receiverSalt = receiveSalt(networkInputStream);
		
		initReceiver(receiverSsrc, rxKey, receiverSalt);
		srtpInitialized = true;
	}
	
	public void initiateIncomingSession(byte[] key, Codec codec, InputStream networkInputStream, OutputStream networkOutputStream) {
		packetSizeInBytes = SRTP_OVERHEAD_IN_BYTES + roundUpToMultipleOf4(codec.getBytesSize());
		payloadSizeInBytes = codec.getBytesSize();
		
		byte[] senderSalt = getRandomSalt();
		
		int receiverSsrc = receiveSsrc(networkInputStream);
		byte[] receiverSalt = receiveSalt(networkInputStream);
		
		initSender(key, senderSalt, codec.getCodecType(), codec.getSamplesSize());
		int senderSsrc = getSsrc();
		
		sendSsrcAndSalt(networkOutputStream, senderSalt, senderSsrc);
		
		initReceiver(receiverSsrc, key, receiverSalt);
		srtpInitialized = true;
	}

	private byte[] receiveSalt(InputStream networkInputStream) {
		try {
			byte[] salt = new byte[SALT_SIZE];
			networkInputStream.read(salt);
			return salt;
		} catch(IOException e) {
			//TODO some error handling
			return null;
		}
	}

	private int receiveSsrc(InputStream networkInputStream) {
		DataInputStream dis = new DataInputStream(networkInputStream);
		try {
			return dis.readInt();
		} catch(IOException e) {
			//TODO some error handling
			return 0;
		}
	}

	private void sendSsrcAndSalt(OutputStream networkOutputStream, byte[] salt, int senderSsrc) {
		DataOutputStream dos = new DataOutputStream(networkOutputStream);
		try {
			dos.writeInt(senderSsrc);
			dos.write(salt);
		} catch(IOException e) {
			//TODO some error handling
		}
	}
	
	private byte[] getRandomSalt() {
		byte[] salt = new byte[SALT_SIZE];
		(new Random()).nextBytes(salt);
		return salt;
	}

	public InputStream wrapStream(InputStream inputStream) {
		PipedOutputStream pipedOutputStream = new PipedOutputStream();
		createWrapper(inputStream, pipedOutputStream);
		wrapper.start();
		return prepareInputStream(pipedOutputStream);
	}

	private void createWrapper(InputStream inputStream, OutputStream outputStream) {
		if(wrapper != null) {
			throw new ProtocolException("wrapper is already initialized");
		}
		wrapper = new SrtpWrapper(inputStream, outputStream, payloadSizeInBytes);
	}
	
	public InputStream unwrapStream(InputStream inputStream) {
		PipedOutputStream pipedOutputStream = new PipedOutputStream();
		createUnwrapper(inputStream, pipedOutputStream);
		unwrapper.start();
		return prepareInputStream(pipedOutputStream);
	}
	
	private void createUnwrapper(InputStream inputStream, OutputStream outputStream) {
		if(unwrapper != null) {
			throw new ProtocolException("unwrapper is already initialized");
		}
		unwrapper = new SrtpUnwrapper(inputStream, outputStream, packetSizeInBytes);
	}
	
	private PipedInputStream prepareInputStream(PipedOutputStream outputStream) {
		try {
			return new PipedInputStream(outputStream);
		} catch(IOException e) {
			throw new AudioManagerException("Cannot instantiate stream");
		}
	}
	
	private int roundUpToMultipleOf4(int size) {
		while(size % 4 != 0) {
			size++;
		}
		return size;
	}
	
	private native int initSender(byte[] keyByteArray, byte[] saltByteArray, int codecType, int sampleCount);
	
	private native int getSsrc();
	
	private native int initReceiver(int ssrc, byte[] keyByteArray, byte[] saltByteArray);
	
	private native byte[] unwrapPacket(byte[] packet);
	
	static {
		System.loadLibrary("srtp");
	}
}
