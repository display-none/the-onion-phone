package org.theonionphone.protocol;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;

import org.theonionphone.audio.AudioManager;
import org.theonionphone.common.exceptions.CallException;
import org.theonionphone.common.exceptions.ConnectionException;
import org.theonionphone.identity.Identity;
import org.theonionphone.network.AnonimityNetwork;
import org.theonionphone.utils.locator.ServiceLocator;

public class CallHandler {

	private final TheOnionPhoneProtocol theOnionPhoneProtocol;
	private final SrtpProtocol srtpProtocol;
	
	private final AudioManager audioManager;
	private final AnonimityNetwork anonimityNetwork;
	
	private Connector applicationToNetworkConnector;
	private Connector networkToApplicationConnector;
	
	private boolean callOngoing = false;
	
	public CallHandler(TheOnionPhoneProtocol theOnionPhoneProtocol, SrtpProtocol srtpProtocol) {
		this.theOnionPhoneProtocol = theOnionPhoneProtocol;
		this.srtpProtocol = srtpProtocol;
		
		this.audioManager = ServiceLocator.getInstance().getAudioManager();
		this.anonimityNetwork = ServiceLocator.getInstance().getAnonimityNetwork();
	}
	
	public void startCall(Identity identity) {
		checkIfCallIsNotOngoing();
		boolean connected = connectTo(identity);
		if(connected) {
			InputStream networkInputStream = anonimityNetwork.getInputStream();
			OutputStream networkOutputStream = anonimityNetwork.getOutputStream();
			
			theOnionPhoneProtocol.initiateSession(networkInputStream, networkOutputStream);
			
			srtpProtocol.initiateOutgoingSession(theOnionPhoneProtocol.getSessionKey(), audioManager.getCodec(), networkInputStream, networkOutputStream);
			
			InputStream applicationInputStream = getApplicationInputStream();
			OutputStream applicationOutputStream = getApplicationOutputStream();
			
			applicationToNetworkConnector = new Connector(applicationInputStream, networkOutputStream);
			networkToApplicationConnector = new Connector(networkInputStream, applicationOutputStream);
			
			audioManager.startSending();
			
			applicationToNetworkConnector.start();
			networkToApplicationConnector.start();
			
			callOngoing = true;
		}
	}
	
	public void endCall() {
		checkIfCallIsOngoing();
		applicationToNetworkConnector.stopAndClose();
		networkToApplicationConnector.stopAndClose();
		applicationToNetworkConnector = null;
		networkToApplicationConnector = null;
		callOngoing = false;
	}
	
	public void endCallWithError(String msg) {
		endCall();
		//display msg
	}

	private OutputStream getApplicationOutputStream() {
		try {
			PipedOutputStream applicationOutputStream = new PipedOutputStream();
			audioManager.playStream(srtpProtocol.unwrapStream(new PipedInputStream(applicationOutputStream)));
			return applicationOutputStream;
		} catch(IOException e) {
			throw new CallException("Could not get application output stream", e);
		}
	}

	private InputStream getApplicationInputStream() {
		return srtpProtocol.wrapStream(audioManager.getStream());
	}

	private boolean connectTo(Identity identity) {
		try {
			anonimityNetwork.connectTo(identity);
		} catch(ConnectionException e) {
			//display sth
			return false;
		}
		return true;
	}
	
	private void checkIfCallIsOngoing() {
		if(callOngoing == false || applicationToNetworkConnector == null || networkToApplicationConnector == null) {
			throw new CallException("There is no ongoing call to end");
		}
	}
	
	private void checkIfCallIsNotOngoing() {
		if(callOngoing == true || applicationToNetworkConnector != null || networkToApplicationConnector != null) {
			throw new CallException("Cannot start a call. There is already an ongoing call");
		}
	}
}
