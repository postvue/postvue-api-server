package com.postvue.feelogserver.global.api.kakao.dto.rsp;

import java.time.LocalDateTime;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.postvue.feelogserver.domain.snsusers.dto.SnsUserDto;
import com.postvue.feelogserver.domain.snsusers.vo.SignUpType;
import com.postvue.feelogserver.domain.snsusers.vo.SnsAppRole;
import com.postvue.feelogserver.domain.snsusers.vo.SnsUserState;
import com.postvue.feelogserver.global.constant.AccountConst;

import lombok.Getter;

@Getter
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class KakaoUserInfo {

	private String id; // 소셜 id
	private LocalDateTime connectedAt; // 연결 시간
	private KakaoAccount kakaoAccount; //카카오 계쩡 관련 정보

	@Getter
	static class KakaoAccount {
		private Profile profile;
		// private String email;
		// private String gender;

		@Getter
		@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
		static class Profile {
			// private String nickname;
			private String profileImageUrl; // 프로필 이미지
		}
	}

	public SnsUserDto toUserDto() {
		return SnsUserDto.of(
			null,
			null,
			null,
			null,
			this.kakaoAccount != null ? this.kakaoAccount.profile.profileImageUrl : AccountConst.ACCOUNT_NOT_PROFILE_PATH,
			SnsAppRole.ROLE_USER,
			SignUpType.KAKAO,
			SnsUserState.ACTIVE,
			null,
			null,
			this.id,
			null,
			null,
			null
		);
	}
}

