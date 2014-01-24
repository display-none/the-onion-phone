package org.theonionphone.network;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import org.theonionphone.common.exceptions.ConnectionException;
import org.theonionphone.identity.Identity;

public interface AnonimityNetwork {
	
	/**
	 * Requests start of connection listener service.
	 */
	void startConnectionListener();
	
	/**
	 * Requests stop of connection listener service.
	 */
	void stopConnectionListener();
	
	/**
	 * Handles incoming connection on given socket.
	 * @param socket socket with incoming connection
	 */
	void handleIncomingConnection(Socket socket);
	
	/**
	 * Returns true if anonimity network is ready to operate.
	 * @return true if anonimity network is ready, false otherwise
	 */
	boolean isReady();
	
	void createIdentity();

	/**
	 * Connects to user identified by given identity.
	 * @param identity identity of a user to connect to
	 * @throws ConnectionException when failed to connect
	 */
	void connectTo(Identity identity);
	
	/**
	 * When connected, returns network input stream
	 * @return network input stream
	 * @throws ConnectionException when not connected
	 */
	InputStream getInputStream();
	
	/**
	 * When connected, returns network output stream
	 * @return network output stream
	 * @throws ConnectionException when not connected
	 */
	OutputStream getOutputStream();
	
	/**
	 * When connected, disconnects.
	 * @throws ConnectionException when not connected
	 */
	void disconnect();
}
