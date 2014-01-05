package org.theonionphone.protocol;

import org.theonionphone.identity.Identity;
import org.theonionphone.utils.locator.LocalService;

public class ProtocolManagementImpl extends LocalService implements ProtocolManagement {

	private TheOnionPhoneProtocol mainProtocol;
	
	public ProtocolManagementImpl() {
		this.mainProtocol = new TheOnionPhoneProtocol();
	}

	@Override
	public void startCall(Identity identity) {
		
	}

	@Override
	public void handleIncomingCall() {
		// TODO Auto-generated method stub
		
	}
	
}
