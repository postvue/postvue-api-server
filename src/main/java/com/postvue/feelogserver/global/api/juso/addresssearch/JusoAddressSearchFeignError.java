package com.postvue.feelogserver.global.api.juso.addresssearch;

import java.io.IOException;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.postvue.feelogserver.global.api.juso.addresssearch.dto.JusoAddressSearchTemplate;
import com.postvue.feelogserver.global.exception.InternalServerErrorException;

import feign.Response;
import feign.codec.ErrorDecoder;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class JusoAddressSearchFeignError implements ErrorDecoder {
	private final ObjectMapper objectMapper;

	@Override
	public Exception decode(String methodKey, Response response) {
		try {
			// 응답 본문을 JSON 형태로 파싱
			JusoAddressSearchTemplate apiResponse = objectMapper.readValue(response.body().asInputStream(),
				JusoAddressSearchTemplate.class);

			// ApiResponse의 status에 따라 예외 처리
			if (JusoRspConst.JUSO_OK_STATUS_CODE.equals(apiResponse.getCommon().getErrorCode())) {
				return null;  // 정상 처리
			} else {
				return new InternalServerErrorException(apiResponse.getCommon().getErrorMessage());
			}

		} catch (IOException e) {
			return new InternalServerErrorException(e.getMessage());
		}
	}
}
