package pl.net.hola.theonionphone.protocol;

import pl.net.hola.theonionphone.identity.Identity;
import pl.net.hola.theonionphone.utils.locator.LocalService;

public class ProtocolManagementImpl extends LocalService implements ProtocolManagement {

	private TheOnionPhoneProtocol mainProtocol;
	
	public ProtocolManagementImpl() {
		this.mainProtocol = new TheOnionPhoneProtocol();
	}

	@Override
	public void startCall(Identity identity) {
		
	}
	
}
