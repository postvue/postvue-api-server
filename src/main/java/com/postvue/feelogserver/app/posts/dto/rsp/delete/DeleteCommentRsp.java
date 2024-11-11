package com.postvue.feelogserver.app.posts.dto.rsp.delete;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class DeleteCommentRsp {
	private String postId;
	private String commentId;
	private Boolean isDeleted;
}
