package com.postvue.feelogserver.global.exception;

import org.springframework.http.HttpStatus;

import com.postvue.feelogserver.global.constant.ErrorConst;

public class InternalServerErrorException extends BaseException {

	public InternalServerErrorException(String errorMessage) {
		super(HttpStatus.INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR.value(), errorMessage,
			ErrorConst.NOT_SYSTEM_ERROR_MESSAGE);
	}

	public InternalServerErrorException(String errorMessage, Exception systemError) {
		super(HttpStatus.INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR.value(), errorMessage,
			(systemError != null) ? systemError.getMessage() : ErrorConst.NOT_SYSTEM_ERROR_MESSAGE);
	}
}
