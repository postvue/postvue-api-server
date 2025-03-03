package com.postvue.feelogserver.domain.snsnotifications.vo;

public enum SnsNotificationType {
	//메시지
	MESSAGE_NOTIFICATION,

	// 게시글
	POST_LIKE_NOTIFICATION,
	POST_CLIP_NOTIFICATION,

	// 게시글 댓글
	POST_COMMENT_NOTIFICATION,
	POST_COMMENT_LIKE_NOTIFICATION,
	POST_COMMENT_REPLY_NOTIFICATION,

	//팔로우
	USER_FOLLOWER_NOTIFICATION,
}
