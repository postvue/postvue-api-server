package com.postvue.feelogserver.global.api.vworld.dto.rsp;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VworldAddressResponse {
	private ServiceInfo service;
	private String status; // 응답 메시지 상태 ex) OK, NOT_FOUND, ERROR
	private InputInfo input;
	private List<VworldGetAddressResultRsp> result;
}
