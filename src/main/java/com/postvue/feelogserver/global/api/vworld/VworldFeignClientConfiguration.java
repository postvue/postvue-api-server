package com.postvue.feelogserver.global.api.vworld;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.fasterxml.jackson.databind.ObjectMapper;

import feign.codec.ErrorDecoder;

@Configuration
public class VworldFeignClientConfiguration {

	@Bean
	public ErrorDecoder vworldErrorDecoder(ObjectMapper objectMapper) {
		return new VworldFeignError(objectMapper);
	}
}
