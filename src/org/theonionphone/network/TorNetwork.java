package org.theonionphone.network;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import org.theonionphone.common.exceptions.ConnectionException;
import org.theonionphone.identity.Identity;
import org.theonionphone.protocol.ProtocolManagement;
import org.theonionphone.utils.TorServiceUtils;
import org.theonionphone.utils.locator.LocalService;
import org.theonionphone.utils.locator.ServiceLocator;

import android.content.Intent;

public class TorNetwork extends LocalService implements AnonimityNetwork {

	public final static String TOR_BIN_PATH = "/data/data/org.torproject.android/app_bin/tor";
	public static final int HIDDEN_SERVICE_PORT = 5004;	//it's a nice number and it's reserved for RTP media data
	private static final int SOCKET_TIMEOUT = 100000;		//100 seconds
	private boolean listenerStartNeeded = false;
	
	private Socket socket;

	@Override
	public void startConnectionListener() {
		Intent intent = new Intent(this, ConnectionListener.class);
		startService(intent);
	}

	@Override
	public void stopConnectionListener() {
		Intent intent = new Intent(this, ConnectionListener.class);
		stopService(intent);
	}

	@Override
	public boolean isReady() {
		return isOrbotEnabled() && isTransparentProxyEnabled();
	}

	private boolean isOrbotEnabled() {
		int procId = TorServiceUtils.findProcessId(TOR_BIN_PATH);		//that's ugly, maybe could be done better

        return (procId != -1);
	}
	
	private boolean isTransparentProxyEnabled() {
		// TODO implement this
		return true;
	}

	@Override
	public void createIdentity() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void connectTo(Identity identity) {
		try {
			socket = new Socket(identity.getNetworkIdentifier(), HIDDEN_SERVICE_PORT);
			socket.setSoTimeout(SOCKET_TIMEOUT);
			socket.setTcpNoDelay(true);
			
			//connection successful, stop accepting incoming connections
			stopConnectionListener();
			listenerStartNeeded = true;
		} catch(IOException e) {
			//TODO show sth in gui
			throw new ConnectionException("cannot connect to hidden service");
		}
	}

	@Override
	public InputStream getInputStream() {
		checkForNullSocket();
		try {
			return socket.getInputStream();
		} catch (IOException e) {
			throw new ConnectionException("error while getting input stream");
		}
	}

	@Override
	public OutputStream getOutputStream() {
		checkForNullSocket();
		try {
			return socket.getOutputStream();
		} catch (IOException e) {
			throw new ConnectionException("error while getting output stream");
		}
	}
	
	@Override
	public void disconnect() {
		checkForNullSocket();
		try {
			socket.close();
		} catch (IOException e) { }
		socket = null;
		resumeListenerIfEnabled();
	}
	
	private void checkForNullSocket() {
		if(socket == null) {
			throw new ConnectionException("not connected");
		}
	}

	private void resumeListenerIfEnabled() {
		if(listenerStartNeeded) {
			startConnectionListener();
			listenerStartNeeded = false;
		}
	}

	@Override
	public void handleIncomingConnection(Socket incomingConnectionSocket) {
		if(socket == null) {
			listenerStartNeeded = true;
			socket = incomingConnectionSocket;
			ProtocolManagement protocolManagement = ServiceLocator.getInstance().getProtocolManagement();
			protocolManagement.handleIncomingCall();
		}
	}
}
