package com.postvue.feelogserver.app.auth.dto.rsp;

public record AuthTokenRsp(
	String accessToken,
	String refreshToken,
	String userId
) {
}
