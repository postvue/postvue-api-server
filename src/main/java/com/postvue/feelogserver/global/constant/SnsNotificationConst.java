package com.postvue.feelogserver.global.constant;

public final class SnsNotificationConst {
	public static Integer ZERO_NOTIFICATION_NUM = 0;

	public static Integer POST_LiKE_NOTIFICATION_MIN_NUM = 10;
	public static Integer POST_LiKE_NOTIFICATION_SEQUENCE_NUM = 10;

	public static Integer POST_CLIP_NOTIFICATION_MIN_NUM = 10;
	public static Integer POST_CLIP_NOTIFICATION_SEQUENCE_NUM = 10;

	// 게시글 댓글
	public static Integer POST_COMMENT_NOTIFICATION_MIN_NUM = 1;
	public static Integer POST_COMMENT_NOTIFICATION_MIN_SEQUENCE_NUM = 5;
	public static Integer POST_COMMENT_NOTIFICATION_SEQUENCE_NUM = 10;

	// 게시글 답글
	public static Integer POST_COMMENT_REPLY_NOTIFICATION_MIN_SEQUENCE_NUM = 10;
	public static Integer POST_COMMENT_REPLY_NOTIFICATION_SEQUENCE_NUM = 10;

	// 댓글 오류 타입
	public static String POST_COMMENT_NONE_TYPE = "NONE";
	public static String POST_COMMENT_NONE_VALUE = "NONE";

	// 최대 알림 메시지 보유 기간
	public static Integer MAX_NOTIFICATION_MSG_RETENTION_DAY = 7;
}

