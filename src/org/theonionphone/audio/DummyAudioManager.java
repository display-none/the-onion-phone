package org.theonionphone.audio;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;

import org.theonionphone.audio.codecs.Codec;
import org.theonionphone.audio.codecs.Codec2;
import org.theonionphone.utils.locator.LocalService;

import android.os.Process;

public class DummyAudioManager extends LocalService implements AudioManager {
	
	private PipedOutputStream pos;
	private DataInputStream is;

	@Override
	public InputStream getStream() {
		pos = new PipedOutputStream();
		try {
			return new PipedInputStream(pos);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void startSending() {
		next();
	}

	@Override
	public void stopSending() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void playStream(InputStream inputStream) {
		this.is = new DataInputStream(inputStream);
	}

	@Override
	public void stopPlaying() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Codec getCodec() {
		return Codec2.getInstance();
	}

	@Override
	public void next() {
		(new Thread() {
			public void run() {
				Process.setThreadPriority(-17);
				
				try {
					byte[] buffer = new byte[7];
					while(true) {
						is.readFully(buffer);
						pos.write(buffer);
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			};
		}).start();
	}

}
