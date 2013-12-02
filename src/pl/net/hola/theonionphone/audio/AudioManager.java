package pl.net.hola.theonionphone.audio;

import java.io.InputStream;
import java.io.OutputStream;

public interface AudioManager {
	
	boolean isReady();
	
	InputStream getStream();
	
	void playStream(OutputStream outputStream);
	
	void stopSending();
	
	void stopPlaying();
}
