package org.theonionphone.audio;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.theonionphone.audio.codecs.Codec;

import android.media.AudioTrack;
import android.os.Process;
import android.util.Log;

public class AudioOutputWorker extends Thread {

	private DataInputStream inputStream;
	private AudioTrack audioTrack;
	private Codec codec;
	
	private boolean running = true;
	
	public AudioOutputWorker(InputStream inputStream, AudioTrack audioTrack, Codec codec) {
		this.inputStream = new DataInputStream(inputStream);
		this.audioTrack = audioTrack;
		this.codec = codec;
	}

	@Override
	public void run() {
		Process.setThreadPriority(-17);
		
		short[] samples = new short[codec.getSamplesSize()];
		byte[] bytes = new byte[codec.getBytesSize()];
		
		codec.initialize();
		audioTrack.play();
		
		int i = 1;
		while(running) {
			//we might want to implement some mechanism to drop a packet when time between send time on 
			//application output is exceeding duration of audio encoded in one packet
			readFromStream(bytes);
			codec.decode(samples, bytes);
			Log.i("packet", "recv " + i++);
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
			inputStream.readFully(bytes);
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
