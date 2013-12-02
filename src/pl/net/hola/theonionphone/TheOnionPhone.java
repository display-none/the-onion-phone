package pl.net.hola.theonionphone;

import android.app.Application;
import android.content.Context;

public class TheOnionPhone extends Application {

	private static TheOnionPhone instance;
	
	public static Context getContext() {
		if(instance == null) {
			instance = new TheOnionPhone();
		}
		return instance.getApplicationContext();
	}
}
