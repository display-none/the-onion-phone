package pl.net.hola.theonionphone.audio;

import java.io.IOException;
import java.io.OutputStream;

import pl.net.hola.theonionphone.audio.codecs.Codec;
import android.media.AudioRecord;

public class AudioInputWorker extends Thread {
	
	private OutputStream outputStream;
	private AudioRecord audioRecord;
	private Codec codec;
	
	private boolean running = true;
	
	public AudioInputWorker(OutputStream outputStream, AudioRecord audioRecord, Codec codec) {
		this.outputStream = outputStream;
		this.audioRecord = audioRecord;
		this.codec = codec;
	}

	@Override
	public void run() {
		short[] samples = new short[codec.getSamplesSize()];
		byte[] bytes = new byte[codec.getBytesSize()];
		
		codec.initialize();
		audioRecord.startRecording();
		
		while(running) {
			audioRecord.read(samples, 0, samples.length);
			codec.encode(samples, bytes);
			writeToStream(bytes);
		}
		
		audioRecord.stop();
		codec.release();
		closeStream();
	}
	
	public void stopAndClose() {
		running = false;
	}

	private void writeToStream(byte[] bytes) {
		try {
			outputStream.write(bytes);
		} catch (IOException e) { }
	}

	private void closeStream() {
		try {
			outputStream.close();
		} catch (IOException e) { }
	}
}