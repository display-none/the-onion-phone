package pl.net.hola.theonionphone.utils.locator;

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
		
		context.bindService(new Intent(context, clazz), connection, Context.BIND_AUTO_CREATE);
	}
	
	T getService() {
		return service;
	}
	
	void cleanUp() {
		context.unbindService(connection);
	}
}
