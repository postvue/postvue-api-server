package com.postvue.feelogserver.app.maps.service;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.nimbusds.jose.util.StandardCharset;
import com.postvue.feelogserver.app.auth.service.AppleAuthService;
import com.postvue.feelogserver.global.api.apple.AppleMapsFeignClient;
import com.postvue.feelogserver.global.api.apple.dto.AppleMapsGeocodeResponse;
import com.postvue.feelogserver.global.api.apple.dto.AppleMapsTokenResponse;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AppleMapsService {

	private final AppleMapsFeignClient appleMapsFeignClient;

	@Value("${openapi.appledevelopers.appleMapApiKey}")
	private String appleMapApiKey;

	public AppleMapsGeocodeResponse getGeocode(String query) {
		AppleMapsTokenResponse response = appleMapsFeignClient.getToken("Bearer " + appleMapApiKey);
		// String encodedQuery = URLEncoder.encode(query, StandardCharsets.UTF_8);
		return appleMapsFeignClient.geocode("Bearer " + response.getAccessToken(), query);
	}

	public String getReverseGeocode(String coordinates) {
		return appleMapsFeignClient.reverseGeocode(null, coordinates);
	}
}