package org.theonionphone.audio;

import java.io.InputStream;

import org.theonionphone.audio.codecs.Codec;
import org.theonionphone.audio.codecs.Codec2;
import org.theonionphone.utils.locator.LocalService;

public class AudioManagerImpl extends LocalService implements AudioManager {

	private final Codec codec;
	private final AudioInput audioInput;
	private final AudioOutput audioOutput;
	
	public AudioManagerImpl() {
		this.codec = Codec2.getInstance();
		this.audioInput = new AudioInput(codec);
		this.audioOutput = new AudioOutput(codec);
	}
	
	@Override
	public InputStream getStream() {
		return audioInput.getStream();
	}
	
	@Override
	public void startSending() {
		audioInput.startSending();
	}
	
	@Override
	public void stopSending() {
		audioInput.stopSending();
	}

	@Override
	public void playStream(InputStream inputStream) {
		audioOutput.playStream(inputStream);
	}

	@Override
	public void stopPlaying() {
		audioOutput.stopPlaying();
	}

	@Override
	public Codec getCodec() {
		return codec;
	}

	@Override
	public void next() {
		// TODO Auto-generated method stub
		
	}
}
