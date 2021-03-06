package org.theonionphone;

import java.security.Security;

import android.app.Application;
import android.content.Context;

public class TheOnionPhone extends Application {

	private Context context;
	
	private static TheOnionPhone instance;
	
	public static Context getContext() {
		return instance.context;
	}
	
	public static void initializeContext(Context context) {
		instance = new TheOnionPhone();
		instance.setContext(context.getApplicationContext());
	}
	
	private void setContext(Context context) {
		this.context = context;
	}
	
	static {
	    Security.insertProviderAt(new org.spongycastle.jce.provider.BouncyCastleProvider(), 1);
	}
}
