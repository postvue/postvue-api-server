package com.postvue.feelogserver.global.constant;

public class SnsNotificationTemplateConst {

	private static final String POST_LIKE_NOTIFICATION_MESSAGE_TEMPLATE = "%s님 외 %d명 이상의 사람들이 회원님의 게시물에 좋아요 ❤ 를 남겼습니다.";

	public static String getPostLikeNotificationMessage(String username, Integer notificationNum) {
		return String.format(POST_LIKE_NOTIFICATION_MESSAGE_TEMPLATE, username, notificationNum);
	}

	private static final String POST_CLIP_NOTIFICATION_MESSAGE_TEMPLATE = "%s님 외 %d명 이상의 사람들이 회원님의 게시물을 저장했습니다.";

	public static String getPostClipNotificationMessage(String username, Integer notificationNum) {
		return String.format(POST_CLIP_NOTIFICATION_MESSAGE_TEMPLATE, username, notificationNum);
	}

	private static final String POST_COMMENT_NOTIFICATION_MESSAGE_TEMPLATE = "%s님 외 %d명 이상의 사람들이 회원님의 게시물에 댓글을 남겼습니다.";

	public static String getPostCommentNotificationMessage(String username, Integer notificationNum) {
		return String.format(POST_COMMENT_NOTIFICATION_MESSAGE_TEMPLATE, username, notificationNum);
	}

	private static final String FOLLOWER_NOTIFICATION_MESSAGE_TEMPLATE = "%s님이 회원님을 팔로우하기 시작했습니다.";

	public static String getFollowerNotificationMessage(String username) {
		return String.format(FOLLOWER_NOTIFICATION_MESSAGE_TEMPLATE, username);
	}

}
