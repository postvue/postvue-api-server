package com.postvue.feelogserver.global.exception;

import org.springframework.http.HttpStatus;

public class BadRequestErrorException extends BaseException {
	public BadRequestErrorException(String errorMessage) {
		super(HttpStatus.BAD_REQUEST, HttpStatus.BAD_REQUEST.value(), errorMessage,
			"Not System Error Message.");
	}

	public BadRequestErrorException(String errorMessage, Exception systemError) {
		super(HttpStatus.BAD_REQUEST, HttpStatus.BAD_REQUEST.value(), errorMessage,
			(systemError != null) ? systemError.getMessage() : "Not System Error Message.");
	}
}
