package org.theonionphone.audio;

import java.io.InputStream;

import org.theonionphone.audio.codecs.Codec;

public interface AudioManager {
	
	InputStream getStream();
	
	void startSending();
	
	void stopSending();
	
	void playStream(InputStream inputStream);
	
	void stopPlaying();
	
	Codec getCodec();
}
