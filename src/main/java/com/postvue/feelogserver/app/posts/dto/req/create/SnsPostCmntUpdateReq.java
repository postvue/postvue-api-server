package com.postvue.feelogserver.app.posts.dto.req.create;

import lombok.Getter;

@Getter
public class SnsPostCmntUpdateReq {
	private String postCommentMediaType;
	private String postCommentMediaContent;
	private String postCommentMsg;
}
