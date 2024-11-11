package com.postvue.feelogserver.global.api.juso.addresssearch;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.postvue.feelogserver.global.api.juso.addresssearch.dto.JusoAddressSearchApiRsp;

@FeignClient(
	name = "jusoAddressSearchApiClient",
	url = "https://business.juso.go.kr/addrlink/addrLinkApi.do",
	configuration = JusoAddressSearchFeignClientConfiguration.class
)
public interface JusoAddressSearchApiClient {
	@GetMapping()
	JusoAddressSearchApiRsp getAddress(@RequestParam("confmKey") String confmKey,
		@RequestParam("currentPage") Integer currentPage,
		@RequestParam("countPerPage") Integer countPerPage,
		@RequestParam("keyword") String keyword,
		@RequestParam(value = "resultType", required = false) String resultType,
		@RequestParam(value = "firstSort", required = false) String firstSort);

}
