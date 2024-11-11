package com.postvue.feelogserver.endpoint.dto;

import java.time.LocalDateTime;

import com.postvue.feelogserver.domain.snsusermessagereactions.SnsUserMessageReaction;
import com.postvue.feelogserver.domain.snsusermessages.vo.SnsMsgReactionType;

public record SnsUserMessageReactionEndpointDto(
	String id,
	String snsUserMessage_id,
	Boolean hasMsgReaction,
	SnsMsgReactionType snsMsgReactionType,
	LocalDateTime reactedAt,
	Boolean isRead,
	LocalDateTime readAt,
	LocalDateTime createdAt
) {

	public static SnsUserMessageReactionEndpointDto fromEntity(SnsUserMessageReaction snsUserMessageReaction){
		return new SnsUserMessageReactionEndpointDto(
			snsUserMessageReaction.getId().toString(),
			snsUserMessageReaction.getSnsUserMessage().getId().toString(),
			snsUserMessageReaction.getHasMsgReaction(),
			snsUserMessageReaction.getSnsMsgReactionType(),
			snsUserMessageReaction.getReactedAt(),
			snsUserMessageReaction.getIsRead(),
			snsUserMessageReaction.getReadAt(),
			snsUserMessageReaction.getReactedAt());
	}
}
