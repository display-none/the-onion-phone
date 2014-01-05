package org.theonionphone.audio.codecs;

public interface Codec {

	void initialize();
	
	void release();
	
	void encode(short[] input, byte[] output);
	
	void decode(short[] output, byte[] input);
	
	int getSamplesSize();
	
	int getBytesSize();
	
	/**
	 * Returns RTP payload type
	 */
	int getCodecType();
}
