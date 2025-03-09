package com.postvue.feelogserver.core.security;

import java.io.IOException;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.postvue.feelogserver.global.constant.LogTemplateConst;
import com.postvue.feelogserver.global.exception.ErrorResponse;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class JwtAccessDeniedHandler implements AccessDeniedHandler {

	@Override
	public void handle(
		HttpServletRequest request,
		HttpServletResponse response,
		AccessDeniedException accessDeniedException
	) throws IOException {
		response.setStatus(HttpStatus.FORBIDDEN.value());
		response.setContentType(MediaType.APPLICATION_JSON_VALUE);
		response.setCharacterEncoding("utf-8");
		ErrorResponse errorResponse = new ErrorResponse(HttpStatus.FORBIDDEN, accessDeniedException.getMessage());

		log.error(
			LogTemplateConst.getErrorLogTemplate(IOException.class.getName(),
				"[Access_Denied_Handler]: 사용자가 필요한 권한이 없는 상태로 접근했습니다.",
				errorResponse.getMessage(),
				this.getClass().getName(), Thread.currentThread().getStackTrace()[1].getMethodName(),
				new Object[] {request, response, accessDeniedException},
				HttpStatus.UNAUTHORIZED.value()
			));
		new ObjectMapper().writeValue(response.getWriter(), errorResponse);
	}
}
