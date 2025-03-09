package com.postvue.feelogserver.app.auth.service;

import com.postvue.feelogserver.domain.snsusers.dto.SnsUserDto;
import com.postvue.feelogserver.global.api.apple.AppleApiClient;
import com.postvue.feelogserver.global.api.apple.dto.AppleRevokeRequestDto;
import com.postvue.feelogserver.global.api.apple.dto.AppleRevokeResponseDto;
import com.postvue.feelogserver.global.api.apple.dto.AppleTokenRequestDto;
import com.postvue.feelogserver.global.api.apple.dto.AppleTokenResponseDto;
import com.postvue.feelogserver.global.api.apple.dto.AppleUserResponseDto;
import com.postvue.feelogserver.global.exception.BadRequestErrorException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.RSAPublicKeySpec;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public class AppleAuthService {

    private final AppleApiClient appleApiClient;

    private final static String APPLE_AUTH_URL = "https://appleid.apple.com";
    private final static String grantType = "authorization_code";

    @Value("${openapi.appledevelopers.appleSocialServiceClientId}")
    private String clientId;

    @Value("${openapi.appledevelopers.appleTeamId}")
    private String teamId;

    @Value("${openapi.appledevelopers.appleFeelogApiKeyId}")
    private String keyId;

    @Value("${openapi.appledevelopers.appleFeelogApiPrivateKey}")
    private String privateKeyString;

    @Value("${serverUrl.appleAuthRedirectUrl}")
    private String redirectUrl;

    // Apple의 공개 키를 사용하여 id_token 검증
    public AppleUserResponseDto verifyIdToken(String idToken) {
        try {
            // Apple 공개 키 가져오기
            Map<String, Object> applePublicKeys = appleApiClient.getApplePublicKeys();

            // JWT 헤더 디코딩
            String[] parts = idToken.split("\\.");
            String headerJson = new String(Base64.getDecoder().decode(parts[0]));
            String kid = extractValueFromJson(headerJson, "kid");
            String alg = extractValueFromJson(headerJson, "alg");

            // kid, alg에 해당하는 공개 키 가져오기
            PublicKey publicKey = getPublicKey(kid, alg, applePublicKeys);

            // JWT 검증 및 파싱
            Claims claims = Jwts.parserBuilder()
                .setSigningKey(publicKey)
                .build()
                .parseClaimsJws(idToken)
                .getBody();

            return AppleUserResponseDto.builder()
                .sub(claims.getSubject())  // Apple의 사용자 고유 ID
                .email(claims.get("email", String.class))
                .build();
        } catch (Exception e) {
            throw new BadRequestErrorException("Apple ID 인증 실패");
        }
    }

    // Apple의 OAuth 2.0을 사용해 access_token 가져오기
    public AppleTokenResponseDto getAppleAccessToken(String authorizationCode) {
        AppleTokenRequestDto request = AppleTokenRequestDto.builder()
            .clientId(clientId)
            .clientSecret(generateClientSecret())
            .code(authorizationCode)
            .grantType(grantType)
            .redirectUri(redirectUrl)
            .build();

        return appleApiClient.getAppleToken(Map.of(
            "client_id", request.getClientId(),
            "client_secret", request.getClientSecret(),
            "code", request.getCode(),
            "grant_type", request.getGrantType(),
            "redirect_uri", request.getRedirectUri()
        ));
    }

    // Apple 계정 연결 해제
    public AppleRevokeResponseDto revokeAppleAccount(
        AppleRevokeRequestDto request,
        Optional<SnsUserDto> userDtoOptional) {
        if (userDtoOptional.isEmpty()) {
            return AppleRevokeResponseDto.builder()
                .success(false)
                .message("User not found")
                .build();
        }

        try {
            // Apple API에 refresh_token을 보내 계정 연결 해제 요청
            appleApiClient.revokeAppleAccount(Map.of(
                "client_id", clientId,
                "client_secret", generateClientSecret(),
                "token", request.getRefreshToken(),
                "token_type_hint", "refresh_token"
            ));

            return AppleRevokeResponseDto.builder()
                .success(true)
                .message("Apple account successfully unlinked")
                .build();
        } catch (Exception e) {
            return AppleRevokeResponseDto.builder()
                .success(false)
                .message("Failed to unlink Apple account: " + e.getMessage())
                .build();
        }
    }

    // Apple 공개 키에서 kid에 해당하는 키 가져오기
    private PublicKey getPublicKey(String kid, String alg, Map<String, Object> applePublicKeys) throws Exception {
        List<Map<String, String>> keys = (List<Map<String, String>>) applePublicKeys.get("keys");

        for (Map<String, String> key : keys) {
            if (kid.equals(key.get("kid")) && alg.equals(key.get("alg"))) {
                byte[] nBytes = Base64.getUrlDecoder().decode(key.get("n"));
                byte[] eBytes = Base64.getUrlDecoder().decode(key.get("e"));

                RSAPublicKeySpec spec = new RSAPublicKeySpec(new java.math.BigInteger(1, nBytes), new java.math.BigInteger(1, eBytes));
                KeyFactory keyFactory = KeyFactory.getInstance("RSA");

                return keyFactory.generatePublic(spec);
            }
        }
        throw new RuntimeException("Apple 공개 키를 찾을 수 없습니다.");
    }

    private String extractValueFromJson(String json, String key) {
        return json.split("\"" + key + "\":\"")[1].split("\"")[0];
    }

    // Apple JWT(Client Secret) 생성
    public String generateClientSecret() {
        try {
            PrivateKey privateKey = loadPrivateKey();
            long now = System.currentTimeMillis();

            LocalDateTime expiration = LocalDateTime.now().plusMinutes(5);

            return Jwts.builder()
                .setIssuer(teamId) // Apple Team ID
                .setIssuedAt(new Date(now)) // 발급 시간
                .setExpiration(Date.from(expiration.atZone(ZoneId.systemDefault()).toInstant()))
                .setAudience(APPLE_AUTH_URL)
                .setSubject(clientId) // Apple Client ID
                .claim("kid", keyId) // Key ID 포함
                .signWith(privateKey, SignatureAlgorithm.ES256) // 서명
                .compact();
        } catch (Exception e) {
            throw new BadRequestErrorException("Apple Client Secret 생성 실패", e);
        }
    }

    // private-key.p8 로드 (EC 키 지원)
    private PrivateKey loadPrivateKey() throws Exception {
        // 환경 변수에서 가져온 Private Key의 개행 문자 정리
        String privateKeyPEM = privateKeyString
            .replace("-----BEGIN PRIVATE KEY-----", "")
            .replace("-----END PRIVATE KEY-----", "")
            .replaceAll("\\s+", ""); // 공백 제거

        byte[] decodedKey = Base64.getDecoder().decode(privateKeyPEM);
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(decodedKey);
        KeyFactory keyFactory = KeyFactory.getInstance("EC"); // ✅ EC 키 처리

        return keyFactory.generatePrivate(keySpec);
    }
}