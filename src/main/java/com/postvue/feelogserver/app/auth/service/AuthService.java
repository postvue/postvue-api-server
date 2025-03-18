package com.postvue.feelogserver.app.auth.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpServerErrorException;

import com.postvue.feelogserver.app.auth.dto.TokenResponse;
import com.postvue.feelogserver.app.auth.dto.req.delete.AuthMemberWithdrawalReq;
import com.postvue.feelogserver.app.auth.dto.req.post.EmailInfoReq;
import com.postvue.feelogserver.app.auth.dto.req.post.SignupUserInfoReq;
import com.postvue.feelogserver.app.email.service.EmailService;
import com.postvue.feelogserver.app.openapis.req.DiscordWebhookRequest;
import com.postvue.feelogserver.app.openapis.service.DiscordService;
import com.postvue.feelogserver.app.profiles.service.ProfileFollowsService;
import com.postvue.feelogserver.app.search.service.SearchService;
import com.postvue.feelogserver.core.security.JwtTokenProvider;
import com.postvue.feelogserver.core.security.exception.JwtTokenExpiredException;
import com.postvue.feelogserver.core.security.exception.JwtTokenValidException;
import com.postvue.feelogserver.domain.snstagfollows.SnsTagFollow;
import com.postvue.feelogserver.domain.snstagfollows.repository.SnsTagFollowJdbcRepository;
import com.postvue.feelogserver.domain.snstags.SnsTag;
import com.postvue.feelogserver.domain.snstags.repository.SnsTagRepository;
import com.postvue.feelogserver.domain.snsuserfavoritetermbookmarks.SnsUserFavoriteTermBookmark;
import com.postvue.feelogserver.domain.snsuserfavoritetermbookmarks.respository.SnsUserFavoriteTermBookmarkJdbcRepository;
import com.postvue.feelogserver.domain.snsusers.SnsUser;
import com.postvue.feelogserver.domain.snsusers.dto.SnsUserDto;
import com.postvue.feelogserver.domain.snsusers.repository.SnsUserJdbcRepository;
import com.postvue.feelogserver.domain.snsusers.repository.SnsUserRepository;
import com.postvue.feelogserver.domain.snsusers.vo.SignUpType;
import com.postvue.feelogserver.domain.snsusers.vo.SnsAppRole;
import com.postvue.feelogserver.domain.snsusers.vo.SnsUserGender;
import com.postvue.feelogserver.domain.snsusers.vo.SnsUserState;
import com.postvue.feelogserver.global.api.apple.dto.AppleRevokeRequestDto;
import com.postvue.feelogserver.global.api.apple.dto.AppleTokenResponseDto;
import com.postvue.feelogserver.global.constant.AccountConst;
import com.postvue.feelogserver.global.constant.CookieConst;
import com.postvue.feelogserver.global.constant.LogTemplateConst;
import com.postvue.feelogserver.global.constant.SystemPhraseConst;
import com.postvue.feelogserver.global.constant.SystemTimeConst;
import com.postvue.feelogserver.global.exception.BadRequestErrorException;
import com.postvue.feelogserver.global.exception.InternalServerErrorException;
import com.postvue.feelogserver.global.exception.RefreshTokenNotFoundException;
import com.postvue.feelogserver.global.exception.SnsUserIdNotFoundException;
import com.postvue.feelogserver.global.exception.UnauthorizedErrorException;
import com.postvue.feelogserver.global.util.generator.CookieUtils;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class AuthService {
	private final SnsUserRepository snsUserRepository;
	private final JwtTokenProvider jwtTokenProvider;
	private final SnsTagRepository snsTagRepository;
	private final SnsTagFollowJdbcRepository snsTagFollowJdbcRepository;
	private final SnsUserFavoriteTermBookmarkJdbcRepository snsUserFavoriteTermBookmarkJdbcRepository;
	private final SearchService searchService;
	private final BCryptPasswordEncoder passwordEncoder;
	private final SnsUserJdbcRepository snsUserJdbcRepository;
	private final EmailService emailService;
	private final AppleAuthService appleAuthService;
	private final DiscordService discordService;

	@Value("${serverUrl.verifyEmailUrl}")
	private String serverVerifyEmailUrl;

	private final int COOKIE_EXPIRED_TIME = (int)(long)SystemTimeConst.SYSTEM_1_HOUR_TIME_BY_SECOND;

	@Transactional(noRollbackFor = {JwtTokenExpiredException.class, JwtTokenValidException.class})
	public TokenResponse renewTokens(String refreshToken) {
		SnsUser snsUser = snsUserRepository.findByRefreshToken(refreshToken)
			.orElseThrow(RefreshTokenNotFoundException::new);

		// CHECK1
		// 리프레시 토큰 검즘 후 오류 나면 리프레시 토큰 삭제
		try {
			jwtTokenProvider.validateToken(refreshToken);
		} catch (Exception e) {
			snsUser.deleteRefreshToken();
			snsUserRepository.save(snsUser);
			throw e;
		}

		// @CHECK2
		// 엑세스 토큰 없거나 만료시, 새로운 토큰 잘 전달
		return createJwtTokens(
			snsUser.getId(),
			SnsAppRole.valueOf(jwtTokenProvider.getUserRole(refreshToken))
		);
	}

	@Transactional
	public TokenResponse createJwtTokens(Long userId, SnsAppRole snsAppRole) {
		// 탈퇴 했는 지 확인
		SnsUser snsUser = snsUserRepository.findByNotFullDeleted(userId)
			.orElseThrow(() -> new SnsUserIdNotFoundException(userId));

		String accessToken = jwtTokenProvider.createAccessToken(userId, snsAppRole);
		String refreshToken = snsUser.getRefreshToken();

		// 리프레쉬 토큰이 null이면 새롭게 생성되도록
		if (refreshToken == null) {
			refreshToken = jwtTokenProvider.createRefreshToken(userId, snsAppRole);
			snsUser.updateRefreshToken(refreshToken);
		}

		return TokenResponse.of(accessToken, refreshToken, snsUser.getId());
	}

	// 소셜 플랫폼을 통한 가입
	@Transactional
	public SnsUser signup(SnsUserDto snsUserDto, SignupUserInfoReq signupUserInfoReq) {
		// batch insert 전에, sns_user 정보가 db상에 존재해야 되기 때문에, flush
		return snsUserRepository.saveAndFlush(snsUserDto.toEntity(signupUserInfoReq));
	}

	// 탈퇴 후 재 가입 메소드
	@Transactional
	public void rejoin(Long userId) {
		SnsUser snsUser = snsUserRepository.findById(userId)
			.orElseThrow(() -> new SnsUserIdNotFoundException(userId));

		snsUser.rejoin();
		snsUserRepository.save(snsUser);
	}

	@Transactional
	public SnsUserDto findByEmail(String email, String password) {
		// @CHECK1
		SnsUser snsUser = snsUserRepository.findBySignupEmail(email).orElseThrow(
			() -> new BadRequestErrorException("비밀번호가 틀립니다.")
		);
		// @CHECK2
		if (!checkPassword(password, snsUser.getHashPw())){
			throw new BadRequestErrorException("비밀번호가 틀립니다.");
		}

		// 탈퇴 했는 지?: 함 // 완전 탈퇴가 이전 인지?
		if (snsUser.getSnsUserState() == SnsUserState.FULL_DELETED) {
			throw new BadRequestErrorException("다시 가입할 수 없습니다.");
		} else if (snsUser.getDeletedAt() != null){
			rejoin(snsUser.getId());
		}

		return SnsUserDto.from(snsUser);
	}


	@Transactional
	public Optional<SnsUserDto> findBySocialLogin(String socialUid, SignUpType signUpType) {
		return snsUserRepository.findNotFullDeletedUser(socialUid, signUpType)
			.map(SnsUserDto::from);
	}

	// 소셜 로그인 처리
	public SnsUserDto processLoginBySocialLogin(Optional<SnsUserDto> optionalUserDto,
		HttpServletResponse response, SnsUserDto userDtoData) {
		SnsUserDto snsUserDto;
		// CHECK3
		if (optionalUserDto.isEmpty()) {
			// DB에 존재하지 않으면 sign up으로 401 에러 반환, 이때 쿠키 값으로 카카오 OAuth 토큰 값 전달
			registerValidationCodeToCookie(response, userDtoData);
			throw new UnauthorizedErrorException(SystemPhraseConst.NOT_SIGNUP_USER_EXCEPTION_PHRASE);

		} else {
			// DB에 존재 하는 경우
			snsUserDto = optionalUserDto.get();
			// 탈퇴 했는 지?: 안함
			if (snsUserDto.deletedAt() == null) {
				return snsUserDto;
			} else if (snsUserDto.snsUserState().equals(SnsUserState.DELETED) // 탈퇴 했는 지?: 함 // 완전 탈퇴가 이전 인지?
			) {
				// 재 가입
				rejoin(snsUserDto.snsUserId());
				return snsUserDto;
			} else if (snsUserDto.snsUserState().equals(SnsUserState.FULL_DELETED)) {
				// 완전 탈퇴 면 => sign up으로 401 에러 반환
				registerValidationCodeToCookie(response, userDtoData);
				throw new UnauthorizedErrorException(SystemPhraseConst.UNAUTHORIZED_EXCEPTION_PHRASE);
			}
			else{
				throw new BadRequestErrorException("오류로 인해 로그인에 실패했습니다.");
			}
		}
	}

	@Transactional
	public SnsUser processSignupByVerificationCode(String registrationVerificationCode,
		SignupUserInfoReq signupUserInfoReq) {
		try {
			// 가입 증명 코드가 유효하지 않으면
			jwtTokenProvider.validateToken(registrationVerificationCode);

			SnsUserDto userDtoData = jwtTokenProvider.getRegisterValidationCode(
				jwtTokenProvider.getSubjectByToken(registrationVerificationCode));
			List<SnsTag> snsTagList = snsTagRepository.findAllByIdIn(
				signupUserInfoReq.getFavoriteTagList().stream().map(Long::valueOf).toList());

			SnsUser snsUser = signup(userDtoData, signupUserInfoReq);
			// batch insert 전에, sns_user 정보가 db상에 존재해야 되기 때문에, flush
			// snsUserRepository.flush();

			List<SnsTagFollow> snsTagFollowList = snsTagList.stream().map(snsTag ->
				SnsTagFollow.builder()
					.snsTag(snsTag)
					.tagName(snsTag.getTagName())
					.snsUser(snsUser)
					.build()).toList();
			snsTagFollowJdbcRepository.saveAll(snsTagFollowList);

			snsUserFavoriteTermBookmarkJdbcRepository.saveAllWithTag(
				snsTagFollowList.stream().map(snsTagFollow -> {
					SnsUserFavoriteTermBookmark snsUserFavoriteTermBookmark = SnsUserFavoriteTermBookmark.builder()
						.snsUser(snsUser)
						.favoriteTermName(searchService.makeTagSearchTerm(snsTagFollow.getSnsTag().getTagName()))
						.snsTagFollow(snsTagFollow)
						.build();

					snsUserFavoriteTermBookmark.setFavoriteTermContent(
						snsTagFollow.getSnsTag().getTagRepsBatchContent());
					snsUserFavoriteTermBookmark.setFavoriteTermContentType(
						snsTagFollow.getSnsTag().getTagRepsBatchContentType());
					return snsUserFavoriteTermBookmark;
				}).toList());

			return snsUser;
		}
		catch (Exception e){
			log.error(e.getMessage());
			String errorMsg = LogTemplateConst.getErrorLogTemplate(
				"SINGUP_ERROR", "유저 " + signupUserInfoReq.getUsername() +"님이 가입에 실패했습니다.",
				e.getMessage(),this.getClass().getName(), Thread.currentThread().getStackTrace()[1].getMethodName(),
				new Object[] {},
				HttpStatus.INTERNAL_SERVER_ERROR.value());
			DiscordWebhookRequest request = new DiscordWebhookRequest(errorMsg);
			discordService.sendMessageToPostReportChannel(request);
			throw new InternalServerErrorException(e.getMessage());
		}
	}

	@Transactional
	public void withdrawal(Long userId, AuthMemberWithdrawalReq authMemberWithdrawalReq) {
		SnsUser snsUser = snsUserRepository.findByIdAndDeletedAtIsNull(userId)
			.orElseThrow(() -> new UnauthorizedErrorException("해당 회원은 존재하지 않습니다."));

		if (snsUser.getSignUpType() == SignUpType.APPLE){
			if (authMemberWithdrawalReq.getAppleAuthorizationCode() == null){
				throw new BadRequestErrorException("사용자 인증이 되지 않았습니다.");
			}
			AppleTokenResponseDto appleTokenResponseDto = appleAuthService.getAppleAccessToken(authMemberWithdrawalReq.getAppleAuthorizationCode());

			Optional<SnsUserDto> optionalUserDto = findBySocialLogin(snsUser.getSocialId(),
				SignUpType.APPLE);
			appleAuthService.revokeAppleAccount(new AppleRevokeRequestDto(appleTokenResponseDto.getRefreshToken()), optionalUserDto);
		}

		snsUser.withdrawal();
	}

	@Transactional
	public String getCheckSignupType(Long userId){
		return snsUserRepository.findById(userId).orElseThrow(
			() -> new BadRequestErrorException("해당 계정은 없습니다.")
		).getSignUpType().toString();
	}

	@Transactional
	public void deleteRefreshToken(Long userId) {
		SnsUser snsUser = snsUserRepository.findById(userId)
			.orElseThrow(() -> new SnsUserIdNotFoundException(userId));

		snsUser.deleteRefreshToken();
	}

	@Transactional
	public List<SnsUser> updateDeletedUserToFullDeletedUser(LocalDateTime daysAgo) {
		List<SnsUser> snsUserList =  snsUserRepository.findDeletedUserOlderThanDays(daysAgo);
		snsUserJdbcRepository.updateAllFullDelete(snsUserList);

		return snsUserList;
	}

	public Boolean checkAuthMe (Long snsUserId){
		snsUserRepository.findById(snsUserId).orElseThrow(
			() -> new BadRequestErrorException("해당 계정은 없습니다.")
		);
		return true;
	}

	public Boolean signupEmail(EmailInfoReq emailInfoReq) {
		SnsUserDto userDtoData = SnsUserDto.of(
			null,
			emailInfoReq.email(),
			emailInfoReq.email(),
			"",
			AccountConst.ACCOUNT_NOT_PROFILE_PATH,
			SnsAppRole.ROLE_USER,
			SignUpType.EMAIL,
			SnsUserState.ACTIVE,
			SnsUserGender.OTHERS,
			null,
			null,
			hashPassword(emailInfoReq.password()),
			null,
			null
		);

		// @CHECK2
		// 인증 링크 생성 (예: 토큰 생성 후 링크로 전송)
		// 가입 증명 코드
		String validationCode = jwtTokenProvider.createRegisterValidationCode(
			userDtoData,
			userDtoData.snsAppRole());


		// @CHECK3
		// // 이메일 보내기
		String verificationLink = serverVerifyEmailUrl + validationCode;
		emailService.sendVerificationEmail(emailInfoReq.email(), verificationLink);

		return true;
	}

	public void processVerificationEmail(String registrationVerificationCode, HttpServletResponse response) {
		// 가입 증명 코드가 유효하지 않으면
		jwtTokenProvider.validateToken(registrationVerificationCode);
		registerEmailValidationCodeToCookie(response, registrationVerificationCode);
	}

	// 가입 된 정보 없을 시, 오류 발생 시켜서, 회원 가입 페이지로 이동하게
	private void registerValidationCodeToCookie(HttpServletResponse response,
		SnsUserDto userDtoData) {
		// // 가입 종류
		response.addCookie(
			CookieUtils.createCookie(CookieConst.SIGNUP_TYPE, userDtoData.signUpType().toString(),
				COOKIE_EXPIRED_TIME));
		// // OAUTH 토큰
		// response.addCookie(
		// 	CookieUtils.createCookie(CookieConst.OAUTH_TOKEN, oauthToken,
		// 		COOKIE_EXPIRED_TIME));

		// 가입 증명 코드
		String validationCode = jwtTokenProvider.createRegisterValidationCode(
			userDtoData,
			userDtoData.snsAppRole());
		response.addCookie(
			CookieUtils.createCookie(CookieConst.REGISTRATION_VERIFICATION_CODE, validationCode,
				COOKIE_EXPIRED_TIME));
	}

	private void registerEmailValidationCodeToCookie(HttpServletResponse response,
		String validationCode) {
		// // 가입 종류
		response.addCookie(
			CookieUtils.createCookie(CookieConst.SIGNUP_TYPE, SignUpType.EMAIL.toString(),
				COOKIE_EXPIRED_TIME));

		response.addCookie(
			CookieUtils.createCookie(CookieConst.REGISTRATION_VERIFICATION_CODE, validationCode,
				COOKIE_EXPIRED_TIME));
	}

	public String hashPassword(String password) {
		return passwordEncoder.encode(password);
	}

	public boolean checkPassword(String rawPassword, String hashedPassword) {
		return passwordEncoder.matches(rawPassword, hashedPassword);
	}

}
