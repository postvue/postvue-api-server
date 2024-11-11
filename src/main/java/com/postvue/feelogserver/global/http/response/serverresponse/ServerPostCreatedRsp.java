package com.postvue.feelogserver.global.http.response.serverresponse;

import org.springframework.http.HttpStatus;

import com.postvue.feelogserver.global.http.response.BaseResponse;
import com.postvue.feelogserver.global.http.response.status.HttpRspStatusMessage;

public class ServerPostCreatedRsp<T> extends BaseResponse<T> {

	public ServerPostCreatedRsp(T data) {
		super(data, HttpStatus.CREATED, HttpRspStatusMessage.SUCCESS_PUT_OK);
	}
}
