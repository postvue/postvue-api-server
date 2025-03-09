package com.postvue.feelogserver.app.auth.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.postvue.feelogserver.app.auth.dto.TokenResponse;
import com.postvue.feelogserver.app.auth.dto.req.post.SignupUserInfoReq;
import com.postvue.feelogserver.app.search.service.SearchService;
import com.postvue.feelogserver.core.security.JwtTokenProvider;
import com.postvue.feelogserver.core.security.exception.JwtTokenExpiredException;
import com.postvue.feelogserver.core.security.exception.JwtTokenValidException;
import com.postvue.feelogserver.domain.snsnotifications.SnsNotification;
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
import com.postvue.feelogserver.domain.snsusers.vo.SnsUserState;
import com.postvue.feelogserver.global.constant.CookieConst;
import com.postvue.feelogserver.global.constant.SystemPhraseConst;
import com.postvue.feelogserver.global.constant.SystemTimeConst;
import com.postvue.feelogserver.global.exception.RefreshTokenNotFoundException;
import com.postvue.feelogserver.global.exception.SnsUserIdNotFoundException;
import com.postvue.feelogserver.global.exception.UnauthorizedErrorException;
import com.postvue.feelogserver.global.util.generator.CookieUtils;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Service
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

	private final int COOKIE_EXPIRED_TIME = (int)(long)SystemTimeConst.SYSTEM_1_HOUR_TIME_BY_SECOND;

	@Transactional(noRollbackFor = {JwtTokenExpiredException.class, JwtTokenValidException.class})
	public TokenResponse renewTokens(String refreshToken) {
		SnsUser snsUser = snsUserRepository.findByRefreshToken(refreshToken)
			.orElseThrow(RefreshTokenNotFoundException::new);

		// 리프레시 토큰 검즘 후 오류 나면 리프레시 토큰 삭제
		try {
			jwtTokenProvider.validateToken(refreshToken);
		} catch (Exception e) {
			snsUser.deleteRefreshToken();
			snsUserRepository.save(snsUser);
			throw e;
		}

		return createJwtTokens(
			snsUser.getId(),
			SnsAppRole.valueOf(jwtTokenProvider.getUserRole(refreshToken))
		);
	}

	@Transactional
	public TokenResponse createJwtTokens(Long userId, SnsAppRole snsAppRole) {

		// 탈퇴 했는 지 확인
		SnsUser snsUser = snsUserRepository.findByIdAndDeletedAtIsNull(userId)
			.orElseThrow(() -> new SnsUserIdNotFoundException(userId));

		String accessToken = jwtTokenProvider.createAccessToken(userId, snsAppRole);
		String refreshToken = snsUser.getRefreshToken();

		// 리프레쉬 토큰이 null이면 새롭게 생성되도록
		if (refreshToken == null) {
			refreshToken = jwtTokenProvider.createRefreshToken(userId, snsAppRole);
			snsUser.updateRefreshToken(refreshToken);
		}

		// @REFER: 로그 기록 남기기
		// log.info("{ 'msg': '리프레시 토큰 갱신'}");
		return TokenResponse.of(accessToken, refreshToken);
	}

	// 소셜 플랫폼을 통한 가입
	@Transactional
	public SnsUser signup(SnsUserDto snsUserDto, SignupUserInfoReq signupUserInfoReq) {
		return snsUserRepository.save(snsUserDto.toEntity(signupUserInfoReq));
	}

	// 탈퇴 후 재 가입 메소드
	@Transactional
	public void rejoin(Long userId) {
		SnsUser snsUser = snsUserRepository.findById(userId)
			.orElseThrow(() -> new SnsUserIdNotFoundException(userId));

		snsUser.rejoin();
	}

	public Optional<SnsUserDto> findBySocialLogin(String socialUid, SignUpType signUpType) {
		return snsUserRepository.findNotFullDeletedUser(socialUid, signUpType)
			.map(SnsUserDto::from);
	}

	// 소셜 로그인 처리
	@Transactional
	public SnsUserDto processLoginBySocialLogin(Optional<SnsUserDto> optionalUserDto,
		HttpServletResponse response, SnsUserDto userDtoData) {
		SnsUserDto snsUserDto;
		if (optionalUserDto.isEmpty()) {
			// DB에 존재하지 않으면 sign up으로 401 에러 반환, 이때 쿠키 값으로 카카오 OAuth 토큰 값 전달
			throwToSignUp(response, userDtoData);
			throw new UnauthorizedErrorException(SystemPhraseConst.UNAUTHORIZED_EXCEPTION_PHRASE);

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
			} else {
				// 완전 탈퇴 면 => sign up으로 401 에러 반환
				throwToSignUp(response, userDtoData);
				throw new UnauthorizedErrorException(SystemPhraseConst.UNAUTHORIZED_EXCEPTION_PHRASE);
			}
		}
	}

	@Transactional
	public SnsUser processSignupBySocialLogin(String registrationVerificationCode,
		SignupUserInfoReq signupUserInfoReq) {

		// 가입 증명 코드가 유효하지 않으면
		jwtTokenProvider.validateToken(registrationVerificationCode);

		SnsUserDto userDtoData = jwtTokenProvider.getRegisterValidationCode(
			jwtTokenProvider.getSubjectByToken(registrationVerificationCode));
		List<SnsTag> snsTagList = snsTagRepository.findAllByIdIn(
			signupUserInfoReq.getFavoriteTagList().stream().map(Long::valueOf).toList());

		SnsUser snsUser = signup(userDtoData, signupUserInfoReq);
		snsUserRepository.flush();
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

	// @REFER: 현재 로직이 단순한데, 추가적으로 로직 필요? : 탈퇴한 포스트는 어떻게 보여줄 지, 팔로우한 유저는 어떻게 보여줄지 등
	@Transactional
	public void withdrawal(Long userId) {
		SnsUser snsUser = snsUserRepository.findByIdAndDeletedAtIsNull(userId)
			.orElseThrow(() -> new UnauthorizedErrorException("해당 회원은 존재하지 않습니다."));

		snsUser.withdrawal();
	}

	@Transactional
	public void deleteRefreshToken(Long userId) {
		SnsUser snsUser = snsUserRepository.findById(userId)
			.orElseThrow(() -> new SnsUserIdNotFoundException(userId));

		snsUser.deleteRefreshToken();
	}

	@Transactional
	public boolean updateDeletedUserToFullDeletedUser(LocalDateTime daysAgo) {
		List<SnsUser> snsUserList =  snsUserRepository.findUserOlderThanDays(daysAgo);
		snsUserJdbcRepository.updateAllFullDelete(snsUserList);

		return true;
	}

	// 가입 된 정보 없을 시, 오류 발생 시켜서, 회원 가입 페이지로 이동하게
	private void throwToSignUp(HttpServletResponse response,
		SnsUserDto userDtoData) {
		// // 가입 종류
		response.addCookie(
			CookieUtils.createCookie(CookieConst.SIGNUP_TYPE, userDtoData.signUpType().toString(),
				COOKIE_EXPIRED_TIME, false));
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

	public String hashPassword(String password) {
		return passwordEncoder.encode(password);
	}

	public boolean checkPassword(String rawPassword, String hashedPassword) {
		return passwordEncoder.matches(rawPassword, hashedPassword);
	}

}
