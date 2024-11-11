package com.postvue.feelogserver.core.security.exception;

import static org.springframework.http.HttpStatus.*;

import com.postvue.feelogserver.global.exception.BaseException;

public class JwtTokenExpiredException extends BaseException {

	public JwtTokenExpiredException(Exception systemError) {
		super(UNAUTHORIZED, UNAUTHORIZED.value(), "유효기간이 만료된 JWT 토큰입니다.", systemError.getMessage());
	}
}
