package org.theonionphone.utils.locator;

import org.theonionphone.common.exceptions.ServiceLocatorException;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;

public class ServiceHolder<T extends LocalService> {

	private T service;
	private final Context context;
	
	private ServiceConnection connection = new ServiceConnection() {
		
		@Override
		public void onServiceDisconnected(ComponentName name) {
			service = null;
		}
		
		@Override
		public void onServiceConnected(ComponentName name, IBinder binder) {
			service = (T) ((T.LocalBinder) binder).getService();
		}
	};
	
	public ServiceHolder(Context context, Class<T> clazz) {
		this.context = context;
		
		boolean created = context.bindService(new Intent(context, clazz), connection, Context.BIND_AUTO_CREATE);
		if(!created) {
			throw new ServiceLocatorException("Service for class " + clazz.getName() + " cannot be created");
		}
	}
	
	T getService() {
		if(service == null) {
			tryToDealWithNullService();
		}
		return service;
	}
	
	void cleanUp() {
		context.unbindService(connection);
	}
	
	private void tryToDealWithNullService() {
		try {
			Thread.sleep(14000);
		} catch (InterruptedException e) { }
		if(service == null) {
			throw new ServiceLocatorException("No service was bound");
		}
	}
}
