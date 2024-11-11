package com.postvue.feelogserver.domain.snsposts.dto;

import com.postvue.feelogserver.domain.snsposts.SnsPost;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class SnsPostRelation {
	private SnsPost snsPost;

	private Long followingId;

	private Long snsPostLikeId;

	private Long snsUserBookmarkId;

	private Long snsPostClipId;

	private Long snsRepostId;
}
