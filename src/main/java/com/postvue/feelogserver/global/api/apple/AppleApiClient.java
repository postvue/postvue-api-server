package com.postvue.feelogserver.global.api.apple;

import java.util.Map;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.postvue.feelogserver.global.api.apple.dto.AppleTokenResponseDto;

@FeignClient(
	name = "appleClient",
	url = "https://appleid.apple.com"
)
public interface AppleApiClient {

	// Apple OAuth 2.0 토큰 요청
	@PostMapping(value = "/auth/token", consumes = "application/x-www-form-urlencoded")
	AppleTokenResponseDto getAppleToken(@RequestParam Map<String, ?> request);

	// Apple 공개 키 가져오기
	@GetMapping("/auth/keys")
	Map<String, Object> getApplePublicKeys();

	// Apple 계정 연결 해제 (revoke)
	@PostMapping(value = "/auth/revoke", consumes = "application/x-www-form-urlencoded")
	void revokeAppleAccount(@RequestParam Map<String, ?> request);
}

