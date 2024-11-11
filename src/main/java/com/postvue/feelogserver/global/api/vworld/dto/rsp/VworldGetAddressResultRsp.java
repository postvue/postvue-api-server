package com.postvue.feelogserver.global.api.vworld.dto.rsp;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class VworldGetAddressResultRsp {
	private String zipcode; // 우편 번호
	private String type; // 주소 유형
	private String text; // 전체 주소 텍스트
	private Structure structure; // 구조화된 주소
}
