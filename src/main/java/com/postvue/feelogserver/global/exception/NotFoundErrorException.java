package com.postvue.feelogserver.global.exception;

import org.springframework.http.HttpStatus;

public class NotFoundErrorException extends BaseException {
	public NotFoundErrorException(String errorMessage) {
		super(HttpStatus.NOT_FOUND, HttpStatus.NOT_FOUND.value(), errorMessage,
			HttpStatus.NOT_FOUND.getReasonPhrase());
	}
}
