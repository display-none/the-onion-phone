package org.theonionphone.common;

import org.theonionphone.identity.Identity;

public class CallInfo {

	private boolean shouldIntroduce = false;
	private boolean requiresIntroduction = false;
	private Identity identity;
	private byte[] rxKey;
	private byte[] txKey;
	
	public byte[] getRxKey() {
		return rxKey;
	}
	
	public void setRxKey(byte[] rxKey) {
		this.rxKey = rxKey;
	}
	
	public byte[] getTxKey() {
		return txKey;
	}
	
	public void setTxKey(byte[] txKey) {
		this.txKey = txKey;
	}
	
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
