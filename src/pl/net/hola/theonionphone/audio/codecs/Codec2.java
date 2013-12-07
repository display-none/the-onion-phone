package pl.net.hola.theonionphone.audio.codecs;

public class Codec2 {
	
	private static final int CODEC2_SAMPLES_PER_FRAME = 160;
	private static final int BYTES_SIZE = 7;

	private native int init();
	
	private native int encode(short[] input, byte[] output);
	
	private native int decode(short[] output, byte[] input);
	
	private native void release();
	
	public Codec2() {
		init();
		try {
			Thread.sleep(1000);
			
			short[] in = new short[CODEC2_SAMPLES_PER_FRAME];
			for(int i = 0; i < in.length; i++) {
				in[i] = 25;
			}
			byte[] out = new byte[BYTES_SIZE];
			
			encode(in, out);
			
			for(int i = 0; i < out.length; i++) {
				System.out.println(out[i]);
			}
			
			short[] orig = new short[CODEC2_SAMPLES_PER_FRAME];
			
			decode(orig, out);
			
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		release();
	}
	
	
	static {
		System.loadLibrary("codec2_codec");
	}
}
