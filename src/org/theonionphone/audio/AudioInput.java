package org.theonionphone.audio;

import java.io.IOException;
import java.io.InputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;

import org.theonionphone.audio.codecs.Codec;
import org.theonionphone.common.exceptions.AudioManagerException;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder.AudioSource;

public class AudioInput {
	
	private final static int SAMPLE_RATE = 8000;
	private final static int THREE_SECOND_BUFFER_SIZE_IN_BYTES = 7 * (SAMPLE_RATE * 2);
	
	private final Codec codec;
	private final AudioRecord audioRecord;
	private AudioInputWorker worker;
	
	public AudioInput(Codec codec) {
		this.codec = codec;
		this.audioRecord = new AudioRecord(AudioSource.VOICE_COMMUNICATION, SAMPLE_RATE, AudioFormat.CHANNEL_IN_MONO, 
				AudioFormat.ENCODING_PCM_16BIT, THREE_SECOND_BUFFER_SIZE_IN_BYTES);
	}
	
	public InputStream getStream() {
		PipedOutputStream outputStream = new PipedOutputStream();
		createWorker(outputStream);
		return prepareInputStream(outputStream);
	}
	
	public void startSending() {
		checkWorkerNotNull();
		worker.start();
	}

	public void stopSending() {
		checkWorkerNotNull();
		worker.stopAndClose();
		worker = null;
	}

	private void createWorker(PipedOutputStream outputStream) {
		if(worker != null) {
			throw new AudioManagerException("attempt to reinitialize");
		}
		worker = new AudioInputWorker(outputStream, audioRecord, codec);
	}

	private PipedInputStream prepareInputStream(PipedOutputStream outputStream) {
		try {
			return new PipedInputStream(outputStream);
		} catch(IOException e) {
			throw new AudioManagerException("Cannot instantiate stream");
		}
	}
	
	private void checkWorkerNotNull() {
		if(worker == null) {
			throw new AudioManagerException("not initialized");
		}
	}
	
	AudioInputWorker getWorker() {
		return worker;
	}
	
	void setWorker(AudioInputWorker worker) {
		this.worker = worker;
	}
}
