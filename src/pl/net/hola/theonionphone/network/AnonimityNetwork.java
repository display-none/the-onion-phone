package pl.net.hola.theonionphone.network;

public interface AnonimityNetwork {
	
	void startConnectionListener();
	
	void stopConnectionListener();
	
	boolean isReady();
	
	void createIdentity();
}
