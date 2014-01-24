package org.theonionphone.network;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import org.theonionphone.utils.locator.ServiceLocator;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.Process;

public class ConnectionListener extends Service implements Runnable {

	private ServerSocket serverSocket;
	
	@Override
	public void run() {
		Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);
		
		try {
			serverSocket = new ServerSocket(TorNetwork.HIDDEN_SERVICE_PORT);
			Socket socket = serverSocket.accept();
			
			AnonimityNetwork torNetwork = ServiceLocator.getInstance().getAnonimityNetwork();
			torNetwork.handleIncomingConnection(socket);
		} catch (IOException e) {
			// TODO handle it
		}
		stopSelf();
	}
	
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	
	@Override
	public void onCreate() {
		(new Thread(this)).start();
	}
	
	@Override
	public void onDestroy() {
		try {
			if(serverSocket != null) {
				serverSocket.close();
			}
		} catch (IOException e) { }
	}
}
