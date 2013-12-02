package pl.net.hola.theonionphone.identity;

public interface IdentityManager {
	
	void createNewIdentity();
	
	void importIdentity();
	
	void exportIdentity();
	
	Identity getIdentity();
}
