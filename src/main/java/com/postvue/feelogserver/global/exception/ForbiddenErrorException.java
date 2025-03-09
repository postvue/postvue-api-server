package com.postvue.feelogserver.global.exception;

import org.springframework.http.HttpStatus;

public class ForbiddenErrorException extends BaseException {
	public ForbiddenErrorException(String errorMessage) {
		super(HttpStatus.FORBIDDEN, HttpStatus.FORBIDDEN.value(), errorMessage,
			HttpStatus.FORBIDDEN.getReasonPhrase());
	}

	public ForbiddenErrorException(String errorMessage, Exception systemError) {
		super(HttpStatus.FORBIDDEN, HttpStatus.FORBIDDEN.value(), errorMessage,
			(systemError != null) ? systemError.getMessage() : HttpStatus.FORBIDDEN.getReasonPhrase());
	}
}
