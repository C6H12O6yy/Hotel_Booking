package com.web.exception;

@SuppressWarnings("serial")
public class InvalidBookingRequestException extends RuntimeException {
	public InvalidBookingRequestException(String message) {
		super(message);
	}
}
