package com.postvue.feelogserver.global.api.naver;

import com.postvue.feelogserver.global.exception.naver.NaverServerException;
import com.postvue.feelogserver.global.exception.naver.NaverTokenValidException;

import feign.Response;
import feign.codec.ErrorDecoder;

public class NaverFeignError implements ErrorDecoder {

	@Override
	public Exception decode(String methodKey, Response response) {
		if (response.status() == 401) {
			throw new NaverTokenValidException();
		}
		throw new NaverServerException();
	}
}

