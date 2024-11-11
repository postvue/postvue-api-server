package com.postvue.feelogserver.app.posts.dto.rsp.get;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class SnsPostCommentRsp {
	private String postCommentId;
	private String commentUserId;
	private String postCommentMsg;
	private String commentMediaType;
	private String commentMediaContent;
	private Boolean isLiked;
	private Integer likeCount;
	private Integer commentCount;
	private String username;
	private String profilePath;
	private Boolean isReplyMsg;
	private String replyTargetCommentId;
	private LocalDateTime postedAt;
}
