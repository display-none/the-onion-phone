package org.theonionphone.protocol;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.theonionphone.common.exceptions.MainProtocolException;

import android.os.Process;

public class SrtpWrapper extends Thread {
	
	private DataInputStream inputStream;
	private OutputStream outputStream;
	private final int payloadSize;
	private final int packetSize;
	private final int headerSize;
	
	private boolean running = true;

	public SrtpWrapper(InputStream inputStream, OutputStream outputStream, int payloadSize, int packetSize, int headerSize) {
		this.inputStream = new DataInputStream(inputStream);
		this.outputStream = outputStream;
		this.payloadSize = payloadSize;
		this.packetSize = packetSize;
		this.headerSize = headerSize;
	}
	
	@Override
	public void run() {
		Process.setThreadPriority(Process.THREAD_PRIORITY_AUDIO);
		
		byte[] packet = new byte[packetSize];
		
		try {
			while(running) {
				readPayloadIntoPacket(packet);
				wrapPayload(packet);
				writePacket(packet);
			}
		} finally {
			closeStreams();
		}
	}
	
	public void stopAndClose() {
		running = false;
	}
	
	private void readPayloadIntoPacket(byte[] packet) {
		try {
			inputStream.readFully(packet, headerSize, payloadSize);
		} catch (IOException e) {
			throw new MainProtocolException("cannot read payload", e);
		}
	}
	
	private void wrapPayload(byte[] packet) {
		int errorCode = wrapPayloadNative(packet);
		if(errorCode != SrtpErrorCode.OK) {
			throw new MainProtocolException("native srtp wrapper exited with error code: " + errorCode);
		}
	}

	private void writePacket(byte[] packet) {
		try {
			outputStream.write(packet);
		} catch (IOException e) {
			throw new MainProtocolException("cannot write packet", e);
		}
	}
	
	private void closeStreams() {
		try {
			inputStream.close();
			outputStream.close();
		} catch (IOException e) { }
	}
	
	private native int wrapPayloadNative(byte[] packet);
	
}
