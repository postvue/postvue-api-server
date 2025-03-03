package com.postvue.feelogserver.global.api.kakao.client.localapi;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

import com.postvue.feelogserver.global.api.kakao.dto.rsp.KakaoSearchResponseDto;
import com.postvue.feelogserver.global.constant.KakaoApiConst;

@FeignClient(
	name = "kakaoLocalApi",
	url = KakaoApiConst.kakaoLocalApi
)
public interface KakaoLocalApiClient {
	@GetMapping("/v2/local/search/keyword.json")
	KakaoSearchResponseDto getLocalSearch(
		@RequestHeader(HttpHeaders.AUTHORIZATION) String restApi,
		@RequestParam("query") String query,                        // 검색어
		@RequestParam("x") float x,                                 // 경도
		@RequestParam("y") float y,                                 // 위도
		@RequestParam("page") float page,                           // 반경
		@RequestParam("size") float size                            // 반경
	);

	@GetMapping("/v2/local/search/keyword.json")
	KakaoSearchResponseDto getLocalSearchNotGis(
		@RequestHeader(HttpHeaders.AUTHORIZATION) String restApi,
		@RequestParam("query") String query,                        // 검색어
		@RequestParam("page") float page,                           // 반경
		@RequestParam("size") float size                            // 반경
	);
}
