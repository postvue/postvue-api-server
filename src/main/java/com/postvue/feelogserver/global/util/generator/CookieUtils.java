package com.postvue.feelogserver.global.util.generator;

import org.springframework.beans.factory.annotation.Value;

import com.postvue.feelogserver.global.constant.CookieConst;

import jakarta.servlet.http.Cookie;

public final class CookieUtils {
	@Value("${serverCookie.allowDomain}")
	private static String ALLOW_DOMAIN;

	@Value("${serverCookie.secure}")
	private static boolean IS_SECURE;
	// 쿠키 생성 util
	public static Cookie createCookie(String cookieName, String token, int maxAge) {
		Cookie cookie = new Cookie(cookieName, token);
		cookie.setHttpOnly(IS_SECURE);
		cookie.setPath("/");
		cookie.setMaxAge(maxAge);

		cookie.setSecure(IS_SECURE);
		if (IS_SECURE) {
			cookie.setDomain(ALLOW_DOMAIN);
			cookie.setAttribute("SameSite", "None");
		}

		return cookie;
	}

	// public static Cookie createCookie(String cookieName, String token, int maxAge, Boolean isHttpOnly) {
	// 	Cookie cookie = new Cookie(cookieName, token);
	// 	if (isHttpOnly) {
	// 		cookie.setHttpOnly(true);
	// 	}
	//
	// 	cookie.setPath("/");
	// 	cookie.setMaxAge(maxAge);
	// 	cookie.setDomain(ALLOW_DOMAIN);
	// 	cookie.setSecure(IS_SECURE);
	//
	// 	if (!ALLOW_DOMAIN.equals("localhost")) {
	// 		cookie.setDomain(ALLOW_DOMAIN);
	// 	}
	// 	return cookie;
	// }

	public static Cookie deleteRefreshToken() {
		Cookie cookie = new Cookie(CookieConst.REFRESH_TOKEN, null);
		cookie.setPath("/");
		cookie.setHttpOnly(IS_SECURE);
		cookie.setSecure(IS_SECURE);
		if (IS_SECURE) {
			cookie.setDomain(ALLOW_DOMAIN);
			cookie.setAttribute("SameSite", "None");
		}
		cookie.setMaxAge(0);
		return cookie;
	}

}
