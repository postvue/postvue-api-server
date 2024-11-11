package com.postvue.feelogserver.global.api.naver;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import feign.codec.ErrorDecoder;

@Configuration
public class NaverFeignClientConfiguration {

	@Bean
	public ErrorDecoder naverFeignErrorDecoder() {
		return new NaverFeignError();
	}
}
