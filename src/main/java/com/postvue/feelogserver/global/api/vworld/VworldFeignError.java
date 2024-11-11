package com.postvue.feelogserver.global.api.vworld;

import java.io.IOException;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.postvue.feelogserver.global.api.vworld.dto.rsp.VworldGetReverseGeocodeRsp;
import com.postvue.feelogserver.global.exception.InternalServerErrorException;

import feign.Response;
import feign.codec.ErrorDecoder;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class VworldFeignError implements ErrorDecoder {
	private final ObjectMapper objectMapper;

	@Override
	public Exception decode(String methodKey, Response response) {
		try {
			// 응답 본문을 JSON 형태로 파싱
			VworldGetReverseGeocodeRsp apiResponse = objectMapper.readValue(response.body().asInputStream(),
				VworldGetReverseGeocodeRsp.class);

			// ApiResponse의 status에 따라 예외 처리
			if (VworldRspConst.VWORLD_OK_RSP_STATUS.equals(apiResponse.getResponse().getStatus())) {
				return null;  // 정상 처리
			} else if (VworldRspConst.VWORLD_NOT_FOUND_RSP_STATUS.equals(apiResponse.getResponse().getStatus())) {
				return new InternalServerErrorException(apiResponse.getResponse().getStatus());
			} else if (VworldRspConst.VWORLD_ERROR_RSP_STATUS.equals(apiResponse.getResponse().getStatus())) {
				return new InternalServerErrorException(apiResponse.getResponse().getStatus());
			} else {
				return null;
			}

		} catch (IOException e) {
			return new InternalServerErrorException(e.getMessage());
		}
	}
}
