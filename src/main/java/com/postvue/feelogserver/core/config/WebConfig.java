package com.postvue.feelogserver.core.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
	@Value("${serverUrl.domainUrl}")
	private String serverDomainUrl;

	@Value("${serverUrl.domainWWWUrl}")
	private String serverDomainWwwUrl;

	@Override
	public void addCorsMappings(CorsRegistry registry) {
		registry.addMapping("/**")
			.allowedOrigins(serverDomainUrl, serverDomainWwwUrl) // 허용할 출처 : 특정 도메인만 받을 수 있음
			.allowedMethods("GET", "POST", "PUT", "DELETE") // 허용할 HTTP method
			.allowedHeaders("*")
			.allowCredentials(true) // 쿠키 인증 요청 허용
			.maxAge(3600L);
	}
}
