package pl.net.hola.theonionphone.audio;

import java.io.InputStream;
import java.io.OutputStream;

import pl.net.hola.theonionphone.audio.codecs.Codec;
import pl.net.hola.theonionphone.audio.codecs.Codec2;
import pl.net.hola.theonionphone.utils.locator.LocalService;

public class AudioManagerImpl extends LocalService implements AudioManager {

	private Codec codec;
	private AudioInput audioInput;
	
	
	public AudioManagerImpl() {
		this.codec = Codec2.getInstance();
		this.audioInput = new AudioInput(codec);
	}
	
	@Override
	public boolean isReady() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public InputStream getStream() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void playStream(OutputStream outputStream) {
		// TODO Auto-generated method stub

	}

	@Override
	public void stopSending() {
		// TODO Auto-generated method stub

	}

	@Override
	public void stopPlaying() {
		// TODO Auto-generated method stub

	}
	
}
