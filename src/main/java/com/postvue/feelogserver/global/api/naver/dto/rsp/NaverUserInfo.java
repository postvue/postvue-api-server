package com.postvue.feelogserver.global.api.naver.dto.rsp;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.postvue.feelogserver.domain.snsusers.dto.SnsUserDto;
import com.postvue.feelogserver.domain.snsusers.vo.SignUpType;
import com.postvue.feelogserver.domain.snsusers.vo.SnsAppRole;
import com.postvue.feelogserver.domain.snsusers.vo.SnsUserGender;
import com.postvue.feelogserver.domain.snsusers.vo.SnsUserState;

import lombok.Getter;

// TODO: 회원 가입 시 받아올 회원 정보 추가
@Getter
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class NaverUserInfo {

	private String resultCode;
	private String message;
	@JsonProperty("response")
	private NaverUserDetail naverUserDetail;

	@Getter
	@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
	public static class NaverUserDetail {
		private String id;
		private String name;
		private String email;
		private String profileImage;
		private String age;
		private String gender;
		private String mobile;
		private String birthday;
		private String birthyear;
	}

	public SnsUserDto toUserDto() {
		return SnsUserDto.of(
			null,
			this.naverUserDetail.email,
			this.naverUserDetail.name,
			this.naverUserDetail.profileImage,
			SnsAppRole.ROLE_USER,
			SignUpType.NAVER,
			SnsUserState.ACTIVE,
			SnsUserGender.OTHERS,
			null,
			this.naverUserDetail.id,
			null,
			null
		);
	}

}

