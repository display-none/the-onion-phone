package org.theonionphone.identity;

import java.security.PublicKey;

public class Identity {
	
	private String networkIdentifier;
	private PublicKey publicKey;
	
	public String getNetworkIdentifier() {
		return networkIdentifier;
	}
	
	public void setNetworkIdentifier(String networkIdentifier) {
		this.networkIdentifier = networkIdentifier;
	}
	
	public PublicKey getPublicKey() {
		return publicKey;
	}
	
	public void setPublicKey(PublicKey publicKey) {
		this.publicKey = publicKey;
	}
}
