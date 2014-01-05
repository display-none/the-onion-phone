package org.theonionphone.protocol;

import java.io.InputStream;
import java.io.OutputStream;

public class TheOnionPhoneProtocol {

	private static final int KEY_SIZE = 16;

	public void initiateSession(InputStream inputStream, OutputStream outputStream) {
		sendAndReceiveHello();
	}

	private void sendAndReceiveHello() {
		// TODO Auto-generated method stub
		
	}

	public byte[] getSessionKey() {
		byte[] key = new byte[KEY_SIZE];
		return key;
	}

}
