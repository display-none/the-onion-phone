package pl.net.hola.theonionphone.utils.locator;

import android.content.Context;
import pl.net.hola.theonionphone.TheOnionPhone;
import pl.net.hola.theonionphone.audio.AudioManager;
import pl.net.hola.theonionphone.audio.AudioManagerImpl;

public class ServiceLocator {
	
	private static ServiceLocator instance;

	private ServiceHolder<AudioManagerImpl> audioMangerHolder;
	
	private Context context;
	
	private ServiceLocator() {
		context = TheOnionPhone.getContext();
	}
	
	public synchronized AudioManager getAudioManager() {
		if(audioMangerHolder == null) {
			audioMangerHolder = new ServiceHolder<AudioManagerImpl>(context, AudioManagerImpl.class);
		}
		
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return audioMangerHolder.getService();
	}
	
	public static synchronized ServiceLocator getInstance() {
		if(instance == null) {
			instance = new ServiceLocator();
		}
		return instance;
	}
}
