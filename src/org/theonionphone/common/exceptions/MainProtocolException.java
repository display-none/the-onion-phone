package org.theonionphone.common.exceptions;

public class MainProtocolException extends ProtocolException {

	private static final long serialVersionUID = 1L;

	public MainProtocolException(String msg) {
		super(msg);
	}
	
	public MainProtocolException(String msg, Throwable t) {
		super(msg, t);
	}
}
