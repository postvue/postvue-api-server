package com.postvue.feelogserver.global.exception.kakao;

import org.springframework.http.HttpStatus;

import com.postvue.feelogserver.global.constant.ErrorConst;
import com.postvue.feelogserver.global.constant.SystemPhraseConst;
import com.postvue.feelogserver.global.exception.BaseException;

public class KakaoServerException extends BaseException {

	public KakaoServerException() {
		super(HttpStatus.INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR.value(),
			SystemPhraseConst.KAKAO_SERVER_EXCEPTION_PHRASE,
			ErrorConst.NOT_SYSTEM_ERROR_MESSAGE);
	}
}
