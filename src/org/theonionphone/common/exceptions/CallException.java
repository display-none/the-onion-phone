package org.theonionphone.common.exceptions;

public class CallException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public CallException(String msg) {
		super(msg);
	}
	
	public CallException(String msg, Throwable e) {
		super(msg, e);
	}
}
