package org.theonionphone.protocol;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.os.Process;
import android.util.Log;

public class Connector extends Thread {

	private final static int PACKET_SIZE = 24;
	private final DataInputStream inputStream;
	private final OutputStream outputStream;
	
	private boolean transmiting = true;
	
	public Connector(InputStream inputStream, OutputStream outputStream) {
		this.inputStream = new DataInputStream(inputStream);
		this.outputStream = outputStream;
	}
	
	@Override
	public void run() {
		Process.setThreadPriority(-14);
		
		byte[] buffer = new byte[PACKET_SIZE];
		
		try {
			while(transmiting) {
				inputStream.readFully(buffer);
				outputStream.write(buffer);
			}
		} catch(IOException e) {
			Log.e("dupa", "dupa", e);
		} finally {
			closeStreams();
		}
	}
	
	public void stopAndClose() {
		transmiting = false;
	}

	private void closeStreams() {
		try {
			inputStream.close();
			outputStream.close();
		} catch (IOException e) { }
	}
}
