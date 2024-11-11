package com.postvue.feelogserver.global.api.juso.addresssearch;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.fasterxml.jackson.databind.ObjectMapper;

import feign.codec.ErrorDecoder;

@Configuration
public class JusoAddressSearchFeignClientConfiguration {

	@Bean
	public ErrorDecoder jusoErrorDecoder(ObjectMapper objectMapper) {
		return new JusoAddressSearchFeignError(objectMapper);
	}
}
