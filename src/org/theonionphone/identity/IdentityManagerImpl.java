package org.theonionphone.identity;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;

import org.theonionphone.utils.locator.LocalService;

public class IdentityManagerImpl extends LocalService implements IdentityManager {

	private KeyPair keypair;
	
	@Override
	public void createNewIdentity() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void importIdentity() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void exportIdentity() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Identity getIdentity() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public KeyPair getMyKeys() {
		if(keypair == null) {
			try {
				keypair = KeyPairGenerator.getInstance("RSA").generateKeyPair();
			} catch (NoSuchAlgorithmException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return keypair;
	}

}
