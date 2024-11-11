package com.postvue.feelogserver.global.exception;

import org.springframework.http.HttpStatus;

import com.postvue.feelogserver.global.constant.ErrorConst;
import com.postvue.feelogserver.global.constant.SystemPhraseConst;

public class RefreshTokenNotFoundException extends BaseException {

	public RefreshTokenNotFoundException() {
		super(HttpStatus.UNAUTHORIZED, HttpStatus.UNAUTHORIZED.value(),
			SystemPhraseConst.REFRESH_TOKEN_NOT_FOUND_EXCEPTION_PHRASE,
			ErrorConst.NOT_SYSTEM_ERROR_MESSAGE);
	}
}
