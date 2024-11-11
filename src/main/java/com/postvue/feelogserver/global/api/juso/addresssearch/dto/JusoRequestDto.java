package com.postvue.feelogserver.global.api.juso.addresssearch.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class JusoRequestDto {
	@NotNull// 필수 필드
	private String confmKey;

	@NotNull // 필수 필드
	private Integer currentPage;

	@NotNull // 필수 필드
	private Integer countPerPage;

	@NotNull // 필수 필드
	private String keyword;

	private String resultType;

	// 필수가 아닌 필드들 (값이 없어도 허용)
	private String firstSort;
	private String hstryYn;
	private String addInfoYn;
}
