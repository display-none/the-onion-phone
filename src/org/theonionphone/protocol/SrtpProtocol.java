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

import android.util.Log;

public class SrtpProtocol {

	private static final int SALT_SIZE = 14;
	private static final int SRTP_HEADER_SIZE_IN_BYTES = 12;
	private static final int SRTP_AUTHORIZATION_TAG_SIZE_IN_BYTES = 4;
	private int packetSizeInBytes;
	private int payloadSizeInBytes;
	private boolean srtpInitialized = false;
	
	private SrtpWrapper wrapper;
	private SrtpUnwrapper unwrapper;

	public void initiateOutgoingSession(byte[] txKey, byte[] rxKey, Codec codec, InputStream networkInputStream, OutputStream networkOutputStream) {
		packetSizeInBytes = SRTP_HEADER_SIZE_IN_BYTES + roundUpToMultipleOf4(codec.getBytesSize()) + SRTP_AUTHORIZATION_TAG_SIZE_IN_BYTES;
		int rtpPacketSizeInBytes = SRTP_HEADER_SIZE_IN_BYTES + roundUpToMultipleOf4(codec.getBytesSize());
		payloadSizeInBytes = codec.getBytesSize();
		
		byte[] senderSalt = getRandomSalt();
		
		initSender(txKey, senderSalt, codec.getCodecType(), codec.getSamplesSize(), payloadSizeInBytes, rtpPacketSizeInBytes, packetSizeInBytes);
		int senderSsrc = getSsrc();
		
		sendSsrcAndSalt(networkOutputStream, senderSalt, senderSsrc);
		
		DataInputStream networkDataInputStream = new DataInputStream(networkInputStream);
		int receiverSsrc = receiveSsrc(networkDataInputStream);
		byte[] receiverSalt = receiveSalt(networkDataInputStream);
		
		initReceiver(receiverSsrc, rxKey, receiverSalt);
		srtpInitialized = true;
		
		Log.i("keys", "tx key " + printKey(txKey));
		Log.i("keys", "tx salt " + printKey(senderSalt));
		Log.i("keys", "rx key " + printKey(rxKey));
		Log.i("keys", "rx salt " + printKey(receiverSalt));
	}

	private String printKey(byte[] txKey) {
		String key = "";
		for(byte b : txKey) {
			key += Byte.toString(b) + ", ";
		}
		return key;
	}

	public void initiateIncomingSession(byte[] txKey, byte[] rxKey, Codec codec, InputStream networkInputStream, OutputStream networkOutputStream) {
		packetSizeInBytes = SRTP_HEADER_SIZE_IN_BYTES + roundUpToMultipleOf4(codec.getBytesSize()) + SRTP_AUTHORIZATION_TAG_SIZE_IN_BYTES;
		int rtpPacketSizeInBytes = SRTP_HEADER_SIZE_IN_BYTES + roundUpToMultipleOf4(codec.getBytesSize());
		payloadSizeInBytes = codec.getBytesSize();
		
		byte[] senderSalt = getRandomSalt();
		
		DataInputStream networkDataInputStream = new DataInputStream(networkInputStream);
		int receiverSsrc = receiveSsrc(networkDataInputStream);
		byte[] receiverSalt = receiveSalt(networkDataInputStream);
		
		int sth = initSender(txKey, senderSalt, codec.getCodecType(), codec.getSamplesSize(), payloadSizeInBytes, rtpPacketSizeInBytes, packetSizeInBytes);
		int senderSsrc = getSsrc();
		
		sendSsrcAndSalt(networkOutputStream, senderSalt, senderSsrc);
		
		sth = initReceiver(receiverSsrc, rxKey, receiverSalt);
		srtpInitialized = true;

		Log.i("keys", "tx key " + printKey(txKey));
		Log.i("keys", "tx salt " + printKey(senderSalt));
		Log.i("keys", "rx key " + printKey(rxKey));
		Log.i("keys", "rx salt " + printKey(receiverSalt));
	}

	private byte[] receiveSalt(DataInputStream networkInputStream) {
		try {
			byte[] salt = new byte[SALT_SIZE];
			networkInputStream.readFully(salt);
			return salt;
		} catch(IOException e) {
			//TODO some error handling
			return null;
		}
	}

	private int receiveSsrc(DataInputStream networkInputStream) {
		try {
			return networkInputStream.readInt();
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
		wrapper = new SrtpWrapper(inputStream, outputStream, payloadSizeInBytes, packetSizeInBytes, SRTP_HEADER_SIZE_IN_BYTES);
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
		unwrapper = new SrtpUnwrapper(inputStream, outputStream, packetSizeInBytes, payloadSizeInBytes, SRTP_HEADER_SIZE_IN_BYTES);
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
	
	private native int initSender(byte[] keyByteArray, byte[] saltByteArray, int codecType, int sampleCount, int payloadSizeInBytes, int rtpPacketSizeInBytes, int srtpPacketSizeInBytes);
	
	private native int getSsrc();
	
	private native int initReceiver(int ssrc, byte[] keyByteArray, byte[] saltByteArray);
	
	private native byte[] unwrapPacket(byte[] packet);
	
	private native int getHeaderSize();
	
	static {
		System.loadLibrary("srtp");
	}
}
