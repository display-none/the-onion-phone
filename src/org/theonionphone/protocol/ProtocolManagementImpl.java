package org.theonionphone.protocol;

import org.theonionphone.common.CallInfo;
import org.theonionphone.utils.locator.LocalService;

public class ProtocolManagementImpl extends LocalService implements ProtocolManagement {

	@Override
	public void startCall(CallInfo callInfo) {
		TheOnionPhoneProtocol mainProtocol = new TheOnionPhoneProtocol();
		SrtpProtocol srtpProtocol = new SrtpProtocol();
		CallHandler callHandler = new CallHandler(mainProtocol, srtpProtocol);
		callHandler.startCall(callInfo);
	}

	@Override
	public void handleIncomingCall() {
		TheOnionPhoneProtocol mainProtocol = new TheOnionPhoneProtocol();
		SrtpProtocol srtpProtocol = new SrtpProtocol();
		CallHandler callHandler = new CallHandler(mainProtocol, srtpProtocol);
		callHandler.handleIncomingCall();
	}
	
}
