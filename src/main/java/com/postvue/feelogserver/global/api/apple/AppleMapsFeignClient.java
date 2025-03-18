package com.postvue.feelogserver.global.api.apple;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

import com.postvue.feelogserver.global.api.apple.dto.AppleMapsGeocodeResponse;
import com.postvue.feelogserver.global.api.apple.dto.AppleMapsTokenResponse;

@FeignClient(name = "apple-maps", url = "https://maps-api.apple.com")
public interface AppleMapsFeignClient {

    @GetMapping("/v1/token")
    AppleMapsTokenResponse getToken(
        @RequestHeader("Authorization") String authorization
    );


    @GetMapping("/v1/geocode")
    AppleMapsGeocodeResponse geocode(
            @RequestHeader("Authorization") String authorization,
            @RequestParam("q") String query
    );

    @GetMapping("/v1/reverseGeocode")
    String reverseGeocode(
            @RequestHeader("Authorization") String authorization,
            @RequestParam("loc") String coordinates
    );
}
