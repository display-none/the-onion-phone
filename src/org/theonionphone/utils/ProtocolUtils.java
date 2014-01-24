package org.theonionphone.utils;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.theonionphone.common.exceptions.MainProtocolException;

public class ProtocolUtils {

	public static boolean compareIfIdentical(byte[] arr1, byte[] arr2) {
		if(arr1.length != arr2.length) {
			return false;
		}
		for(int i = 0; i < arr1.length; i++) {
			if(arr1[i] != arr2[i]) {
				return false;
			}
		}
		return true;
	}

	public static void sendMessage(byte[] msg, OutputStream outputStream) {
		try {
			outputStream.write(msg);
		} catch (IOException e) {
			throw new MainProtocolException("error while sending message");
		}
	}
	
	public static void sendMessageWithSizeFirst(byte[] msg, OutputStream outputStream) {
		DataOutputStream dos = new DataOutputStream(outputStream);
		sendMessageWithSizeFirst(msg, dos);
	}
	
	public static void sendMessageWithSizeFirst(byte[] msg, DataOutputStream dataOutputStream) {
		try {
			DataOutputStream dos = new DataOutputStream(dataOutputStream);
			dos.writeInt(msg.length);
		} catch (IOException e) {
			throw new MainProtocolException("error while sending message size");
		}
		sendMessage(msg, dataOutputStream);
	}
	
	public static void receiveSpecificMessage(byte[] msg, DataInputStream inputStream) {
		byte[] receivedMsg = new byte[msg.length];
		receiveMessageInto(receivedMsg, inputStream);
		if(!compareIfIdentical(msg, receivedMsg)) {
			throw new MainProtocolException("received wrong message");
		}
	}
	
	public static void receiveMessageInto(byte[] msg, DataInputStream inputStream) {
		try {
			inputStream.readFully(msg);
		} catch (IOException e) {
			throw new MainProtocolException("error while receiving message");
		}
	}
	
	public static byte[] receiveMessageWithSizeFirst(DataInputStream dataInputStream) {
		try {
			int size = dataInputStream.readInt();
			byte[] msg = new byte[size];
			receiveMessageInto(msg, dataInputStream);
			return msg;
		} catch (IOException e) {
			throw new MainProtocolException("error while receiving message size");
		}		
	}
}
