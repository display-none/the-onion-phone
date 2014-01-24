package org.theonionphone.protocol;

import static org.theonionphone.utils.ProtocolUtils.*;

import java.io.DataInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.KeyAgreement;
import org.theonionphone.common.CallInfo;
import org.theonionphone.common.exceptions.MainProtocolException;

public class KeyExchangeHelper {

	private static final int DH_KEYSIZE = 224;
	private static final int FINAL_KEY_SIZE = 16;

	/**
	 * Start outgoing key exchange
	 */
	public void initiateKeyExchange(CallInfo callInfo, InputStream inputStream, OutputStream outputStream) {
		DataInputStream dataInputStream = new DataInputStream(inputStream);
		KeyPairGenerator keyPairGenerator = getKeyPairGenerator();
		KeyPair keyPair = keyPairGenerator.generateKeyPair();
		
		sendKey(keyPair.getPublic(), outputStream);
		
		KeyAgreement keyAgreement = initKeyAgreement(keyPair.getPrivate());
		
		PublicKey otherPublic = getOtherPartyPublicKey(dataInputStream);
		
		byte[] secret = generateSecret(keyAgreement, otherPublic);
		byte[] finalKeys = getSha256Digest(secret);
		
		callInfo.setTxKey(getFirstKey(finalKeys));
		callInfo.setRxKey(getSecondKey(finalKeys));
	}

	/**
	 * Start incoming key exchange
	 */
	public void handleKeyExchange(CallInfo callInfo, InputStream inputStream, OutputStream outputStream) {
		DataInputStream dataInputStream = new DataInputStream(inputStream);
		
		KeyPairGenerator keyPairGenerator = initializeKeyPairGenerator();
		
		KeyPair keyPair = keyPairGenerator.generateKeyPair();
		sendPublicKey(keyPair.getPublic(), outputStream);
		
		KeyAgreement keyAgreement = initKeyAgreement(keyPair.getPrivate());
		
		PublicKey otherPublic = getOtherPartyPublicKey(dataInputStream);
		
		byte[] secret = generateSecret(keyAgreement, otherPublic);
		byte[] finalKeys = getSha256Digest(secret);
		
		callInfo.setTxKey(getSecondKey(finalKeys));
		callInfo.setRxKey(getFirstKey(finalKeys));
	}
	
	private KeyPairGenerator getKeyPairGenerator() {
		try {
			KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("ECDH", "SC");
			keyPairGenerator.initialize(DH_KEYSIZE);
			return keyPairGenerator;
		} catch (NoSuchAlgorithmException e) {
			throw new MainProtocolException("well, no diffie-hellman, sorry", e);
		} catch (NoSuchProviderException e) {
			throw new MainProtocolException("spongy castle was not loaded properly", e);
		}
	}

	private KeyPairGenerator initializeKeyPairGenerator() {
		try {
			KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("ECDH", "SC");
			keyPairGenerator.initialize(224);
			return keyPairGenerator;
		} catch (NoSuchAlgorithmException e) {
			throw new MainProtocolException("well, no diffie-hellman, shame", e);
		} catch (NoSuchProviderException e) {
			throw new MainProtocolException("spongy castle not found", e);
		}
	}
	
	private void sendPublicKey(PublicKey publicKey, OutputStream outputStream) {
		sendMessageWithSizeFirst(publicKey.getEncoded(), outputStream);
	}
	
	private void sendKey(PublicKey publicKey, OutputStream outputStream) {
		byte[] myPublicEncoded = publicKey.getEncoded();
		sendMessageWithSizeFirst(myPublicEncoded, outputStream);
	}
	
	private KeyAgreement initKeyAgreement(PrivateKey privateKey) {
		try {
			KeyAgreement keyAgreement = KeyAgreement.getInstance("ECDH", "SC");
			keyAgreement.init(privateKey);
			return keyAgreement;
		} catch (NoSuchAlgorithmException e) {
			throw new MainProtocolException("well, no diffie-hellman, sorry", e);
		} catch (InvalidKeyException e) {
			throw new MainProtocolException("dh doesn't like my key", e);
		} catch (NoSuchProviderException e) {
			throw new MainProtocolException("spongy castle not found", e);
		}
	}
	
	private PublicKey getOtherPartyPublicKey(DataInputStream inputStream) {
		try {
			byte[] otherPublicEncoded = receiveMessageWithSizeFirst(inputStream);
			KeyFactory keyFactory = KeyFactory.getInstance("ECDH", "SC");
			return keyFactory.generatePublic(new X509EncodedKeySpec(otherPublicEncoded));
		} catch (NoSuchAlgorithmException e) {
			throw new MainProtocolException("well, no diffie-hellman, sorry", e);
		} catch (InvalidKeySpecException e) {
			throw new MainProtocolException("received key was invalid", e);
		} catch (NoSuchProviderException e) {
			throw new MainProtocolException("spongy castle not found", e);
		}
	}
	
	private byte[] generateSecret(KeyAgreement keyAgreement, PublicKey otherPublic) {
		try {
			keyAgreement.doPhase(otherPublic, true);
			return keyAgreement.generateSecret();
		} catch (InvalidKeyException e) {
			throw new MainProtocolException("received key was invalid", e);
		} catch (IllegalStateException e) {
			throw new MainProtocolException("something went wrong in key exchange", e);
		}
	}
	
	private byte[] getSha256Digest(byte[] secret) {
		MessageDigest digest;
		try {
			digest = MessageDigest.getInstance("SHA-256");
		} catch (NoSuchAlgorithmException e) {
			throw new MainProtocolException("there's no sha256, how's it even possible?", e);
		}
		byte[] finalKeys = digest.digest(secret);
		return finalKeys;
	}

	/*
	 * first half of sha-256 output
	 */
	private byte[] getFirstKey(byte[] finalKeys) {
		byte[] firstKey = new byte[FINAL_KEY_SIZE];
		for(int i = 0; i < FINAL_KEY_SIZE; i++) {
			firstKey[i] = finalKeys[i];
		}
		return firstKey;
	}
	
	/*
	 * second half of sha-256 output
	 */
	private byte[] getSecondKey(byte[] finalKeys) {
		byte[] secondKey = new byte[FINAL_KEY_SIZE];
		for(int i = FINAL_KEY_SIZE; i < finalKeys.length; i++) {
			secondKey[i - FINAL_KEY_SIZE] = finalKeys[i];
		}
		return secondKey;
	}
}