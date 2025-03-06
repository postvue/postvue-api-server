package com.postvue.feelogserver.global.exception;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import org.springframework.dao.DataIntegrityViolationException;
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

	@ExceptionHandler(DataIntegrityViolationException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ErrorResponse handleDataIntegrityViolationException(DataIntegrityViolationException ex) {
		log.error("데이터 무결성 오류 발생: {}", ex.getMessage());
		return new ErrorResponse(HttpStatus.BAD_REQUEST, "DATA_INTEGRITY_ERROR: " + extractPostgresErrorMessage(ex));
	}

	@ExceptionHandler(RuntimeException.class)
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	public ErrorResponse handleRuntimeException(RuntimeException ex) {
		log.error("서버 오류 발생: {}", ex.getMessage(), ex);
		return new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "INTERNAL_SERVER_ERROR: " + ex.getMessage());
	}

	@ExceptionHandler(InvocationTargetException.class)
	public ResponseEntity<String> handleInvocationTargetException(InvocationTargetException e) {
		Throwable cause = e.getCause();  // 실제 예외 확인
		String errorMessage = cause != null ? cause.getMessage() : "알 수 없는 오류 발생";
		return new ResponseEntity<>("메서드 호출 중 오류 발생: " + errorMessage, HttpStatus.INTERNAL_SERVER_ERROR);
	}

	private String extractPostgresErrorMessage(DataIntegrityViolationException ex) {
		Throwable cause = ex.getCause();
		while (cause != null) {
			if (cause.getMessage().contains("violates not-null constraint")) {
				return "필수 입력값이 누락되었습니다: " + cause.getMessage();
			}
			cause = cause.getCause();
		}
		return "데이터 무결성 오류가 발생했습니다.";
	}
}
