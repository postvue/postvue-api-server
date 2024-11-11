package com.postvue.feelogserver.global.api.kakao;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import feign.codec.ErrorDecoder;

@Configuration
public class KakaoFeignClientConfiguration {

	@Bean
	public ErrorDecoder kakaoErrorDecoder() {
		return new KakaoFeignError();
	}
}
