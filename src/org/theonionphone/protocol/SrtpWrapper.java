package org.theonionphone.protocol;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class SrtpWrapper extends Thread {
	
	private InputStream inputStream;
	private OutputStream outputStream;
	private int payloadSize;
	
	private boolean running = true;

	public SrtpWrapper(InputStream inputStream, OutputStream outputStream, int payloadSize) {
		this.inputStream = inputStream;
		this.outputStream = outputStream;
		this.payloadSize = payloadSize;
	}
	
	@Override
	public void run() {
		
		byte[] payload = new byte[payloadSize];
		byte[] packet;
		
		while(running) {
			readPayload(payload);
			packet = wrapPayload(payload);
			writePacket(packet);
		}
		
		closeStreams();
	}
	
	public void stopAndClose() {
		running = false;
	}
	
	private void readPayload(byte[] payload) {
		try {
			inputStream.read(payload);
		} catch (IOException e) {
			//TODO handle it
		}
	}

	private void writePacket(byte[] packet) {
		try {
			outputStream.write(packet);
		} catch (IOException e) {
			// TODO handle it
		}
	}
	
	private void closeStreams() {
		try {
			inputStream.close();
			outputStream.close();
		} catch (IOException e) { }
	}
	
	private native byte[] wrapPayload(byte[] payload);
	
}
