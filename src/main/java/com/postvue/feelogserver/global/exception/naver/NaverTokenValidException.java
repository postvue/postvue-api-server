package com.postvue.feelogserver.global.exception.naver;

import org.springframework.http.HttpStatus;

import com.postvue.feelogserver.global.constant.SystemPhraseConst;
import com.postvue.feelogserver.global.exception.BaseException;

public class NaverTokenValidException extends BaseException {
	public NaverTokenValidException() {
		super(HttpStatus.UNAUTHORIZED, HttpStatus.UNAUTHORIZED.value(),
			SystemPhraseConst.NAVER_TOKEN_VALID_EXCEPTION_PHRASE, null);
	}
}
