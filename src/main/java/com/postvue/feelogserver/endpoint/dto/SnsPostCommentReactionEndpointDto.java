package com.postvue.feelogserver.endpoint.dto;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Value;

import com.postvue.feelogserver.domain.snspostcommentreactions.SnsPostCommentReaction;
import com.postvue.feelogserver.domain.snspostcommentreactions.vo.PostCommentMediaType;

import lombok.Getter;
import lombok.Setter;

public record SnsPostCommentReactionEndpointDto(
	String id,
	String snsPost_id,
	String sourceComment_id,
	String commentUser_id,
	String commentMsg,
	PostCommentMediaType commentMediaType,
	String commentMediaContent,
	Boolean isSource,
	LocalDateTime createdAt
	){


	public static SnsPostCommentReactionEndpointDto fromEntity(SnsPostCommentReaction snsPostCommentReaction){
		return new SnsPostCommentReactionEndpointDto(
			snsPostCommentReaction.getId().toString(),
			snsPostCommentReaction.getSnsPost().getId().toString(),
			snsPostCommentReaction.getSourceComment() != null ? snsPostCommentReaction.getSourceComment().getId().toString() : null,
			snsPostCommentReaction.getCommentUser().getId().toString(),
			snsPostCommentReaction.getCommentMsg(),
			snsPostCommentReaction.getCommentMediaType(),
			snsPostCommentReaction.getCommentMediaContent() != null ? snsPostCommentReaction.getCommentMediaContent() : null,
			snsPostCommentReaction.getIsSource(),
			snsPostCommentReaction.getCreatedAt());
	}
}
