package org.theonionphone.identity;

public class Identity {
	
	private final String networkIdentifier;
	
	public Identity(String networkIdentifier) {
		this.networkIdentifier = networkIdentifier;
	}

	public String getNetworkIdentifier() {
		return networkIdentifier;
	}

}
