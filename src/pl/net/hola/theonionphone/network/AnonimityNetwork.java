package pl.net.hola.theonionphone.network;

import java.io.InputStream;
import java.io.OutputStream;

import pl.net.hola.theonionphone.common.exceptions.ConnectionException;
import pl.net.hola.theonionphone.identity.Identity;

public interface AnonimityNetwork {
	
	void startConnectionListener();
	
	void stopConnectionListener();
	
	boolean isReady();
	
	void createIdentity();

	void connectTo(Identity identity) throws ConnectionException;
	
	InputStream getInputStream();
	
	OutputStream getOutputStream();
}
