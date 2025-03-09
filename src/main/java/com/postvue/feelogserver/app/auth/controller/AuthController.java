package com.postvue.feelogserver.app.auth.controller;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.postvue.feelogserver.app.auth.dto.TokenResponse;
import com.postvue.feelogserver.app.auth.dto.req.delete.AuthMemberWithdrawalReq;
import com.postvue.feelogserver.app.auth.dto.req.post.AppleLoginReq;
import com.postvue.feelogserver.app.auth.dto.req.post.EmailInfoReq;
import com.postvue.feelogserver.app.auth.dto.req.post.EmailSignupVerifyReq;
import com.postvue.feelogserver.app.auth.dto.req.post.GoogleLoginReq;
import com.postvue.feelogserver.app.auth.dto.req.post.KakaoLoginReq;
import com.postvue.feelogserver.app.auth.dto.req.post.NaverLoginReq;
import com.postvue.feelogserver.app.auth.dto.req.post.SignupUserInfoReq;
import com.postvue.feelogserver.app.auth.dto.rsp.AuthTokenRsp;
import com.postvue.feelogserver.app.auth.service.AppleAuthService;
import com.postvue.feelogserver.app.auth.service.AuthService;
import com.postvue.feelogserver.app.auth.service.GoogleAuthService;
import com.postvue.feelogserver.app.auth.service.dto.GoogleUserInfoDto;
import com.postvue.feelogserver.app.openapis.req.DiscordWebhookRequest;
import com.postvue.feelogserver.app.openapis.service.DiscordService;
import com.postvue.feelogserver.core.security.CustomUserDetails;
import com.postvue.feelogserver.core.security.JwtTokenProvider;
import com.postvue.feelogserver.domain.snsusers.SnsUser;
import com.postvue.feelogserver.domain.snsusers.dto.SnsUserDto;
import com.postvue.feelogserver.domain.snsusers.vo.SignUpType;
import com.postvue.feelogserver.global.api.apple.dto.AppleUserResponseDto;
import com.postvue.feelogserver.global.api.kakao.KakaoApiClient;
import com.postvue.feelogserver.global.api.kakao.dto.rsp.KakaoUserInfo;
import com.postvue.feelogserver.global.api.naver.NaverApiClient;
import com.postvue.feelogserver.global.api.naver.dto.rsp.NaverUserInfo;
import com.postvue.feelogserver.global.constant.CookieConst;
import com.postvue.feelogserver.global.exception.BadRequestErrorException;
import com.postvue.feelogserver.global.exception.BaseException;
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
	private final AppleAuthService appleAuthService;
	private final GoogleAuthService googleAuthService;
	private final KakaoApiClient kakaoApiClient;
	private final NaverApiClient naverApiClient;

	private final JwtTokenProvider jwtTokenProvider;

	private final DiscordService discordService;

	// @VERIFY1
	@GetMapping("/check/signup/qual")
	public ServerGetOkRsp<Boolean> getCheckSignupQual(
		@Valid @NotNull @CookieValue(value = CookieConst.REGISTRATION_VERIFICATION_CODE) String registrationVerificationCode) {

		// @CHECK1
		jwtTokenProvider.validateToken(registrationVerificationCode);
		return new ServerGetOkRsp<>(true);
	}

	// @VERIFY1
	// 엑세스 토큰 갱신
	@PostMapping("/renewal/tokens")
	public ServerPostCreatedRsp<AuthTokenRsp> renewTokens(
		@Valid @NotNull @CookieValue(name = CookieConst.REFRESH_TOKEN, required = false) String refreshToken,
		HttpServletResponse response) {
		TokenResponse tokens = authService.renewTokens(refreshToken);

		response.addCookie(
			CookieUtils.createCookie(CookieConst.REFRESH_TOKEN, tokens.refreshToken(), COOKIE_MAX_AGE_REFRESH_TOKEN));

		return new ServerPostCreatedRsp<>(
			new AuthTokenRsp(tokens.accessToken(),tokens.refreshToken(),tokens.userId().toString())
		);
	}

	// @VERIFY1
	//이메일 로그인
	@PostMapping("/login/email")
	public ServerGetOkRsp<AuthTokenRsp> emailLogin(@Valid @RequestBody EmailInfoReq emailInfoReq,
		HttpServletResponse response) {

		SnsUserDto userDto = authService.findByEmail(emailInfoReq.email(), emailInfoReq.password());
		TokenResponse tokens = authService.createJwtTokens(userDto.snsUserId(), userDto.snsAppRole());

		response.addCookie(
			CookieUtils.createCookie(CookieConst.REFRESH_TOKEN, tokens.refreshToken(), COOKIE_MAX_AGE_REFRESH_TOKEN));

		return new ServerGetOkRsp<>(
			new AuthTokenRsp(tokens.accessToken(), tokens.refreshToken(), tokens.userId().toString())
		);
	}

	// VERIFY1
	// 카카오 로그인
	@PostMapping("/login/kakao")
	public ServerGetOkRsp<AuthTokenRsp> kakaoLogin(@Valid @RequestBody KakaoLoginReq request,
		HttpServletResponse response) {

		// @CHECK1
		// 외부 api: 카카오 정보 조회
		KakaoUserInfo kakaoUserInfo = kakaoApiClient.getUserInfo(
			AuthorizationUtils.returnBearerByAccessToken(request.kakaoAccessToken()));


		// @CHECK2
		Optional<SnsUserDto> optionalUserDto = authService.findBySocialLogin(kakaoUserInfo.getId(), SignUpType.KAKAO);


		// @CHECK3
		return getAuthTokenRspServerGetOkRsp(response, optionalUserDto, kakaoUserInfo.toUserDto());
	}

	// @VERIFY1
	// 네이버 로그인
	@PostMapping("/login/naver")
	public ServerGetOkRsp<AuthTokenRsp> naverLogin(@Valid @RequestBody NaverLoginReq request,
		HttpServletResponse response) {

		// @CHECK1
		// 외부 api: 네이버 정보 조회
		NaverUserInfo naverUserInfo = naverApiClient.getUserInfo(
			AuthorizationUtils.returnBearerByAccessToken(request.naverAccessToken()));

		// CHECK2
		Optional<SnsUserDto> optionalUserDto = authService.findBySocialLogin(naverUserInfo.getNaverUserDetail().getId(),
			SignUpType.NAVER);

		return getAuthTokenRspServerGetOkRsp(response, optionalUserDto, naverUserInfo.toUserDto());
	}

	@PostMapping("/login/apple")
	public ServerGetOkRsp<AuthTokenRsp> appleLogin(@Valid @RequestBody AppleLoginReq request,
		HttpServletResponse response) {

		// @CHECK1
		// 외부 api: 네이버 정보 조회
		AppleUserResponseDto appleUserResponseDto = appleAuthService.verifyIdToken(request.idToken());

		// CHECK2
		Optional<SnsUserDto> optionalUserDto = authService.findBySocialLogin(appleUserResponseDto.getSub(),
			SignUpType.APPLE);

		return getAuthTokenRspServerGetOkRsp(response, optionalUserDto, appleUserResponseDto.toUserDto());
	}

	@PostMapping("/login/google")
	public ServerGetOkRsp<AuthTokenRsp> googleLogin(
		@Valid @RequestBody GoogleLoginReq request, HttpServletResponse response) {

		// @CHECK1
		// 외부 api: 네이버 정보 조회
		GoogleUserInfoDto googleUserInfoDto =  googleAuthService.getGoogleUserInfoByIdToken(request.idToken());

		// CHECK2
		Optional<SnsUserDto> optionalUserDto = authService.findBySocialLogin(googleUserInfoDto.getSub(),
			SignUpType.GOOGLE);

		return getAuthTokenRspServerGetOkRsp(response, optionalUserDto, googleUserInfoDto.toUserDto());
	}

	private ServerGetOkRsp<AuthTokenRsp> getAuthTokenRspServerGetOkRsp(HttpServletResponse response,
		Optional<SnsUserDto> optionalUserDto, SnsUserDto userDto) {
		SnsUserDto snsUserDto = authService.processLoginBySocialLogin(optionalUserDto,
			response, userDto);

		TokenResponse tokens = authService.createJwtTokens(snsUserDto.snsUserId(), snsUserDto.snsAppRole());

		response.addCookie(
			CookieUtils.createCookie(CookieConst.REFRESH_TOKEN, tokens.refreshToken(), COOKIE_MAX_AGE_REFRESH_TOKEN));

		return new ServerGetOkRsp<>(new AuthTokenRsp(tokens.accessToken(),tokens.refreshToken(), tokens.userId().toString()));
	}

	// @VERIFY1
	@PostMapping("/signup/email")
	public ServerPostCreatedRsp<Boolean> signupVerifyEmail(
		@Valid @RequestBody EmailInfoReq emailInfoReq) {

		return new ServerPostCreatedRsp<>(authService.signupEmail(emailInfoReq));
	}

	// @VERIFY1
	@PostMapping("/verify/signup/email")
	public ServerPostCreatedRsp<Boolean> verifySignupEmail(
		@Valid @RequestBody EmailSignupVerifyReq emailSignupVerifyReq,
		HttpServletResponse response) {

		authService.processVerificationEmail(emailSignupVerifyReq.verificationCode(), response);
		return new ServerPostCreatedRsp<>(true);
	}

	// @VERIFY1
	// 회원가입
	@PostMapping("/signup")
	public ServerPostCreatedRsp<String> signupBySocialLogin(
		@Valid @NotNull @CookieValue(value = CookieConst.REGISTRATION_VERIFICATION_CODE) String registrationVerificationCode,
		@Valid @RequestBody SignupUserInfoReq signupUserInfoReq,
		HttpServletResponse response) {

		// 계정 생성
		SnsUser snsUser = authService.processSignupByVerificationCode(registrationVerificationCode, signupUserInfoReq);

		// CHECK2
		// 토큰 생성 및 전달
		TokenResponse tokens = authService.createJwtTokens(snsUser.getId(), snsUser.getSnsAppRole());

		response.addCookie(
			CookieUtils.createCookie(CookieConst.REFRESH_TOKEN, tokens.refreshToken(), COOKIE_MAX_AGE_REFRESH_TOKEN));

		// CHECK3
		response.addCookie(
			CookieUtils.createCookie(CookieConst.SIGNUP_TYPE, "",
				0)); // 가입 종류 쿠키 값 제거

		response.addCookie(
			CookieUtils.createCookie(CookieConst.REGISTRATION_VERIFICATION_CODE, "",
				0)); // 가입 증명 코드 제거


		String signupMsg = signupUserInfoReq.getUsername() + " 님이 서비스에 가입했습니다.";
		DiscordWebhookRequest request = new DiscordWebhookRequest(signupMsg);
		discordService.sendMessageToServiceNotificationChannel(request);

		return new ServerPostCreatedRsp<>(tokens.accessToken());
	}

	// @VERIFY1
	// 로그아웃
	@PostMapping("/logout")
	public ServerGetOkRsp<String> logout(@AuthenticationPrincipal CustomUserDetails userDetails,
		HttpServletResponse response) {
		Long snsUserId = (userDetails == null) ? null : Long.valueOf(userDetails.getUserId());
		// @CHECK1
		authService.deleteRefreshToken(Long.valueOf(snsUserId));
		// @CHECK2
		Cookie cookie = CookieUtils.deleteRefreshToken();
		response.addCookie(cookie);
		return new ServerGetOkRsp<>(String.valueOf(snsUserId));
	}

	// 가입 유형 조회
	@GetMapping("/signup/type")
	public ServerGetOkRsp<String> getCheckSignupType(
		@AuthenticationPrincipal CustomUserDetails userDetails) {

		Long snsUserId = (userDetails == null) ? null : Long.valueOf(userDetails.getUserId());

		return new ServerGetOkRsp<>(authService.getCheckSignupType(snsUserId));
	}

	// 회원 탈퇴
	@PostMapping("/member-withdrawal")
	public ServerDeleteRsp<String> withdrawal(
		@AuthenticationPrincipal CustomUserDetails userDetails,
		@Valid @RequestBody AuthMemberWithdrawalReq authMemberWithdrawalReq
	) {
		Long snsUserId = (userDetails == null) ? null : Long.valueOf(userDetails.getUserId());
		authService.withdrawal(snsUserId, authMemberWithdrawalReq);
		return new ServerDeleteRsp<>("회원이 탈퇴 되었습니다.");
	}

	@GetMapping("/test")
	public void testException() {
		throw new BadRequestErrorException("테스트 예외");
	}
}
