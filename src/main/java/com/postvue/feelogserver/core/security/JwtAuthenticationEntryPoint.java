package com.postvue.feelogserver.core.security;

import java.io.IOException;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.postvue.feelogserver.global.exception.ErrorResponse;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

	//@REFER: 이 부분 이해가 잘 안돼. 나중에 이해할 수 있는 수준으로 올리기
	//@REFER: 매직 값 존재
	@Override
	public void commence(
		HttpServletRequest request,
		HttpServletResponse response,
		AuthenticationException authException
	) throws IOException {
		// log.error(
		// 	LogTemplateConst.getErrorLogTemplate(IOException.class.getName(),
		// 		"[Authentication_Entry_Point]: 사용자가 유효한 자격증명을 제공하지 않고 접근을 시도했습니다.",
		// 		authException.getMessage(),
		// 		GroupOrderService.class.getName(), Thread.currentThread().getStackTrace()[1].getMethodName(),
		// 		new Object[] {request, response, authException},
		// 		HttpStatus.UNAUTHORIZED.value()
		// 	));

		response.setStatus(HttpStatus.UNAUTHORIZED.value());
		response.setContentType(MediaType.APPLICATION_JSON_VALUE);
		response.setCharacterEncoding("utf-8");
		ErrorResponse errorResponse = new ErrorResponse(HttpStatus.UNAUTHORIZED, authException.getMessage());
		new ObjectMapper().writeValue(response.getWriter(), errorResponse);
	}
}
