package com.postvue.feelogserver.core.security;

import java.io.IOException;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.postvue.feelogserver.core.security.exception.JwtTokenExpiredException;
import com.postvue.feelogserver.core.security.exception.JwtTokenValidException;
import com.postvue.feelogserver.global.constant.LogTemplateConst;
import com.postvue.feelogserver.global.constant.SystemErrorSpecificationConst;
import com.postvue.feelogserver.global.exception.ErrorResponse;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class JwtExceptionFilter extends OncePerRequestFilter {

	@Override
	protected void doFilterInternal(
		HttpServletRequest request,
		HttpServletResponse response,
		FilterChain filterChain
	) throws ServletException, IOException {
		try {
			filterChain.doFilter(request, response);
		} catch (JwtTokenExpiredException | JwtTokenValidException ex) {
			setErrorResponse(response, ex.getErrorMessage());
		}
	}

	private void setErrorResponse(HttpServletResponse response, String errorMessage) throws IOException {
		log.error(
			LogTemplateConst.getErrorLogTemplate(IOException.class.getName(),
				"[JWT_Exception_Filter]",
				errorMessage,
				this.getClass().getName(), Thread.currentThread().getStackTrace()[1].getMethodName(),
				new Object[] {response, errorMessage},
				HttpStatus.UNAUTHORIZED.value()
			));
		response.setStatus(HttpStatus.UNAUTHORIZED.value());
		response.setContentType(MediaType.APPLICATION_JSON_VALUE);
		response.setCharacterEncoding("utf-8");
		ErrorResponse serverErrorResponse = new ErrorResponse(HttpStatus.UNAUTHORIZED,
			SystemErrorSpecificationConst.TOKEN_EXPIRED_SPECIFICATION);

		//@REFER: ObjedtMapper 자체가 비용이 커서, 싱글턴 방식 또는 프로토타입패턴 으로 구현 필요
		new ObjectMapper().writeValue(response.getWriter(), serverErrorResponse);
	}
}
