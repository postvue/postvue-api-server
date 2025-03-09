package com.postvue.feelogserver.global.exception;

import org.springframework.http.HttpStatus;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
public class BaseException extends RuntimeException {
	private final HttpStatus httpStatus;
	private final int errorCode;
	private final String errorMessage;
	private final String systemErrorMessage;

	public BaseException(HttpStatus httpStatus, int errorCode, String errorMessage, String systemErrorMessage) {
		super(errorMessage);  // RuntimeException의 message 설정
		this.httpStatus = httpStatus;
		this.errorCode = errorCode;
		this.errorMessage = errorMessage;
		this.systemErrorMessage = systemErrorMessage;
	}
}
