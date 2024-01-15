package com.web.exception;

@SuppressWarnings("serial")
public class InternalServerException extends RuntimeException {
	public InternalServerException(String message) {
		super(message);
	}
}
