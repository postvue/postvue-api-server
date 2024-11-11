package com.postvue.feelogserver.global.http.response;

import org.springframework.http.HttpStatus;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class BaseResponse<T> {
	private T data;
	private final HttpStatus statusCode;
	private final String message;
}
