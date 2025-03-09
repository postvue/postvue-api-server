package com.postvue.feelogserver.global.api.google.oauth.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GoogleAuthResponseDto {
    private String sub;       // Google 사용자 고유 ID
    private String email;     // 사용자 이메일
    private String name;      // 사용자 이름
    private String picture;   // 프로필 사진 URL
}