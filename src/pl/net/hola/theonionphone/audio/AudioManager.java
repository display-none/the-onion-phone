package pl.net.hola.theonionphone.audio;

import java.io.InputStream;

public interface AudioManager {
	
	InputStream getStream();
	
	void startSending();
	
	void stopSending();
	
	void playStream(InputStream inputStream);
	
	void stopPlaying();
}
