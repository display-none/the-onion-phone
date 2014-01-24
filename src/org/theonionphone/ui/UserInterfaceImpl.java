package org.theonionphone.ui;

import org.theonionphone.TheOnionPhone;
import org.theonionphone.common.CallInfo;
import org.theonionphone.protocol.ProtocolManagement;
import org.theonionphone.utils.locator.LocalService;
import org.theonionphone.utils.locator.ServiceLocator;

public class UserInterfaceImpl extends LocalService implements UserInterface {

	private CallInfo callInfo;
	
	@Override
	public void handleIncomingCall(CallInfo callInfo) {
		this.callInfo = callInfo;
	}

	@Override
	public void handleIncomingMessage() {
		// TODO Auto-generated method stub

	}

	@Override
	public void acceptCall() {
		ProtocolManagement protocolManagement = ServiceLocator.getInstance().getProtocolManagement();
		protocolManagement.acceptCall(callInfo);
	}

}
