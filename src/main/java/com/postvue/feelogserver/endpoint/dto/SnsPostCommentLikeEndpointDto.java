package com.postvue.feelogserver.endpoint.dto;

import java.time.LocalDateTime;

import com.postvue.feelogserver.domain.snspostcommentlikes.SnsPostCommentLike;

public record SnsPostCommentLikeEndpointDto(
	String id,
	String snsPost_id,
	String snsPostCommentReaction_id,
	String snsUser_id,
	String snsUser_username,
	Boolean isLiked, LocalDateTime isLikedAt, LocalDateTime createdAt
	){

	public static SnsPostCommentLikeEndpointDto fromEntity(SnsPostCommentLike snsPostCommentLike){
		return new SnsPostCommentLikeEndpointDto(
			snsPostCommentLike.getId().toString(),
			snsPostCommentLike.getSnsPost().getId().toString(),
			snsPostCommentLike.getSnsPostCommentReaction().getId().toString(),
			snsPostCommentLike.getSnsUser().getId().toString(),
			snsPostCommentLike.getSnsUser().getUsername(),
			snsPostCommentLike.getIsLiked(),
			snsPostCommentLike.getIsLikedAt(),
			snsPostCommentLike.getCreatedAt());
	}
}
