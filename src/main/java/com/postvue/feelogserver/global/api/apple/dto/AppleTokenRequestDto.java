package com.postvue.feelogserver.global.api.apple.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AppleTokenRequestDto {
    private String clientId;
    private String clientSecret;
    private String code;
    private String grantType;
    private String redirectUri;
}