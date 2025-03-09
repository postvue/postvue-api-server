package com.postvue.feelogserver.global.exception;

import java.io.IOException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {
	@ExceptionHandler(BaseException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)  // 400 오류
	@ResponseBody
	public ResponseEntity<ErrorResponse> handleRestApiException(BaseException ex) {
		return ResponseEntity
			.status(ex.getHttpStatus())
			.body(new ErrorResponse(ex.getHttpStatus(), ex.getErrorMessage()));
	}

	@ExceptionHandler(IOException.class)
	public ResponseEntity<?> handleIoException(IOException error) {
		return new ResponseEntity<>(new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "").getStatusCode());
	}
}
