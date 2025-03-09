package com.postvue.feelogserver.endpoint.dto;

import java.time.LocalDateTime;

import com.postvue.feelogserver.domain.snsusermessagerooms.SnsUserMessageRoom;
import com.postvue.feelogserver.domain.snsusermessagerooms.vo.MsgRoomType;

public record SnsUserMessageRoomEndpointDto(
	String id,
	MsgRoomType msgRoomType,
	LocalDateTime createdAt,
	LocalDateTime lastUpdatedAt
) {
	public static SnsUserMessageRoomEndpointDto fromEntity(SnsUserMessageRoom snsUserMessageRoom){
		return new SnsUserMessageRoomEndpointDto(
			snsUserMessageRoom.getId().toString(),
			snsUserMessageRoom.getMsgRoomType(),
			snsUserMessageRoom.getCreatedAt(),
			snsUserMessageRoom.getLastUpdatedAt()
		);
	}
}
