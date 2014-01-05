package org.theonionphone.identity;

public interface IdentityManager {
	
	void createNewIdentity();
	
	void importIdentity();
	
	void exportIdentity();
	
	Identity getIdentity();
}
