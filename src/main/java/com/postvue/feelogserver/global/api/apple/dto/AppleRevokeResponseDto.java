package com.postvue.feelogserver.global.api.apple.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AppleRevokeResponseDto {
    private boolean success;
    private String message;
}