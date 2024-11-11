package com.postvue.feelogserver.app.posts.dto.common;

import com.postvue.feelogserver.domain.snsposts.vo.PostContentType;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class PostContent {
	private PostContentType postContentType;
	private String content;
	private Integer ascSortNum;
}
