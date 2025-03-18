package com.postvue.feelogserver.endpoint;

import java.time.LocalDateTime;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import com.nimbusds.oauth2.sdk.TokenRequest;
import com.postvue.feelogserver.app.auth.dto.TokenResponse;
import com.postvue.feelogserver.app.auth.service.AuthService;
import com.postvue.feelogserver.core.security.CustomUserDetails;
import com.postvue.feelogserver.core.security.JwtTokenProvider;
import com.postvue.feelogserver.domain.snsusers.SnsUser;
import com.postvue.feelogserver.domain.snsusers.dto.SnsUserDto;
import com.postvue.feelogserver.domain.snsusers.repository.SnsUserRepository;
import com.postvue.feelogserver.domain.snsusers.vo.SnsAppRole;
import com.postvue.feelogserver.endpoint.common.AuthTokenValidProcess;
import com.postvue.feelogserver.global.constant.LogTemplateConst;
import com.postvue.feelogserver.global.exception.BadRequestErrorException;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import com.vaadin.hilla.Endpoint;
import com.vaadin.hilla.Nonnull;
import com.vaadin.hilla.exception.EndpointException;

import lombok.RequiredArgsConstructor;

@Endpoint
@Service
@RequiredArgsConstructor
public class AuthEndpoint {
    private final AuthService authService;
    private final AuthTokenValidProcess authTokenValidProcess;

    @AnonymousAllowed
    public @Nonnull String login(@Nonnull LoginRequest request) {
        try {
            // 사용자 인증
            SnsUserDto userDto = authService.findByEmail(request.email, request.password);
            if (userDto.snsAppRole() != SnsAppRole.ROLE_ADMIN){
                throw new BadRequestErrorException("접근 권한이 없습니다.");
            }
            TokenResponse tokens = authService.createJwtTokens(userDto.snsUserId(), userDto.snsAppRole());

            // 인증 성공 시 JWT 발급
            return tokens.accessToken();
        }
        catch (Exception e){
            throw new EndpointException("서버 오류 발생", LogTemplateConst.getLogInfoTemplate(e.getMessage() + "_" + e.toString(), LocalDateTime.now().toString()));
        }
    }

    @AnonymousAllowed
    public UserInfo getUser(@Nonnull TokenRequest request) {
        try {
            SnsUser snsUser = authTokenValidProcess.processValidToken(request.token);
            return new UserInfo(snsUser.getId(), snsUser.getSignupEmail(), snsUser.getSnsAppRole());
        }
        catch (Exception e){
            throw new EndpointException("서버 오류 발생", LogTemplateConst.getLogInfoTemplate(e.getMessage() + "_" + e.toString(), LocalDateTime.now().toString()));
        }
    }

    public static class TokenRequest {
        public String token;
    }

    public static class UserInfo {
        public Long id;
        public String email;
        public SnsAppRole role;

        public UserInfo(Long id, String email, SnsAppRole role) {
            this.id = id;
            this.email = email;
            this.role = role;
        }
    }

    public static class LoginRequest {
        public String email;
        public String password;
    }
}
