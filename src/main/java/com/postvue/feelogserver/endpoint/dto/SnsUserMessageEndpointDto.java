package com.postvue.feelogserver.endpoint.dto;

import java.time.LocalDateTime;

import com.postvue.feelogserver.domain.snsusermessages.SnsUserMessage;
import com.postvue.feelogserver.domain.snsusermessages.vo.SnsMsgType;

public record SnsUserMessageEndpointDto(
	String id,
	String sourceUser_id,
	String snsUserMessageRoom_id,
	SnsMsgType msgType,
	String msgContent,
	LocalDateTime createdAt
) {

	public static SnsUserMessageEndpointDto fromEntity(SnsUserMessage snsUserMessage){
		return new SnsUserMessageEndpointDto(
			snsUserMessage.getId().toString(),
			snsUserMessage.getSourceUser().getId().toString(),
			snsUserMessage.getSnsUserMessageRoom().getId().toString(),
			snsUserMessage.getMsgType(),
			snsUserMessage.getMsgContent(),
			snsUserMessage.getCreatedAt()
			);
	}
}
