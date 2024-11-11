package com.postvue.feelogserver.global.http.response.serverresponse;

import org.springframework.http.HttpStatus;

import com.postvue.feelogserver.global.http.response.BaseResponse;
import com.postvue.feelogserver.global.http.response.status.HttpRspStatusMessage;

public class ServerGetOkRsp<T> extends BaseResponse<T> {

	public ServerGetOkRsp(T data) {
		super(data, HttpStatus.OK, HttpRspStatusMessage.SUCCESS_GET_OK);
	}
}
