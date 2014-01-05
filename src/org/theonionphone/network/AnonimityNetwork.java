package org.theonionphone.network;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import org.theonionphone.identity.Identity;

public interface AnonimityNetwork {
	
	void startConnectionListener();
	
	void stopConnectionListener();
	
	void handleIncomingConnection(Socket socket);
	
	boolean isReady();
	
	void createIdentity();

	void connectTo(Identity identity);
	
	InputStream getInputStream();
	
	OutputStream getOutputStream();
	
	void disconnect();
}
