package com.postvue.feelogserver.global.api.google.oauth;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.postvue.feelogserver.global.api.google.oauth.dto.GoogleAuthResponseDto;

@FeignClient(
	name = "googleOauthClient",
	url = "https://oauth2.googleapis.com"
)
public interface GoogleOauthApiClient {

	@GetMapping("/tokeninfo")
	GoogleAuthResponseDto getGoogleTokenInfo(@RequestParam("access_token") String idToken);
}

