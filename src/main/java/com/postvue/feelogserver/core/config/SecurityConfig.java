package com.postvue.feelogserver.core.config;

import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.postvue.feelogserver.core.security.JwtAccessDeniedHandler;
import com.postvue.feelogserver.core.security.JwtAuthenticationEntryPoint;
import com.postvue.feelogserver.core.security.JwtAuthenticationFilter;
import com.postvue.feelogserver.core.security.JwtExceptionFilter;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

	private final JwtAuthenticationFilter jwtAuthenticationFilter;
	private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
	private final JwtAccessDeniedHandler jwtAccessDeniedHandler;
	private final JwtExceptionFilter jwtExceptionFilter;

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		return http
			.httpBasic(AbstractHttpConfigurer::disable)
			.csrf(AbstractHttpConfigurer::disable)
			.formLogin((AbstractHttpConfigurer::disable))
			.sessionManagement((sessionManagementConfig ->
				sessionManagementConfig
					.sessionCreationPolicy(SessionCreationPolicy.STATELESS)))
			.authorizeHttpRequests((authorizationManagerRequestMatcherRegistry ->
				authorizationManagerRequestMatcherRegistry
					.requestMatchers(PathRequest.toStaticResources().atCommonLocations()).permitAll()
					.requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()
					.requestMatchers(HttpMethod.GET, "/").permitAll()
					.anyRequest().permitAll()
			))
			// 모든 요청에 대해 필터 전에 처리 됨
			.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
			.addFilterBefore(jwtExceptionFilter, jwtAuthenticationFilter.getClass())
			.exceptionHandling(exceptionConfig -> exceptionConfig
				.authenticationEntryPoint(jwtAuthenticationEntryPoint)
				.accessDeniedHandler(jwtAccessDeniedHandler)
			)
			.build();

	}

	@Bean
	public BCryptPasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
}
