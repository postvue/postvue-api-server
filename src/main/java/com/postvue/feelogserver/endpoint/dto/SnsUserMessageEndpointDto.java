package com.postvue.feelogserver.endpoint.dto;

import java.time.LocalDateTime;

import com.postvue.feelogserver.domain.snsusermessages.SnsUserMessage;
import com.postvue.feelogserver.domain.snsusermessages.vo.MsgMediaType;
import com.postvue.feelogserver.global.util.converter.JsonConverter;

public record SnsUserMessageEndpointDto(
	String id,
	String sourceUser_id,
	String snsUserMessageRoom_id,
	String msgTextContent,
	MsgMediaType msgMediaType,
	String msgMediaContent,
	String msgMetaInfo,
	LocalDateTime createdAt,
	LocalDateTime deletedAt,
	LocalDateTime lastUpdatedAt
) {

	public static SnsUserMessageEndpointDto fromEntity(SnsUserMessage snsUserMessage){
		return new SnsUserMessageEndpointDto(
			snsUserMessage.getId().toString(),
			snsUserMessage.getSourceUser().getId().toString(),
			snsUserMessage.getSnsUserMessageRoom().getId().toString(),
			snsUserMessage.getMsgTextContent(),
			snsUserMessage.getMsgMediaType(),
			snsUserMessage.getMsgMediaContent(),
			JsonConverter.convertToJsonString(snsUserMessage.getMsgMetaInfo()),
			snsUserMessage.getCreatedAt(),
			snsUserMessage.getDeletedAt(),
			snsUserMessage.getLastUpdatedAt()
			);
	}
}
