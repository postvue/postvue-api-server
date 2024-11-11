package com.postvue.feelogserver.global.api.juso.addresssearch.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Juso {
	private String detBdNmList; // 상세 건물명
	private String rn; // 도로명
	private String emdNm; // 읍면동명
	private String zipNo; //우편번호
	private String roadAddr; // 전체 도로명주소
	private String roadAddrPart1; // 도로명주소(참고항목 제외)
	private String roadAddrPart2; // 도로명주소 참고항목
	private String jibunAddr; // 지번주소
	private String engAddr; // 도로명 주소(영문)
	private String bdNm; // 건물명
	private String emdNo; // 읍면동일련번호
	private String sggNm; // 시군구명
	private String siNm; // 시도명
	private String admCd; // 행정구역코드
	private String udrtYn; // 지하여부(0 : 지상, 1 : 지하)
	private String buldMnnm; // 건물본번
	private String buldSlno; // 건물부번
	private String lnbrMnnm; // 지번본번(번지)
	private String lnbrSlno; // 지번부번(호)
	private String bdKdcd; // 공동주택여부(1 : 공동주택, 0 : 비공동주택)
	private String liNm; // 법정리명
	private String rnMgtSn; // 도로명코드
	private String mtYn; // 산여부(0 : 대지, 1 : 산)
	private String bdMgtSn; // 건물관리번호
}
