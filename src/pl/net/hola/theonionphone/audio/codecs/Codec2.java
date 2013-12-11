package pl.net.hola.theonionphone.audio.codecs;

import pl.net.hola.theonionphone.common.exceptions.CodecException;

public class Codec2 implements Codec {
	
	private static final int SAMPLES_SIZE = 160;
	private static final int BYTES_SIZE = 7;
	private int initializedTimes = 0;
	
	private static Codec2 instance = null;
	
	private Codec2() { }
	
	public synchronized static Codec2 getInstance() {
		if(instance == null) {
			instance = new Codec2();
		}
		return instance;
	}
	
	@Override
	public void initialize() {
		if(initializedTimes == 0) {
			codec2Init();
		}
		initializedTimes++;
	}
	
	@Override
	public void release() {
		checkIfInitialized();
		
		initializedTimes--;
		if(initializedTimes == 0) {
			codec2Release();
		}
	}
	
	@Override
	public void encode(short[] input, byte[] output) {
		checkIfInitialized();
		int succeeded = codec2Encode(input, output);
		if(succeeded != 0) {
			throw new CodecException("Library returned error");
		}
	}

	@Override
	public void decode(short[] output, byte[] input) {
		checkIfInitialized();
		int succeeded = codec2Decode(output, input);
		if(succeeded != 0) {
			throw new CodecException("Library returned error");
		}
	}
	
	@Override
	public int getSamplesSize() {
		return SAMPLES_SIZE;
	}
	
	@Override
	public int getBytesSize() {
		return BYTES_SIZE;
	}

	private native int codec2Init();
	
	private native int codec2Encode(short[] input, byte[] output);
	
	private native int codec2Decode(short[] output, byte[] input);
	
	private native void codec2Release();
	
	public void someTest() {
		codec2Init();
		try {
			Thread.sleep(1000);
			
			short[] in = new short[SAMPLES_SIZE];
			for(int i = 0; i < in.length; i++) {
				in[i] = 25;
			}
			byte[] out = new byte[BYTES_SIZE];
			
			codec2Encode(in, out);
			
			for(int i = 0; i < out.length; i++) {
				System.out.println(out[i]);
			}
			
			short[] orig = new short[SAMPLES_SIZE];
			
			codec2Decode(orig, out);
			
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		codec2Release();
	}
	
	private void checkIfInitialized() {
		if(initializedTimes == 0) {
			throw new CodecException("Codec not initialized");
		}
	}
	
	static {
		System.loadLibrary("codec2_codec");
	}
}
