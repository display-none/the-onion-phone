package org.theonionphone.protocol;

import org.theonionphone.common.CallInfo;

public interface ProtocolManagement {

	void startCall(CallInfo callInfo);

	void handleIncomingCall();
	
}
