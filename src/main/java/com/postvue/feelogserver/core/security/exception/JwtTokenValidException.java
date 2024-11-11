package com.postvue.feelogserver.core.security.exception;

import static org.springframework.http.HttpStatus.*;

import com.postvue.feelogserver.global.exception.BaseException;

public class JwtTokenValidException extends BaseException {

	public JwtTokenValidException(Exception systemError) {
		super(UNAUTHORIZED, UNAUTHORIZED.value(), "유효하지 않는 JWT 토큰입니다.", systemError.getMessage());
	}
}
