package com.postvue.feelogserver.global.http.response.servererrorresponse;

import org.springframework.http.HttpStatus;

import lombok.AllArgsConstructor;
import lombok.Getter;

//@REFER: ErrorResponse와 중복됨
@Getter
@AllArgsConstructor
public class ServerErrorResponse {
	private final HttpStatus statusCode;
	private final String message;
}
