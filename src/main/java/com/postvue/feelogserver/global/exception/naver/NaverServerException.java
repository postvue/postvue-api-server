package com.postvue.feelogserver.global.exception.naver;

import static org.springframework.http.HttpStatus.*;

import com.postvue.feelogserver.global.constant.ErrorConst;
import com.postvue.feelogserver.global.constant.SystemPhraseConst;
import com.postvue.feelogserver.global.exception.BaseException;

public class NaverServerException extends BaseException {

	public NaverServerException() {
		super(INTERNAL_SERVER_ERROR, INTERNAL_SERVER_ERROR.value(), SystemPhraseConst.NAVER_SERVER_EXCEPTION_PHRASE,
			ErrorConst.NOT_SYSTEM_ERROR_MESSAGE);
	}
}
