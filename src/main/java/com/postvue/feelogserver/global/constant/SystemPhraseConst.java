package com.postvue.feelogserver.global.constant;

public final class SystemPhraseConst {

	//서버 Error
	public static final String REFRESH_TOKEN_NOT_FOUND_EXCEPTION_PHRASE = "해당 refresh 토큰을 가진 사용자가 없습니다.";
	public static final String USER_ID_NOT_FOUND_EXCEPTION_PHRASE = "해당 아이디(PK)를 가진 사용자는 존재하지 않습니다. PK";
	public static final String UNAUTHORIZED_EXCEPTION_PHRASE = "인증 오류 났습니다."; // 401
	public static final String NOT_SIGNUP_USER_EXCEPTION_PHRASE = "가입하지 않은 유저입니다."; //401
	public static final String INTERNAL_SERVER_VEXCEPTION_PHRASE = "Internal Server Error: An internal server error occurred."; // 500번대 에러

	// 카카오 api관련 에러
	public static final String KAKAO_SERVER_EXCEPTION_PHRASE = "카카오 서버와 통신 중 에러가 발생했습니다."; // 500 에러
	public static final String KAKAO_TOKEN_VALID_EXCEPTION_PHRASE = "유효하지 않는 KAKAO 토큰입니다."; // 401 에러

	public static final String NAVER_SERVER_EXCEPTION_PHRASE = "네이버 서버와 통신 중 에러가 발생했습니다."; // 500 에러
	public static final String NAVER_TOKEN_VALID_EXCEPTION_PHRASE = "유효하지 않는 KAKAO 토큰입니다."; // 401 에러

}
