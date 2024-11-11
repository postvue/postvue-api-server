package com.postvue.feelogserver.endpoint.dto;

import java.time.LocalDateTime;

import com.postvue.feelogserver.domain.snspostuserreactions.SnsPostUserReaction;

public record SnsPostUserReactionEndpointDto(
	String id,
	String snsPost_id,
	String snsUser_id,

	Boolean isLiked,
	LocalDateTime isLikedAt,
	Boolean isClipped,
	LocalDateTime isClippedAt,

	Boolean isReposted,
	LocalDateTime isRepostedAt,

	Boolean isBookmarked,

	LocalDateTime isBookmarkedAt,
	Boolean isShown,

	LocalDateTime notShownAt,

	LocalDateTime createdAt
	){

	public static SnsPostUserReactionEndpointDto fromEntity(SnsPostUserReaction snsUserCommentReaction){
		return new SnsPostUserReactionEndpointDto(
			snsUserCommentReaction.getId().toString(),
			snsUserCommentReaction.getSnsPost().getId().toString(),
			snsUserCommentReaction.getSnsUser().getId().toString(),
			snsUserCommentReaction.getIsLiked(),
			snsUserCommentReaction.getIsLikedAt(),
			snsUserCommentReaction.getIsClipped(),
			snsUserCommentReaction.getIsClippedAt(),
			snsUserCommentReaction.getIsReposted(),
			snsUserCommentReaction.getIsRepostedAt(),
			snsUserCommentReaction.getIsBookmarked(),
			snsUserCommentReaction.getIsBookmarkedAt(),
			snsUserCommentReaction.getIsShown(),
			snsUserCommentReaction.getNotShownAt(),
			snsUserCommentReaction.getCreatedAt());
	}
}
