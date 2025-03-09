package com.postvue.feelogserver.app.auth.dto.req.delete;

import jakarta.annotation.Nullable;
import lombok.Getter;

@Getter
public class AuthMemberWithdrawalReq {
	@Nullable
	private String appleAuthorizationCode;
}
