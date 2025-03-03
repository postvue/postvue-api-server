package com.postvue.feelogserver.global.api.apple.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class AppleRevokeRequestDto {
    private String refreshToken; // Apple에서 받은 refresh_token
}