package com.postvue.feelogserver.global.api.apple.dto;

import com.postvue.feelogserver.domain.snsusers.dto.SnsUserDto;
import com.postvue.feelogserver.domain.snsusers.vo.SignUpType;
import com.postvue.feelogserver.domain.snsusers.vo.SnsAppRole;
import com.postvue.feelogserver.domain.snsusers.vo.SnsUserState;
import com.postvue.feelogserver.global.constant.AccountConst;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AppleUserResponseDto {
    private String sub;    // Apple의 사용자 고유 ID
    private String email;  // 사용자 이메일

    public SnsUserDto toUserDto() {
        return SnsUserDto.of(
            null,
            this.email,
            null,
            null,
            AccountConst.ACCOUNT_NOT_PROFILE_PATH,
            SnsAppRole.ROLE_USER,
            SignUpType.APPLE,
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