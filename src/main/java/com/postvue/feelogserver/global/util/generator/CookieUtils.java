package com.postvue.feelogserver.global.util.generator;

import com.postvue.feelogserver.global.constant.CookieConst;

import jakarta.servlet.http.Cookie;

public final class CookieUtils {

	// 쿠키 생성 util
	public static Cookie createCookie(String cookieName, String token, int maxAge) {
		Cookie cookie = new Cookie(cookieName, token);
		cookie.setHttpOnly(true);
		// TODO: https 세팅 후 주석 해제
		// cookie.setSecure(true);
		cookie.setPath("/");
		cookie.setMaxAge(maxAge);
		// cookie.setDomain(serverDomain);
		return cookie;
	}

	public static Cookie createCookie(String cookieName, String token, int maxAge, Boolean isHttpOnly) {
		Cookie cookie = new Cookie(cookieName, token);
		if (isHttpOnly) {
			cookie.setHttpOnly(true);
		}
		// TODO: https 세팅 후 주석 해제
		// cookie.setSecure(true);
		cookie.setPath("/");
		cookie.setMaxAge(maxAge);
		// cookie.setDomain(serverDomain);
		return cookie;
	}

	public static Cookie deleteRefreshToken() {
		Cookie cookie = new Cookie(CookieConst.REFRESH_TOKEN, null);
		cookie.setPath("/");
		// cookie.setDomain(serverDomain);
		cookie.setMaxAge(0);
		return cookie;
	}

}
