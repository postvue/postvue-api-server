package com.postvue.feelogserver.global.http.response.serverresponse;

import org.springframework.http.HttpStatus;

import com.postvue.feelogserver.global.http.response.BaseResponse;
import com.postvue.feelogserver.global.http.response.status.HttpRspStatusMessage;

public class ServerDeleteRsp<T> extends BaseResponse<T> {

	public ServerDeleteRsp(T data) {
		super(data, HttpStatus.NO_CONTENT, HttpRspStatusMessage.SUCCESS_PUT_OK);
	}
}
