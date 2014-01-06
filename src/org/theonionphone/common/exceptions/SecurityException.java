package org.theonionphone.common.exceptions;

public class SecurityException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public SecurityException(String msg) {
		super(msg);
	}
	
	public SecurityException(String msg, Throwable t) {
		super(msg, t);
	}
}
