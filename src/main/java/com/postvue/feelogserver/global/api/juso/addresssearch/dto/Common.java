package com.postvue.feelogserver.global.api.juso.addresssearch.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Common {
	private String errorMessage;
	private String countPerPage;
	private String totalCount;
	private String errorCode;
	private String currentPage;
}
