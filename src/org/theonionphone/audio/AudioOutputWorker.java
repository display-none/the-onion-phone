package org.theonionphone.audio;

import java.io.IOException;
import java.io.InputStream;

import org.theonionphone.audio.codecs.Codec;

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
			//we might want to implement some mechanism to drop a packet when time between send time on 
			//application output is exceeding duration of audio encoded in one packet
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
		} catch (IOException e) { 
			running = false;
		}
	}

	private void closeStream() {
		try {
			inputStream.close();
		} catch (IOException e) { }
	}
}
