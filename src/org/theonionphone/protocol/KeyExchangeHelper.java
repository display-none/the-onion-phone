package org.theonionphone.protocol;

import static org.theonionphone.utils.ProtocolUtils.*;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.KeyAgreement;
import javax.crypto.interfaces.DHPublicKey;
import javax.crypto.spec.DHParameterSpec;

import org.theonionphone.common.CallInfo;
import org.theonionphone.common.exceptions.MainProtocolException;

public class KeyExchangeHelper {

	private static final int DH_KEYSIZE = 2048;
	private static final int FINAL_KEY_SIZE = 16;

	public void initiateKeyExchange(CallInfo callInfo, InputStream inputStream, OutputStream outputStream) {
		KeyPairGenerator keyPairGenerator = getKeyPairGenerator();
		KeyPair keyPair = keyPairGenerator.generateKeyPair();
		
		DHParameterSpec dhSpec = ((DHPublicKey)keyPair.getPublic()).getParams();
		sendDHSpecAndKey(dhSpec, keyPair.getPublic(), outputStream);
		
		KeyAgreement keyAgreement = initKeyAgreement(keyPair.getPrivate());
		
		PublicKey otherPublic = getOtherPartyPublicKey(inputStream);
		
		byte[] secret = generateSecret(keyAgreement, otherPublic);
		byte[] finalKeys = getSha256Digest(secret);
		
		callInfo.setTxKey(getFirstKey(finalKeys));
		callInfo.setRxKey(getSecondKey(finalKeys));
	}
	
	public void handleKeyExchange(CallInfo callInfo, InputStream inputStream, OutputStream outputStream) {
		DHParameterSpec dhSpec = receiveDHSpec(inputStream);
		KeyPairGenerator keyPairGenerator = initializeKeyPairGenerator(dhSpec);
		
		KeyPair keyPair = keyPairGenerator.generateKeyPair();
		sendPublicKey(keyPair.getPublic(), outputStream);
		
		KeyAgreement keyAgreement = initKeyAgreement(keyPair.getPrivate());
		
		PublicKey otherPublic = getOtherPartyPublicKey(inputStream);
		
		byte[] secret = generateSecret(keyAgreement, otherPublic);
		byte[] finalKeys = getSha256Digest(secret);
		
		callInfo.setTxKey(getSecondKey(finalKeys));
		callInfo.setRxKey(getFirstKey(finalKeys));
	}
	
	private KeyPairGenerator getKeyPairGenerator() {
		try {
			KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("DH");
			keyPairGenerator.initialize(DH_KEYSIZE);
			return keyPairGenerator;
		} catch (NoSuchAlgorithmException e) {
			throw new MainProtocolException("well, no diffie-hellman, sorry", e);
		}
	}
	

	private KeyPairGenerator initializeKeyPairGenerator(DHParameterSpec dhSpec) {
		try {
			KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("DH");
			keyPairGenerator.initialize(dhSpec);
			return keyPairGenerator;
		} catch (NoSuchAlgorithmException e) {
			throw new MainProtocolException("well, no diffie-hellman, shame", e);
		} catch (InvalidAlgorithmParameterException e) {
			throw new MainProtocolException("diffie-hellman specification was invalid", e);
		}
	}
	
	private void sendPublicKey(PublicKey publicKey, OutputStream outputStream) {
		sendMessageWithSizeFirst(publicKey.getEncoded(), outputStream);
	}
	
	private void sendDHSpecAndKey(DHParameterSpec dhSpec, PublicKey publicKey, OutputStream outputStream) {
		BigInteger specG = dhSpec.getG();
		BigInteger specP = dhSpec.getP();
		BigInteger specL = BigInteger.valueOf(dhSpec.getL());
		byte[] myPublicEncoded = publicKey.getEncoded();
		
		DataOutputStream dos = new DataOutputStream(outputStream);
		sendMessageWithSizeFirst(specG.toByteArray(), dos);
		sendMessageWithSizeFirst(specP.toByteArray(), dos);
		sendMessageWithSizeFirst(specL.toByteArray(), dos);
		sendMessageWithSizeFirst(myPublicEncoded, dos);
	}
	
	private DHParameterSpec receiveDHSpec(InputStream inputStream) {
		DataInputStream dis = new DataInputStream(inputStream);
		
		BigInteger specG = new BigInteger(receiveMessageWithSizeFirst(dis));
		BigInteger specP = new BigInteger(receiveMessageWithSizeFirst(dis));
		int specL = (new BigInteger(receiveMessageWithSizeFirst(dis))).intValue();
		
		return new DHParameterSpec(specP, specG, specL);
	}
	
	private KeyAgreement initKeyAgreement(PrivateKey privateKey) {
		try {
			KeyAgreement keyAgreement = KeyAgreement.getInstance("DH");
			keyAgreement.init(privateKey);
			return keyAgreement;
		} catch (NoSuchAlgorithmException e) {
			throw new MainProtocolException("well, no diffie-hellman, sorry", e);
		} catch (InvalidKeyException e) {
			throw new MainProtocolException("dh doesn't like my key", e);
		}
	}
	
	private PublicKey getOtherPartyPublicKey(InputStream inputStream) {
		try {
			byte[] otherPublicEncoded = receiveMessageWithSizeFirst(inputStream);
			KeyFactory keyFactory = KeyFactory.getInstance("DH");
			return keyFactory.generatePublic(new X509EncodedKeySpec(otherPublicEncoded));
		} catch (NoSuchAlgorithmException e) {
			throw new MainProtocolException("well, no diffie-hellman, sorry", e);
		} catch (InvalidKeySpecException e) {
			throw new MainProtocolException("received key was invalid", e);
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
			secondKey[i] = finalKeys[i];
		}
		return secondKey;
	}
}
