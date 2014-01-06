package org.theonionphone.common;

import org.theonionphone.identity.Identity;

public class CallInfo {

	private boolean shouldIntroduce = false;
	private boolean requiresIntroduction = false;
	private Identity identity;
	
	public Identity getIdentity() {
		return identity;
	}
	
	public void setIdentity(Identity identity) {
		this.identity = identity;
	}
	
	public boolean getShouldIntroduce() {
		return shouldIntroduce;
	}
	
	public void setShouldIntroduce(boolean shouldIntroduce) {
		this.shouldIntroduce = shouldIntroduce;
	}
	
	public boolean getRequiresIntroduction() {
		return requiresIntroduction;
	}
	
	public void setRequiresIntroduction(boolean requiresIntroduction) {
		this.requiresIntroduction = requiresIntroduction;
	}
}
