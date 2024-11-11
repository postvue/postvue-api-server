package com.postvue.feelogserver.global.api.kakao;

import org.springframework.http.HttpStatus;

import com.postvue.feelogserver.global.exception.kakao.KakaoServerException;
import com.postvue.feelogserver.global.exception.kakao.KakaoTokenValidException;

import feign.Response;
import feign.codec.ErrorDecoder;

public class KakaoFeignError implements ErrorDecoder {

	@Override
	public Exception decode(String methodKey, Response response) {

		// 401, 인증 오류 시
		if (response.status() == HttpStatus.UNAUTHORIZED.value()) {
			throw new KakaoTokenValidException();
		}

		// 500 에러
		throw new KakaoServerException();
	}
}
