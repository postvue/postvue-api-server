package com.postvue.feelogserver.global.api.naver;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

import com.postvue.feelogserver.global.api.naver.dto.rsp.NaverLocalSearchResponseDto;
import com.postvue.feelogserver.global.api.naver.dto.rsp.NaverUserInfo;

@FeignClient(
	name = "naveruserinfo",
	url = "https://openapi.naver.com",
	configuration = NaverFeignClientConfiguration.class
)
public interface NaverApiClient {

	@GetMapping("/v1/nid/me")
	NaverUserInfo getUserInfo(@RequestHeader(HttpHeaders.AUTHORIZATION) String accessToken);

	@GetMapping("/v1/search/local.json")
	NaverLocalSearchResponseDto getLocalSearch(
		@RequestHeader("X-Naver-Client-Id") String clientId,       // 네이버 클라이언트 ID
		@RequestHeader("X-Naver-Client-Secret") String clientSecret, // 네이버 클라이언트 시크릿
		@RequestParam("query") String query,                        // 검색어
		@RequestParam("display") int display                       // 표시할 결과 수
	);
}

