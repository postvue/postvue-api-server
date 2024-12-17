package com.postvue.feelogserver.domain.snstags.vo;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.postvue.feelogserver.domain.snsposts.vo.PostContentType;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class PostTag {
	private Long tagId;
	private String tagName;

	@JsonCreator
	public PostTag(
		@JsonProperty("tagId") Long tagId,
		@JsonProperty("tagName") String tagName
	) {
		this.tagId = tagId;
		this.tagName = tagName;
	}
}
