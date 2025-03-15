package com.postvue.feelogserver.core.security;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.postvue.feelogserver.core.security.exception.JwtTokenExpiredException;
import com.postvue.feelogserver.core.security.exception.JwtTokenValidException;
import com.postvue.feelogserver.domain.snsusers.dto.SnsUserDto;
import com.postvue.feelogserver.domain.snsusers.vo.SnsAppRole;
import com.postvue.feelogserver.global.constant.SystemPhraseConst;
import com.postvue.feelogserver.global.constant.SystemTimeConst;
import com.postvue.feelogserver.global.exception.InternalServerErrorException;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Header;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class JwtTokenProvider {

	// 엑세스 토큰 최대 유효 기간: 1시간
	private static final long ACCESS_TOKEN_EXPIRED_TIME = SystemTimeConst.SYSTEM_1_HOUR_TIME_BY_MILLISECOND;

	// 엑세스 토큰 최대 유효 기간: 30일
	private static final Long REFRESH_TOKEN_EXPIRED_TIME = SystemTimeConst.SYSTEM_1_MONTH_BY_MILLISECOND;

	// 가입 증명 코드 최대 유효 기간: 1시간
	private static final long REGISTER_VALIDATION_CODE_EXPIRED_TIME = SystemTimeConst.SYSTEM_1_HOUR_TIME_BY_MILLISECOND;

	private final String USER_ROLE_CLAIME_NAME = "role";

	@Value("${security.service.auth.JWT_SECRET_KEY}")
	private String secretKey;

	private Key encodeKey;

	@PostConstruct
	protected void init() {
		encodeKey = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
	}

	// 엑세스 토큰 생성
	public String createAccessToken(Long memberId, SnsAppRole role) {
		return createToken(memberId, role, ACCESS_TOKEN_EXPIRED_TIME);
	}

	// 리프레쉬 토큰 생성
	public String createRefreshToken(Long memberId, SnsAppRole role) {
		return createToken(memberId, role, REFRESH_TOKEN_EXPIRED_TIME);
	}

	// 가입 증명 토큰 코드 생성
	public String createRegisterValidationCode(SnsUserDto snsUserDto, SnsAppRole role) {
		ObjectMapper objectMapper = new ObjectMapper();
		try {
			return createValidationToken(objectMapper.writeValueAsString(snsUserDto), role);
		} catch (JsonProcessingException exception) {
			throw new InternalServerErrorException(SystemPhraseConst.INTERNAL_SERVER_VEXCEPTION_PHRASE);
		}
	}

	public SnsUserDto getRegisterValidationCode(String registerValidationCode) {
		ObjectMapper objectMapper = new ObjectMapper();
		try {
			return objectMapper.readValue(registerValidationCode, SnsUserDto.class);
		} catch (JsonProcessingException exception) {
			throw new InternalServerErrorException(SystemPhraseConst.INTERNAL_SERVER_VEXCEPTION_PHRASE);
		}
	}

	public CustomUserDetails getUserDetails(String token) {
		return CustomUserDetails.of(getSubjectByToken(token), getUserRole(token));
	}

	public Authentication getAuthentication(String token) {
		CustomUserDetails userDetails = CustomUserDetails.of(getSubjectByToken(token), getUserRole(token));
		userDetails.getRole();
		return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
	}

	public void validateToken(String token) {
		try {
			Jwts.parserBuilder()
				.setSigningKey(encodeKey)
				.build()
				.parseClaimsJws(token);
		} catch (ExpiredJwtException ex) {
			throw new JwtTokenExpiredException(ex);
		} catch (Exception ex) {
			throw new JwtTokenValidException(ex);
		}
	}

	public String getUserRole(String token) {
		return getClaimByToken(token).get(USER_ROLE_CLAIME_NAME).toString();
	}

	// 토큰 생성 메소드: 엑세스 토큰, 리프레쉬 토큰
	private String createToken(Long memberId, SnsAppRole sysAppRole, long tokenExpiredTime) {
		Date now = new Date();
		return Jwts.builder()
			.setHeaderParam(Header.TYPE, Header.JWT_TYPE)
			.setSubject(String.valueOf(memberId))
			.claim(USER_ROLE_CLAIME_NAME, sysAppRole.name())
			.setIssuedAt(now)
			.setExpiration(new Date(now.getTime() + tokenExpiredTime))
			.signWith(encodeKey, SignatureAlgorithm.HS256)
			.compact();
	}

	private String createValidationToken(String validationCodeString, SnsAppRole sysAppRole) {
		Date now = new Date();
		return Jwts.builder()
			.setHeaderParam(Header.TYPE, Header.JWT_TYPE)
			.setSubject(validationCodeString)
			.claim(USER_ROLE_CLAIME_NAME, sysAppRole.name())
			.setIssuedAt(now)
			.setExpiration(new Date(now.getTime() + JwtTokenProvider.REGISTER_VALIDATION_CODE_EXPIRED_TIME))
			.signWith(encodeKey, SignatureAlgorithm.HS256)
			.compact();
	}

	public String getSubjectByToken(String token) {
		return getClaimByToken(token).getSubject();
	}

	private Claims getClaimByToken(String token) {
		return Jwts.parserBuilder()
			.setSigningKey(encodeKey)
			.build()
			.parseClaimsJws(token)
			.getBody();
	}
}
