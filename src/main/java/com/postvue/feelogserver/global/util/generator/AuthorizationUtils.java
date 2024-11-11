package com.postvue.feelogserver.global.util.generator;

import com.postvue.feelogserver.global.constant.HeaderConst;

public final class AuthorizationUtils {

	public static String returnBearerByAccessToken(String accessToken) {
		return HeaderConst.TOKEN_HEADER_PREFIX + accessToken;
	}
}
