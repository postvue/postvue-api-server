package com.postvue.feelogserver.app.auth.controller;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.postvue.feelogserver.app.auth.dto.TokenResponse;
import com.postvue.feelogserver.app.auth.dto.req.post.KakaoLoginReq;
import com.postvue.feelogserver.app.auth.dto.req.post.NaverLoginReq;
import com.postvue.feelogserver.app.auth.dto.req.post.SignupUserInfoReq;
import com.postvue.feelogserver.app.auth.service.AuthService;
import com.postvue.feelogserver.core.security.CustomUserDetails;
import com.postvue.feelogserver.domain.snsusers.SnsUser;
import com.postvue.feelogserver.domain.snsusers.dto.SnsUserDto;
import com.postvue.feelogserver.domain.snsusers.vo.SignUpType;
import com.postvue.feelogserver.global.api.kakao.KakaoApiClient;
import com.postvue.feelogserver.global.api.kakao.dto.rsp.KakaoUserInfo;
import com.postvue.feelogserver.global.api.naver.NaverApiClient;
import com.postvue.feelogserver.global.api.naver.dto.rsp.NaverUserInfo;
import com.postvue.feelogserver.global.constant.CookieConst;
import com.postvue.feelogserver.global.http.response.serverresponse.ServerDeleteRsp;
import com.postvue.feelogserver.global.http.response.serverresponse.ServerGetOkRsp;
import com.postvue.feelogserver.global.http.response.serverresponse.ServerPostCreatedRsp;
import com.postvue.feelogserver.global.util.generator.AuthorizationUtils;
import com.postvue.feelogserver.global.util.generator.CookieUtils;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
	@Value("${security.service.auth.COOKIE_MAX_AGE_REFRESH_TOKEN}")
	private Integer COOKIE_MAX_AGE_REFRESH_TOKEN;

	private final AuthService authService;
	private final KakaoApiClient kakaoApiClient;
	private final NaverApiClient naverApiClient;

	// 엑세스 토큰 갱신
	@PostMapping("/renewal/tokens")
	public ServerPostCreatedRsp<String> renewTokens(
		@Valid @NotNull @CookieValue(name = CookieConst.REFRESH_TOKEN, required = false) String refreshToken,
		HttpServletResponse response) {
		TokenResponse tokens = authService.renewTokens(refreshToken);

		response.addCookie(
			CookieUtils.createCookie(CookieConst.REFRESH_TOKEN, tokens.refreshToken(), COOKIE_MAX_AGE_REFRESH_TOKEN));

		return new ServerPostCreatedRsp<>(tokens.accessToken());
	}

	// 카카오 로그인
	@PostMapping("/login/kakao")
	public ServerGetOkRsp<String> kakaoLogin(@Valid @RequestBody KakaoLoginReq request,
		HttpServletResponse response) {

		// 외부 api: 카카오 정보 조회
		KakaoUserInfo kakaoUserInfo = kakaoApiClient.getUserInfo(
			AuthorizationUtils.returnBearerByAccessToken(request.kakaoAccessToken()));

		Optional<SnsUserDto> optionalUserDto = authService.findBySocialLogin(kakaoUserInfo.getId(), SignUpType.KAKAO);

		SnsUserDto snsUserDto = authService.processLoginBySocialLogin(optionalUserDto,
			response, kakaoUserInfo.toUserDto());

		TokenResponse tokens = authService.createJwtTokens(snsUserDto.snsUserId(), snsUserDto.snsAppRole());

		response.addCookie(
			CookieUtils.createCookie(CookieConst.REFRESH_TOKEN, tokens.refreshToken(), COOKIE_MAX_AGE_REFRESH_TOKEN));

		return new ServerGetOkRsp<>(tokens.accessToken());
	}

	// 네이버 로그인
	@PostMapping("/login/naver")
	public ServerGetOkRsp<String> naverLogin(@Valid @RequestBody NaverLoginReq request,
		HttpServletResponse response) {

		// 외부 api: 네이버 정보 조회
		NaverUserInfo naverUserInfo = naverApiClient.getUserInfo(
			AuthorizationUtils.returnBearerByAccessToken(request.naverAccessToken()));

		Optional<SnsUserDto> optionalUserDto = authService.findBySocialLogin(naverUserInfo.getNaverUserDetail().getId(),
			SignUpType.NAVER);

		SnsUserDto snsUserDto = authService.processLoginBySocialLogin(optionalUserDto,
			response, naverUserInfo.toUserDto());

		TokenResponse tokens = authService.createJwtTokens(snsUserDto.snsUserId(), snsUserDto.snsAppRole());

		response.addCookie(
			CookieUtils.createCookie(CookieConst.REFRESH_TOKEN, tokens.refreshToken(), COOKIE_MAX_AGE_REFRESH_TOKEN));

		return new ServerGetOkRsp<>(tokens.accessToken());
	}

	// 회원가입
	@PostMapping("/signup")
	public ServerPostCreatedRsp<String> signupBySocialLogin(
		@Valid @NotNull @CookieValue(value = CookieConst.REGISTRATION_VERIFICATION_CODE) String registrationVerificationCode,
		@Valid @RequestBody SignupUserInfoReq signupUserInfoReq,
		HttpServletResponse response) {

		SnsUser snsUser = authService.processSignupBySocialLogin(registrationVerificationCode, signupUserInfoReq);
		TokenResponse tokens = authService.createJwtTokens(snsUser.getId(), snsUser.getSnsAppRole());

		response.addCookie(
			CookieUtils.createCookie(CookieConst.REFRESH_TOKEN, tokens.refreshToken(), COOKIE_MAX_AGE_REFRESH_TOKEN));

		response.addCookie(
			CookieUtils.createCookie(CookieConst.SIGNUP_TYPE, "",
				0, false)); // 가입 종류 쿠키 값 제거

		response.addCookie(
			CookieUtils.createCookie(CookieConst.REGISTRATION_VERIFICATION_CODE, "",
				0)); // 가입 증명 코드 제거

		return new ServerPostCreatedRsp<>(tokens.accessToken());
	}

	// 로그아웃
	@PostMapping("/logout")
	public ServerGetOkRsp<String> logout(@AuthenticationPrincipal CustomUserDetails userDetails,
		HttpServletResponse response) {
		Long snsUserId = (userDetails == null) ? null : Long.valueOf(userDetails.getUserId());
		authService.deleteRefreshToken(Long.valueOf(snsUserId));
		Cookie cookie = CookieUtils.deleteRefreshToken();
		response.addCookie(cookie);
		return new ServerGetOkRsp<>(String.valueOf(snsUserId));
	}

	// 회원 탈퇴
	@DeleteMapping("/member-withdrawal")
	public ServerDeleteRsp<String> withdrawal(@AuthenticationPrincipal CustomUserDetails userDetails) {
		String userId = userDetails.getUserId();
		authService.withdrawal(Long.valueOf(userId));
		return new ServerDeleteRsp<>("회원이 탈퇴 되었습니다.");
	}
}
