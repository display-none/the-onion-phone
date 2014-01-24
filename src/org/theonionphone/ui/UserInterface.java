package org.theonionphone.ui;

import org.theonionphone.common.CallInfo;

public interface UserInterface {

	void handleIncomingCall(CallInfo callInfo);
	
	void handleIncomingMessage();
	
	void acceptCall();
}
