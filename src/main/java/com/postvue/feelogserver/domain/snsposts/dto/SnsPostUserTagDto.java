package com.postvue.feelogserver.domain.snsposts.dto;

import com.postvue.feelogserver.domain.snsposts.SnsPost;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class SnsPostUserTagDto {
	private SnsPost snsPost;
	private Long followingId;
	private Boolean isLiked;
	private Boolean isClipped;
	private Boolean isReposted;
}
