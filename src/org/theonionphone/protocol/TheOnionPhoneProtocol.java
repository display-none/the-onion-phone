package org.theonionphone.protocol;

import static org.theonionphone.utils.ProtocolUtils.*;

import java.io.InputStream;
import java.io.OutputStream;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Random;

import org.theonionphone.common.CallInfo;
import org.theonionphone.common.exceptions.MainProtocolException;
import org.theonionphone.common.exceptions.SecurityException;
import org.theonionphone.identity.Identity;
import org.theonionphone.identity.IdentityManager;
import org.theonionphone.utils.locator.ServiceLocator;

public class TheOnionPhoneProtocol {

	private static final int KEY_SIZE = 16;
								//hello msgs
	private static final byte[] HELLO_MSG = {'H', 'e', 'l', 'l', 'o', ' ', ' ', ' '};
	private static final byte[] HELLO_ACK_MSG = {'H', 'e', 'l', 'l', 'o', 'A', 'C', 'K'};
								//version msg
	private static final byte[] VERSION_MSG = {'V', 'e', 'r', '0', '1', '.', '0', '0'};
								//introduction msgs
	private static final byte[] INTRODUCTION_MSG = {'I', 'n', 't', 'r', 'o', 'd', 'u', 'c', 'e', ' ', ' '};
	private static final byte[] NO_INTRODUCTION_MSG = {'N', 'o', 'I', 'n', 't', 'r', 'o', 'd', 'u', 'c', 'e'};
								//introduction request msgs
	private static final byte[] INTRODUCTION_REQ_MSG = {'R', 'e', 'q', 'I', 'n', 't', 'r', 'o', 'd', ' ', ' '};
	private static final byte[] NO_INTRODUCTION_REQ_MSG = {'N', 'o', 'R', 'e', 'q', 'I', 'n', 't', 'r', 'o', 'd'};
								//accept / reject msgs
	private static final byte[] ACCEPT_MSG = {'A', 'c', 'c', 'e', 'p', 't'};
	private static final byte[] REJECT_MSG = {'R', 'e', 'j', 'e', 'c', 't'};
	private static final byte[] WAIT_FOR_ACCEPTANCE_MSG = {'W', 'a', 'i', 't', ' ', ' '};
	
	private static final int INTRODUCTION_MSG_SIZE = 80;
	
	private KeepAliveWorker keepAliveWorker;

	/* 	OUTGOING  */
	
	public void initiateOutgoingSession(CallInfo callInfo, InputStream inputStream, OutputStream outputStream) {
		sendAndReceiveHello(inputStream, outputStream);
		sendAndReceiveVersion(inputStream, outputStream);
		handleOutgoingIntroduction(callInfo, inputStream, outputStream);
		handleOutgoingRequestForIntroduction(callInfo, inputStream, outputStream);
		
		waitForAccept(inputStream);
		
		handleIncomingIntroductionIfRequested(callInfo, inputStream, outputStream);
	}

	private void sendAndReceiveHello(InputStream inputStream, OutputStream outputStream) {
		sendMessage(HELLO_MSG, outputStream);
		receiveSpecificMessage(HELLO_ACK_MSG, inputStream);
		receiveSpecificMessage(HELLO_MSG, inputStream);
		sendMessage(HELLO_ACK_MSG, outputStream);
	}
	
	private void sendAndReceiveVersion(InputStream inputStream, OutputStream outputStream) {
		sendMessage(VERSION_MSG, outputStream);
		receiveVersion(inputStream);
	}
	
	private void handleOutgoingIntroduction(CallInfo callInfo, InputStream inputStream, OutputStream outputStream) {
		if(callInfo.getShouldIntroduce()) {
			sendMessage(INTRODUCTION_MSG, outputStream);
			
			proveIdentityToOtherParty(inputStream, outputStream);
		} else {
			sendMessage(NO_INTRODUCTION_MSG, outputStream);
		}
	}

	private void proveIdentityToOtherParty(InputStream inputStream, OutputStream outputStream) {
		IdentityManager identityManager = ServiceLocator.getInstance().getIdentityManager();
		KeyPair keyPair = identityManager.getMyKeys();
		
		//send our public key
		byte[] publicKeyEncoded = keyPair.getPublic().getEncoded();
		sendMessageWithSizeFirst(publicKeyEncoded, outputStream);
		
		//get a message to sign
		byte[] messageToSign = new byte[INTRODUCTION_MSG_SIZE];
		receiveMessageInto(messageToSign, inputStream);
		
		try {
			//sign it
			Signature signature = Signature.getInstance("SHA256WITHRSA");
			signature.initSign(keyPair.getPrivate());
			signature.update(messageToSign);
			byte[] signatureBytes = signature.sign();
			
			//and send
			sendMessageWithSizeFirst(signatureBytes, outputStream);
		} catch (NoSuchAlgorithmException e) {
			throw new MainProtocolException("there's no fucking sha256withRSA on this device", e);
		} catch (InvalidKeyException e) {
			throw new MainProtocolException("guess what? invalid key", e);
		} catch (SignatureException e) {
			throw new MainProtocolException("sth happened", e);
		}
	}
	
	private void handleOutgoingRequestForIntroduction(CallInfo callInfo, InputStream inputStream, OutputStream outputStream) {
		if(callInfo.getRequiresIntroduction()) {
			sendMessage(INTRODUCTION_REQ_MSG, outputStream);
		} else {
			sendMessage(NO_INTRODUCTION_REQ_MSG, outputStream);
		}
	}
	
	private void waitForAccept(InputStream inputStream) {
		while(true) {
			byte[] msg = new byte[ACCEPT_MSG.length];
			receiveMessageInto(msg, inputStream);
			
			if(compareIfIdentical(WAIT_FOR_ACCEPTANCE_MSG, msg)) {
				continue;
			} else if(compareIfIdentical(ACCEPT_MSG, msg)) {
				//cool, now we can get to business
				break;
			} else if(compareIfIdentical(REJECT_MSG, msg)) {
				//TODO: show sth in UI
				throw new MainProtocolException("call rejected");
			} else {
				throw new MainProtocolException("received wrong message");
			}
		}
	}
	
	private void handleIncomingIntroductionIfRequested(CallInfo callInfo, InputStream inputStream, OutputStream outputStream) {
		if(callInfo.getRequiresIntroduction()) {
			PublicKey publicKey = receiveAndVerifyPublicKey(inputStream, outputStream);
			validateKeyAgainstExpected(callInfo.getIdentity().getPublicKey(), publicKey);
			callInfo.getIdentity().setPublicKey(publicKey);			//since we validated, the keys are either the same or first one was not specified
		}
	}
	
	private void validateKeyAgainstExpected(PublicKey expectedPublicKey, PublicKey actualPublicKey) {
		if(expectedPublicKey == null) {
			return;		//user wasn't expecting any specific public key
		}
		boolean same = compareIfIdentical(expectedPublicKey.getEncoded(), actualPublicKey.getEncoded());
		if(!same) {
			throw new SecurityException("other party public key doesn't match public key specified by user");
		}
	}
	
	

	/* 	INCOMING  */
	
	public CallInfo initiateIncomingSession(InputStream inputStream, OutputStream outputStream) {
		CallInfo callInfo = new CallInfo();
		receiveAndSendHello(inputStream, outputStream);
		receiveAndSendVersion(inputStream, outputStream);
		Identity identity = handleIncomingIntroduction(inputStream, outputStream);
		callInfo.setIdentity(identity);
		boolean requiresIntroduction = handleIncomingRequestForIntroduction(inputStream, outputStream);
		callInfo.setRequiresIntroduction(requiresIntroduction);
		startKeepAliveWorker(outputStream);
		
		return callInfo;
	}

	private void receiveAndSendHello(InputStream inputStream, OutputStream outputStream) {
		receiveSpecificMessage(HELLO_MSG, inputStream);
		sendMessage(HELLO_ACK_MSG, outputStream);
		sendMessage(HELLO_MSG, outputStream);
		receiveSpecificMessage(HELLO_ACK_MSG, inputStream);
	}
	
	private void receiveAndSendVersion(InputStream inputStream, OutputStream outputStream) {
		receiveVersion(inputStream);
		sendMessage(VERSION_MSG, outputStream);
	}
	
	private void receiveVersion(InputStream inputStream) {
		byte[] receivedMsg = new byte[VERSION_MSG.length];
		receiveMessageInto(receivedMsg, inputStream);
		//it's the first version, so we don't give a damn
	}
	
	private Identity handleIncomingIntroduction(InputStream inputStream, OutputStream outputStream) {
		Identity identity = new Identity();
		if(isOtherPartyIntroducing(inputStream)) {
			PublicKey publicKey = receiveAndVerifyPublicKey(inputStream, outputStream);
			identity.setPublicKey(publicKey);
		}
		return identity;
	}

	private boolean isOtherPartyIntroducing(InputStream inputStream) {
		byte[] introductionType = new byte[INTRODUCTION_MSG.length];
		receiveMessageInto(introductionType, inputStream);
		if(compareIfIdentical(INTRODUCTION_MSG, introductionType)) {
			return true;
		} else if(compareIfIdentical(NO_INTRODUCTION_MSG, introductionType)) {
			return false;
		} else {
			throw new MainProtocolException("received wrong message");
		}
	}

	private PublicKey receiveAndVerifyPublicKey(InputStream inputStream, OutputStream outputStream) {
		//receive other party's public key
		byte[] publicKeyEncoded = receiveMessageWithSizeFirst(inputStream);
		PublicKey publicKey;
		try {
			publicKey = KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(publicKeyEncoded));
		} catch (InvalidKeySpecException e) {
			throw new MainProtocolException("received key is invalid", e);
		} catch (NoSuchAlgorithmException e) {
			throw new MainProtocolException("there's no fucking RSA on this device... unbelievable", e);
		}
		
		//generate and send random message
		byte[] randomMessage = new byte[INTRODUCTION_MSG_SIZE];
		(new Random()).nextBytes(randomMessage);
		sendMessage(randomMessage, outputStream);
		
		//receive signature
		byte[] receivedSignature = receiveMessageWithSizeFirst(inputStream);
		
		//verify signature
		try {
			Signature signature = Signature.getInstance("SHA256WITHRSA");
			signature.initVerify(publicKey);
			signature.update(randomMessage);
			boolean verified = signature.verify(receivedSignature);
			if(!verified) {
				throw new SecurityException("failed to verify other party's identity");
			}
		} catch (NoSuchAlgorithmException e) {
			throw new MainProtocolException("there's no fucking sha256withRSA on this device", e);
		} catch (InvalidKeyException e) {
			throw new MainProtocolException("received key is invalid", e);
		} catch (SignatureException e) {
			throw new MainProtocolException("sth with signature", e);
		}
		return publicKey;
	}
	
	private boolean handleIncomingRequestForIntroduction(InputStream inputStream, OutputStream outputStream) {
		byte[] introductionReqType = new byte[INTRODUCTION_REQ_MSG.length];
		receiveMessageInto(introductionReqType, inputStream);
		if(compareIfIdentical(INTRODUCTION_REQ_MSG, introductionReqType)) {
			return true;
		} else if(compareIfIdentical(NO_INTRODUCTION_REQ_MSG, introductionReqType)) {
			return false;
		} else {
			throw new MainProtocolException("received wrong message");
		}
	}
	
	
	
	
	public void acceptCall(CallInfo callInfo, InputStream inputStream, OutputStream outputStream) {
		keepAliveWorker.stopSending();
		sendMessage(ACCEPT_MSG, outputStream);
		
		handleOutgoingIntroductionIfRequested(callInfo, inputStream, outputStream);
	}
	
	private void handleOutgoingIntroductionIfRequested(CallInfo callInfo, InputStream inputStream, OutputStream outputStream) {
		if(callInfo.getRequiresIntroduction()) {
			proveIdentityToOtherParty(inputStream, outputStream);
		}
	}

	private void startKeepAliveWorker(OutputStream outputStream) {
		keepAliveWorker = new KeepAliveWorker(outputStream);
		keepAliveWorker.start();
	}
	
	public byte[] getSessionKey() {
		byte[] key = new byte[KEY_SIZE];
		return key;
	}
	
	private static class KeepAliveWorker extends Thread {
		
		private static final long SLEEP_TIME = 5000;	//5 seconds
		private OutputStream outputStream;
		private boolean running = true;
		
		public KeepAliveWorker(OutputStream outputStream) {
			this.outputStream = outputStream;
		}
		
		@Override
		public void run() {
			while(running) {
				sendMessage(WAIT_FOR_ACCEPTANCE_MSG, outputStream);
				try {
					Thread.sleep(SLEEP_TIME);
				} catch (InterruptedException e) { }
			}
		}
		
		public void stopSending() {
			running = false;
			this.interrupt();
		}
	}
}
