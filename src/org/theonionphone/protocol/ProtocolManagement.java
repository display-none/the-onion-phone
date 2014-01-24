package org.theonionphone.protocol;

import org.theonionphone.common.CallInfo;

public interface ProtocolManagement {

	/**
	 * Start call specified by call info
	 * @param callInfo info about the call
	 */
	void startCall(CallInfo callInfo);

	/**
	 * Handle incoming call
	 */
	void handleIncomingCall();
	
	/**
	 * End currently ongoing call
	 */
	void endCall();
	
	void acceptCall(CallInfo callInfo);
}
