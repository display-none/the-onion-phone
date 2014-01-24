package org.theonionphone.audio;

import java.io.InputStream;

import org.theonionphone.audio.codecs.Codec;
import org.theonionphone.common.exceptions.AudioManagerException;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;

public class AudioOutput {

	private final static int SAMPLE_RATE = 8000;
	private final static int TEN_SECOND_BUFFER_SIZE_IN_BYTES = 2 * (SAMPLE_RATE * 2);
	
	private final Codec codec;
	private final AudioTrack audioTrack;
	private AudioOutputWorker worker;
	
	public AudioOutput(Codec codec) {
		this.codec = codec;
		this.audioTrack = new AudioTrack(AudioManager.STREAM_VOICE_CALL, SAMPLE_RATE, AudioFormat.CHANNEL_OUT_MONO, AudioFormat.ENCODING_PCM_16BIT, TEN_SECOND_BUFFER_SIZE_IN_BYTES, AudioTrack.MODE_STREAM);
	}
	
	public void playStream(InputStream inputStream) {
		worker = new AudioOutputWorker(inputStream, audioTrack, codec);
		worker.start();
	}
	
	public void stopPlaying() {
		checkWorkerNotNull();
		worker.stopAndClose();
		worker = null;
	}

	private void checkWorkerNotNull() {
		if(worker == null) {
			throw new AudioManagerException("not initialized");
		}
	}
}
