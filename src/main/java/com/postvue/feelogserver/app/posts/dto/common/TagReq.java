package com.postvue.feelogserver.app.posts.dto.common;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
@AllArgsConstructor
public class TagReq {
	private String tagName;
}
