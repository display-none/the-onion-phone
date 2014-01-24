package org.theonionphone.audio;

import java.io.InputStream;

import org.theonionphone.audio.codecs.Codec;

public interface AudioManager {
	
	/**
	 * Gets a stream to read audio data from. Stream will be filled after calling {@link #startSending()}.
	 * 
	 * Note: all calls initialize new stream. Data will be written only to the last stream.
	 * 
	 * @return stream with audio data
	 */
	InputStream getStream();
	
	/**
	 * Starts writing audio data to stream.
	 */
	void startSending();
	
	/**
	 * Stops writing audio data and closes stream.
	 */
	void stopSending();
	
	/**
	 * Initializes AudioManager with given stream and starts playing audio data from it.
	 * @param inputStream InputStream with audio data to play
	 */
	void playStream(InputStream inputStream);
	
	/**
	 * Stops playing audio data and closes stream.
	 */
	void stopPlaying();
	
	/**
	 * Gets currently used audio codec.
	 * @return audio codec used by AudioManager
	 */
	Codec getCodec();

	void next();
}
