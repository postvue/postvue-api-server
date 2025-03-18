package com.postvue.feelogserver.global.api.apple.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class AppleMapsTokenResponse {
    
    @JsonProperty("accessToken")
    private String accessToken;

    @JsonProperty("expiresInSeconds")
    private int expiresInSeconds;

    public String getAccessToken() {
        return accessToken;
    }

    public int getExpiresInSeconds() {
        return expiresInSeconds;
    }
}
