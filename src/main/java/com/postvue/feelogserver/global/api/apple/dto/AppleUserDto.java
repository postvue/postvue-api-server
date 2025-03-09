package com.postvue.feelogserver.global.api.apple.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AppleUserDto {
    private String sub;   // Apple 사용자 고유 ID
    private String email; // 이메일
}
