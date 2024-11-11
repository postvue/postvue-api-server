package com.postvue.feelogserver.global.api.kakao;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

import com.postvue.feelogserver.global.api.kakao.dto.rsp.KakaoUserInfo;
import com.postvue.feelogserver.global.constant.KakaoApiConst;

@FeignClient(
	name = "kakaouserinfo",
	url = KakaoApiConst.kakaoApiUrl,
	configuration = KakaoFeignClientConfiguration.class
)
public interface KakaoApiClient {
	@GetMapping("/v2/user/me")
	KakaoUserInfo getUserInfo(@RequestHeader(HttpHeaders.AUTHORIZATION) String accessToken);
}
