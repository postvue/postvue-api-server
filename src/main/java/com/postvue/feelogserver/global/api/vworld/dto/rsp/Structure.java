package com.postvue.feelogserver.global.api.vworld.dto.rsp;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Structure {
	private String level0; // 국가 ex) 대한민구
	private String level1; // 시·도 ex) 경기도
	private String level2; // 시·군·구 ex) 화성시
	private String level3; // 시·군·구 ex) ""
	private String level4L; // (도로)도로명, (지번)법정읍·면·동 명 ex) 목동
	private String level4LC; // (도로)도로코드, (지번)법정읍·면·동 코드 ex) "4159013400"
	private String level4A; // (도로)행정읍·면·동 명, (지번)지원안함 ex) 동탄9동
	private String level4AC; // (도로)행정읍·면·동 코드, (지번)지원안함 ex) 4159063000
	private String level5; // (도로)길, (지번)번지 ex) 481
	private String detail; // 상세주소
}
