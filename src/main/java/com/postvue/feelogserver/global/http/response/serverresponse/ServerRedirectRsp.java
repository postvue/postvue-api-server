package com.postvue.feelogserver.global.http.response.serverresponse;

import org.springframework.http.HttpStatus;

import com.postvue.feelogserver.global.http.response.BaseResponse;
import com.postvue.feelogserver.global.http.response.status.HttpRspStatusMessage;

public class ServerRedirectRsp<T> extends BaseResponse<T> {

	public ServerRedirectRsp(T data) {
		super(data, HttpStatus.PERMANENT_REDIRECT, HttpRspStatusMessage.SUCCESS_GET_OK);
	}
}
