package org.theonionphone.protocol;

import org.theonionphone.identity.Identity;

public interface ProtocolManagement {

	void startCall(Identity identity);

	void handleIncomingCall();
	
}
