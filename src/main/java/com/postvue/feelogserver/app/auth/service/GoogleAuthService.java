package com.postvue.feelogserver.app.auth.service;

import java.util.Map;

import org.springframework.stereotype.Service;

import com.postvue.feelogserver.app.auth.service.dto.GoogleUserInfoDto;
import com.postvue.feelogserver.global.api.google.oauth.GoogleOauthApiClient;
import com.postvue.feelogserver.global.api.google.oauth.dto.GoogleAuthResponseDto;
import com.postvue.feelogserver.global.exception.BadRequestErrorException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public class GoogleAuthService {
    private final GoogleOauthApiClient googleOauthApiClient;

    // Apple의 공개 키를 사용하여 id_token 검증
    public GoogleUserInfoDto getGoogleUserInfoByIdToken(String idToken) {

        // Google 서버에 요청하여 토큰 검증
        GoogleAuthResponseDto googleAuthResponseDto = googleOauthApiClient.getGoogleTokenInfo(idToken);

        if (googleAuthResponseDto == null || googleAuthResponseDto.getEmail() == null || googleAuthResponseDto.getSub() == null) {
            throw new BadRequestErrorException("Invalid Google token");
        }

        String email = googleAuthResponseDto.getEmail();
        String name = googleAuthResponseDto.getName();
        String sub = googleAuthResponseDto.getSub();
        String picture = googleAuthResponseDto.getPicture();

        return new GoogleUserInfoDto(email, name, sub, picture);
    }
}