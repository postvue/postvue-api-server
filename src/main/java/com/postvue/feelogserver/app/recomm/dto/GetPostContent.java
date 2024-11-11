package com.postvue.feelogserver.app.recomm.dto;

import com.postvue.feelogserver.domain.snsposts.vo.PostContentType;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class GetPostContent {
	private String postId;
	private PostContentType postContentType;
	private String content;
}
