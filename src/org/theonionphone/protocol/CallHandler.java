package org.theonionphone.protocol;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;

import org.theonionphone.audio.AudioManager;
import org.theonionphone.common.CallInfo;
import org.theonionphone.common.exceptions.CallException;
import org.theonionphone.common.exceptions.ConnectionException;
import org.theonionphone.identity.Identity;
import org.theonionphone.network.AnonimityNetwork;
import org.theonionphone.ui.UserInterface;
import org.theonionphone.utils.locator.ServiceLocator;

import android.util.Log;

public class CallHandler {

	private final TheOnionPhoneProtocol theOnionPhoneProtocol;
	private final SrtpProtocol srtpProtocol;
	
	private final AudioManager audioManager;
	private final AnonimityNetwork anonimityNetwork;
	private final UserInterface userInterface;
	
	private Connector applicationToNetworkConnector;
	private Connector networkToApplicationConnector;
	
	private CallStatus callStatus = CallStatus.NO_CALL;
	
	public CallHandler(TheOnionPhoneProtocol theOnionPhoneProtocol, SrtpProtocol srtpProtocol) {
		this.theOnionPhoneProtocol = theOnionPhoneProtocol;
		this.srtpProtocol = srtpProtocol;
		
		this.audioManager = ServiceLocator.getInstance().getAudioManager();
		this.anonimityNetwork = ServiceLocator.getInstance().getAnonimityNetwork();
		this.userInterface = ServiceLocator.getInstance().getUserInterface();
	}
	
	public void startCall(CallInfo callInfo) {
		assertNoCall();
		boolean connected = connectTo(callInfo.getIdentity());
		if(connected) {
			InputStream networkInputStream = anonimityNetwork.getInputStream();
			OutputStream networkOutputStream = anonimityNetwork.getOutputStream();
			
			theOnionPhoneProtocol.initiateOutgoingSession(callInfo, networkInputStream, networkOutputStream);
			
			srtpProtocol.initiateOutgoingSession(callInfo.getTxKey(), callInfo.getRxKey(), audioManager.getCodec(), networkInputStream, networkOutputStream);
			
			InputStream applicationInputStream = getApplicationInputStream();
			OutputStream applicationOutputStream = getApplicationOutputStream();
			
			applicationToNetworkConnector = new Connector(applicationInputStream, networkOutputStream);
			networkToApplicationConnector = new Connector(networkInputStream, applicationOutputStream);
			
			audioManager.startSending();
			
			applicationToNetworkConnector.start();
			networkToApplicationConnector.start();
			
			callStatus = CallStatus.ONGOING;
		}
	}
	
	public void handleIncomingCall() {
		InputStream networkInputStream = anonimityNetwork.getInputStream();
		OutputStream networkOutputStream = anonimityNetwork.getOutputStream();
		
		CallInfo callInfo = theOnionPhoneProtocol.initiateIncomingSession(networkInputStream, networkOutputStream);
		
		callStatus = CallStatus.INITIALIZED;
		
		userInterface.handleIncomingCall(callInfo);
		Log.i("the-onion-phone", "waiting for accept");
		userInterface.acceptCall();
	}
	
	public void acceptCall(CallInfo callInfo) {
		assertCallIsInitialized();
		
		InputStream networkInputStream = anonimityNetwork.getInputStream();
		OutputStream networkOutputStream = anonimityNetwork.getOutputStream();
		
		theOnionPhoneProtocol.acceptCall(callInfo, networkInputStream, networkOutputStream);
		
		srtpProtocol.initiateIncomingSession(callInfo.getTxKey(), callInfo.getRxKey(), audioManager.getCodec(), networkInputStream, networkOutputStream);
		
		InputStream applicationInputStream = getApplicationInputStream();
		OutputStream applicationOutputStream = getApplicationOutputStream();
		
		applicationToNetworkConnector = new Connector(applicationInputStream, networkOutputStream);
		networkToApplicationConnector = new Connector(networkInputStream, applicationOutputStream);
		
		audioManager.startSending();
		
		applicationToNetworkConnector.start();
		networkToApplicationConnector.start();
		
		callStatus = CallStatus.ONGOING;
	}
	
	public void endCall() {
		assertCallIsOngoing();
		applicationToNetworkConnector.stopAndClose();
		networkToApplicationConnector.stopAndClose();
		applicationToNetworkConnector = null;
		networkToApplicationConnector = null;
		callStatus = CallStatus.NO_CALL;
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
	
	private void assertCallIsOngoing() {
		if(callStatus != CallStatus.ONGOING || applicationToNetworkConnector == null || networkToApplicationConnector == null) {
			throw new CallException("There is no ongoing call to end");
		}
	}
	
	private void assertCallIsInitialized() {
		if(callStatus != CallStatus.INITIALIZED) {
			throw new CallException("no call was initialized");
		}
	}
	
	private void assertNoCall() {
		if(callStatus != CallStatus.NO_CALL || applicationToNetworkConnector != null || networkToApplicationConnector != null) {
			throw new CallException("Cannot start a call. There is already an ongoing call");
		}
	}
	
	private enum CallStatus {
		NO_CALL, INITIALIZED, ONGOING
	}
}
