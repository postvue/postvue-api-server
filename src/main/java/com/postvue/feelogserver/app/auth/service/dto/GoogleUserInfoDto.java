package com.postvue.feelogserver.app.auth.service.dto;

import com.postvue.feelogserver.domain.snsusers.dto.SnsUserDto;
import com.postvue.feelogserver.domain.snsusers.vo.SignUpType;
import com.postvue.feelogserver.domain.snsusers.vo.SnsAppRole;
import com.postvue.feelogserver.domain.snsusers.vo.SnsUserState;
import com.postvue.feelogserver.global.constant.AccountConst;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class GoogleUserInfoDto {
	private String email;
	private String name;
	private String sub;
	private String picture;

	public SnsUserDto toUserDto() {
		return SnsUserDto.of(
			null,
			this.email,
			null,
			null,
			AccountConst.ACCOUNT_NOT_PROFILE_PATH,
			SnsAppRole.ROLE_USER,
			SignUpType.GOOGLE,
			SnsUserState.ACTIVE,
			null,
			null,
			this.sub,
			null,
			null,
			null
		);
	}
}
