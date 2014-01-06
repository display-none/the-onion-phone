package org.theonionphone.identity;

import java.security.KeyPair;

public interface IdentityManager {
	
	void createNewIdentity();
	
	void importIdentity();
	
	void exportIdentity();
	
	Identity getIdentity();
	
	KeyPair getMyKeys();
}
