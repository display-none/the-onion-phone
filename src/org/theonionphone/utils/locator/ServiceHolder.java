package org.theonionphone.utils.locator;

import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

import org.theonionphone.common.exceptions.ServiceLocatorException;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;

public class ServiceHolder<T extends LocalService> {

	private static final long SERVICE_CREATION_TIMEOUT_SECONDS = 10;
	private Semaphore serviceCreationSemaphore = new Semaphore(0);
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
			serviceCreationSemaphore.release();
		}
	};
	
	public ServiceHolder(final Context context, final Class<T> clazz) {
		this.context = context;
		
//		(new Thread() {
//			public void run() {
				boolean created = context.bindService(new Intent(context, clazz), connection, Context.BIND_AUTO_CREATE);
				if(!created) {
					throw new ServiceLocatorException("Service for class " + clazz.getName() + " cannot be created");
				}
//			};
//		}).start();
		
	}
	
	T getService() {
		if(service == null) {
			tryToDealWithNullService();
		}
		return service;
	}
	
	void cleanUp() {
		context.unbindService(connection);
		serviceCreationSemaphore.drainPermits();
	}
	
	private void tryToDealWithNullService() {
		try {
			serviceCreationSemaphore.tryAcquire(1, SERVICE_CREATION_TIMEOUT_SECONDS, TimeUnit.SECONDS);
		} catch (InterruptedException e) { }
		if(service == null) {
			throw new ServiceLocatorException("No service was bound");
		}
	}
}
