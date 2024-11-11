package com.postvue.feelogserver.global.exception;

import org.springframework.http.HttpStatus;

import com.postvue.feelogserver.global.constant.ErrorConst;

public class UnauthorizedErrorException extends BaseException {
	public UnauthorizedErrorException(String errorMessage) {
		super(HttpStatus.UNAUTHORIZED, HttpStatus.UNAUTHORIZED.value(), errorMessage,
			ErrorConst.NOT_SYSTEM_ERROR_MESSAGE);
	}
}
