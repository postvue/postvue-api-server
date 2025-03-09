package com.postvue.feelogserver.domain.snsusers.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.postvue.feelogserver.app.auth.dto.req.post.SignupUserInfoReq;
import com.postvue.feelogserver.domain.snsusers.SnsUser;
import com.postvue.feelogserver.domain.snsusers.vo.SignUpType;
import com.postvue.feelogserver.domain.snsusers.vo.SnsAppRole;
import com.postvue.feelogserver.domain.snsusers.vo.SnsUserGender;
import com.postvue.feelogserver.domain.snsusers.vo.SnsUserState;
import com.postvue.feelogserver.global.constant.AccountConst;

public record SnsUserDto(
	Long snsUserId,
	String email,
	String nickname,
	String profilePath,
	SnsAppRole snsAppRole,
	SignUpType signUpType,
	SnsUserState snsUserState,
	SnsUserGender snsUserGender,
	LocalDate birthDate,
	String socialId,
	String refreshToken,
	LocalDateTime deletedAt
) {

	public static SnsUserDto of(
		Long snsUserId,
		String email,
		String nickname,
		String profilePath,
		SnsAppRole snsAppRole,
		SignUpType signUpType,
		SnsUserState snsUserState,
		SnsUserGender gender,
		LocalDate birthDate,
		String socialId,
		String refreshToken,
		LocalDateTime deletedAt
	) {
		return new SnsUserDto(
			snsUserId, email, nickname, profilePath, snsAppRole, signUpType, snsUserState,
			gender, birthDate, socialId, refreshToken, deletedAt
		);
	}

	public static SnsUserDto from(SnsUser snsUser) {
		return of(
			snsUser.getId(),
			snsUser.getEmail(),
			snsUser.getNickname(),
			snsUser.getProfilePath(),
			snsUser.getSnsAppRole(),
			snsUser.getSignUpType(),
			snsUser.getSnsUserState(),
			snsUser.getSnsUserGender(),
			snsUser.getBirthDate(),
			snsUser.getSocialId(),
			snsUser.getRefreshToken(),
			snsUser.getDeletedAt()
		);
	}

	public SnsUser toEntity() {
		return SnsUser.builder()
			.email(this.email)
			.nickname(this.nickname)
			.profilePath(this.profilePath)
			.snsAppRole(this.snsAppRole)
			.signUpType(this.signUpType)
			.snsUserState(this.snsUserState)
			.snsUserGender(this.snsUserGender)
			.birthDate(this.birthDate)
			.socialId(this.socialId)
			.refreshToken(this.refreshToken)
			.build();
	}

	public SnsUser toEntity(SignupUserInfoReq signupUserInfoReq) {
		return SnsUser.builder()
			.email(this.email)
			.signupEmail(this.email)
			.nickname(signupUserInfoReq.getNickname())
			.username(signupUserInfoReq.getUsername())
			.snsUserGender(SnsUserGender.valueOf(signupUserInfoReq.getGender()))
			.profilePath(this.profilePath != null ? this.profilePath : AccountConst.ACCOUNT_NOT_PROFILE_PATH)
			.snsAppRole(this.snsAppRole)
			.signUpType(this.signUpType)
			.snsUserState(this.snsUserState)
			.snsUserGender(SnsUserGender.valueOf(signupUserInfoReq.getGender()))
			.birthDate(signupUserInfoReq.convertBirthDateAsLocalDate())
			.socialId(this.socialId)
			.refreshToken(this.refreshToken)
			.build();
	}
}

