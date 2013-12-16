package pl.net.hola.theonionphone.protocol;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class Connector extends Thread {

	private final static int PACKET_SIZE = 48;
	private final InputStream inputStream;
	private final OutputStream outputStream;
	
	private boolean transmiting = true;
	
	public Connector(InputStream inputStream, OutputStream outputStream) {
		this.inputStream = inputStream;
		this.outputStream = outputStream;
	}
	
	@Override
	public void run() {
		byte[] buffer = new byte[PACKET_SIZE];
		
		try {
			while(transmiting) {
				inputStream.read(buffer);
				outputStream.write(buffer);
			}
		} catch(IOException e) {
			//would be nice to log or print sth
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
