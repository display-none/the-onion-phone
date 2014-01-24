package org.theonionphone.protocol;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.theonionphone.common.exceptions.MainProtocolException;

import android.os.Process;

public class SrtpUnwrapper extends Thread {

	private DataInputStream inputStream;
	private OutputStream outputStream;
	private int packetSize;
	private int payloadSize;
	private int headerSize;
	
	private boolean running = true;

	public SrtpUnwrapper(InputStream inputStream, OutputStream outputStream, int packetSize, int payloadSize, int headerSize) {
		this.inputStream = new DataInputStream(inputStream);
		this.outputStream = outputStream;
		this.packetSize = packetSize;
		this.payloadSize = payloadSize;
		this.headerSize = headerSize;
	}
	
	@Override
	public void run() {
		Process.setThreadPriority(Process.THREAD_PRIORITY_AUDIO);
		
		byte[] packet = new byte[packetSize];
		
		try {
			while(running) {
				readPacket(packet);
				unwrapPacket(packet);
				writePayload(packet);
			}
		} finally {
			closeStreams();
		}
	}
	
	public void stopAndClose() {
		running = false;
	}
	
	private void readPacket(byte[] packet) {
		try {
			inputStream.readFully(packet);
		} catch (IOException e) {
			throw new MainProtocolException("cannot read packet", e);
		}
	}
	
	private void unwrapPacket(byte[] packet) {
		int errorCode = unwrapPacketNative(packet);
		if(errorCode != SrtpErrorCode.OK) {
			throw new MainProtocolException("native srtp unwrapper exited with error code: " + errorCode);
		}
	}

	private void writePayload(byte[] packet) {
		try {
			outputStream.write(packet, headerSize, payloadSize);
		} catch (IOException e) {
			throw new MainProtocolException("cannot write payload", e);
		}
	}
	
	private void closeStreams() {
		try {
			inputStream.close();
			outputStream.close();
		} catch (IOException e) { }
	}
	
	private native int unwrapPacketNative(byte[] packet);
	
}
