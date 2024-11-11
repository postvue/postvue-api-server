package com.postvue.feelogserver.global.constant;

import java.util.Arrays;
import java.util.List;

public final class WebSocketPathConst {

	public static final String MESSAGE_BROKER_PATH = "/topic";
	public static final String APPLICATION_PATH = "/app";

	// 메시지 페이지
	private static final String MESSAGES_PATH = "/messages";
	// 세션
	public static final String SESSIONS_PATH = "/sessions";

	// 메시지 대화
	public static final String MESSAGE_CONVERSATIONS_PATH = "/conversations";
	// 알림
	public static final String NOTIFICATION_PATH = "/notifications";

	public static final String API_MESSAGES_PATH = APPLICATION_PATH + MESSAGES_PATH;

	// 세션 어플리케이션, 브로커
	public static final String API_SESSIONS_PATH = APPLICATION_PATH + SESSIONS_PATH;
	public static final String SESSION_BROKER_PATH = MESSAGE_BROKER_PATH + SESSIONS_PATH;

	public static final String MESSAGE_CONVERSATION_BROKER_PATH = MESSAGE_BROKER_PATH + MESSAGE_CONVERSATIONS_PATH;

	public static final String NOTIFICATION_BROKER_PATH = MESSAGE_BROKER_PATH + NOTIFICATION_PATH;

	public static final List<String> BROKER_URL_LIST = Arrays.asList(
		SESSION_BROKER_PATH, NOTIFICATION_BROKER_PATH, MESSAGE_CONVERSATION_BROKER_PATH, NOTIFICATION_BROKER_PATH,
		API_SESSIONS_PATH
	);

	public static final List<String> APPLICATION_PATH_LIST = Arrays.asList(APPLICATION_PATH);

	//
}
