package org.theonionphone.utils.locator;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

/**
 * Base class to all services to be located by ServiceLocator.
 * 
 * Provides binding and returns created service instance for the application to use
 */
public abstract class LocalService extends Service {

	class LocalBinder extends Binder {
		LocalService getService() {
			return LocalService.this;
		}
	}
	
	private Binder binder = new LocalBinder();
	
	@Override
	public IBinder onBind(Intent intent) {
		return binder;
	}

}
