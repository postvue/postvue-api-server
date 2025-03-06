package com.postvue.feelogserver.endpoint.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.postvue.feelogserver.domain.snsusers.SnsUser;
import com.postvue.feelogserver.domain.snsusers.vo.SignUpType;
import com.postvue.feelogserver.domain.snsusers.vo.SnsAppRole;
import com.postvue.feelogserver.domain.snsusers.vo.SnsUserGender;
import com.postvue.feelogserver.domain.snsusers.vo.SnsUserState;

public record SnsUserEndpointDto(
	String id,
	String nickname,
	String signupEmail,
	String email,
	String username,
	String hashPw,
	String userLink,
	String userDescription,
	String socialId,
	SnsUserGender snsUserGender,
	LocalDate birthDate,
	SnsUserState snsUserState,
	SnsAppRole snsAppRole,
	SignUpType signUpType,
	Boolean isPrivateProfile,
	String profilePath,
	String refreshToken,
	LocalDateTime deletedAt,
	Boolean hasFollowerNotification,
	LocalDateTime createdAt,
	LocalDateTime lastUpdatedAt,
	String lastUpdatedByid
) {


	public static SnsUserEndpointDto fromEntity(SnsUser snsUser){
		return new SnsUserEndpointDto(
			snsUser.getId().toString(),
			snsUser.getNickname(),
			snsUser.getSignupEmail(),
			snsUser.getEmail(),
			snsUser.getUsername(),
			snsUser.getHashPw(),
			snsUser.getUserLink(),
			snsUser.getUserDescription(),
			snsUser.getSocialId(),
			snsUser.getSnsUserGender(),
			snsUser.getBirthDate(),
			snsUser.getSnsUserState(),
			snsUser.getSnsAppRole(),
			snsUser.getSignUpType(),
			snsUser.getIsPrivateProfile(),
			snsUser.getProfilePath(),
			snsUser.getRefreshToken(),
			snsUser.getDeletedAt(),
			snsUser.getHasFollowerNotification(),
			snsUser.getCreatedAt(),
			snsUser.getLastUpdatedAt(),
			snsUser.getLastUpdatedByid() != null ? snsUser.getLastUpdatedByid().toString() : null
		);
	}
}
