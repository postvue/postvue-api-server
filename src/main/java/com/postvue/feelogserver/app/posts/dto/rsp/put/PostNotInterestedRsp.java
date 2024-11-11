package com.postvue.feelogserver.app.posts.dto.rsp.put;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Setter
@Getter
public class PostNotInterestedRsp {
	private String postId;
	private Boolean isInterested;
}
