package org.theonionphone.common.exceptions;

public class ProtocolException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public ProtocolException(String msg) {
		super(msg);
	}
	
	public ProtocolException(String msg, Throwable t) {
		super(msg, t);
	}
}
