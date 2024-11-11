package com.postvue.feelogserver.global.exception.kakao;

import org.springframework.http.HttpStatus;

import com.postvue.feelogserver.global.constant.SystemPhraseConst;
import com.postvue.feelogserver.global.exception.BaseException;

public class KakaoTokenValidException extends BaseException {
	public KakaoTokenValidException() {
		super(HttpStatus.UNAUTHORIZED, HttpStatus.UNAUTHORIZED.value(),
			SystemPhraseConst.KAKAO_TOKEN_VALID_EXCEPTION_PHRASE, null);
	}
}
