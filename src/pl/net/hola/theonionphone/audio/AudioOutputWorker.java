package pl.net.hola.theonionphone.audio;

import java.io.IOException;
import java.io.InputStream;
import pl.net.hola.theonionphone.audio.codecs.Codec;
import android.media.AudioTrack;

public class AudioOutputWorker extends Thread {

	private InputStream inputStream;
	private AudioTrack audioTrack;
	private Codec codec;
	
	private boolean running = true;
	
	public AudioOutputWorker(InputStream inputStream, AudioTrack audioTrack, Codec codec) {
		this.inputStream = inputStream;
		this.audioTrack = audioTrack;
		this.codec = codec;
	}

	@Override
	public void run() {
		short[] samples = new short[codec.getSamplesSize()];
		byte[] bytes = new byte[codec.getBytesSize()];
		
		codec.initialize();
		audioTrack.play();
		
		while(running) {
			readFromStream(bytes);
			codec.decode(samples, bytes);
			audioTrack.write(samples, 0, samples.length);
		}
		
		audioTrack.stop();
		codec.release();
		closeStream();
	}
	
	public void stopAndClose() {
		running = false;
	}

	private void readFromStream(byte[] bytes) {
		try {
			inputStream.read(bytes);
		} catch (IOException e) { }
	}

	private void closeStream() {
		try {
			inputStream.close();
		} catch (IOException e) { }
	}
}
