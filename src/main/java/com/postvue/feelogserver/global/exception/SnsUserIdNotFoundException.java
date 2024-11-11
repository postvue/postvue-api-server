package com.postvue.feelogserver.global.exception;

import static org.springframework.http.HttpStatus.*;

import com.postvue.feelogserver.global.constant.ErrorConst;
import com.postvue.feelogserver.global.constant.SystemPhraseConst;

// 해당 유저 id가 없을 떄 에러 발생
public class SnsUserIdNotFoundException extends BaseException {
	public SnsUserIdNotFoundException(Long memberId) {
		super(NOT_FOUND, NOT_FOUND.value(), SystemPhraseConst.USER_ID_NOT_FOUND_EXCEPTION_PHRASE + memberId,
			ErrorConst.NOT_SYSTEM_ERROR_MESSAGE);
	}
}
