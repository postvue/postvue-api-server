package com.postvue.feelogserver.domain.snstags.vo;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class PostTag {
	private Long tagId;
	private String tagName;
}
