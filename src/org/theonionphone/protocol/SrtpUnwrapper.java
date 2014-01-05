package org.theonionphone.protocol;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class SrtpUnwrapper extends Thread {

	private InputStream inputStream;
	private OutputStream outputStream;
	private int packetSize;
	
	private boolean running = true;

	public SrtpUnwrapper(InputStream inputStream, OutputStream outputStream, int packetSize) {
		this.inputStream = inputStream;
		this.outputStream = outputStream;
		this.packetSize = packetSize;
	}
	
	@Override
	public void run() {
		
		byte[] packet = new byte[packetSize];
		byte[] payload;
		
		while(running) {
			readPacket(packet);
			payload = unwrapPacket(packet);
			writePayload(payload);
		}
		
		closeStreams();
	}
	
	public void stopAndClose() {
		running = false;
	}
	
	private void readPacket(byte[] packet) {
		try {
			inputStream.read(packet);
		} catch (IOException e) {
			//TODO handle it
		}
	}

	private void writePayload(byte[] payload) {
		try {
			outputStream.write(payload);
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
	
	private native byte[] unwrapPacket(byte[] packet);
	
}
