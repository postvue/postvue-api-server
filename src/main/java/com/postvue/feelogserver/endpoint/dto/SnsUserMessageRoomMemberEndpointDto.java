package com.postvue.feelogserver.endpoint.dto;

import java.time.LocalDateTime;

import com.postvue.feelogserver.domain.snsusermessageroommembers.SnsUserMessageRoomMember;
import com.postvue.feelogserver.domain.snsusermessagerooms.vo.MsgRoomType;

public record SnsUserMessageRoomMemberEndpointDto(
	String id,
	String snsUserMessageRoom_id,
	String sourceUser_id,
	String targetUser_id,
	MsgRoomType msgRoomType,
	LocalDateTime readAt,
	Boolean isHidden,
	Boolean isBlocked,
	LocalDateTime createdAt
) {
	public static SnsUserMessageRoomMemberEndpointDto fromEntity(SnsUserMessageRoomMember snsUserMessageRoomMember){
		return new SnsUserMessageRoomMemberEndpointDto(
			snsUserMessageRoomMember.getId().toString(),
			snsUserMessageRoomMember.getSnsUserMessageRoom() !=null ? snsUserMessageRoomMember.getSnsUserMessageRoom().getId().toString() : null,
			snsUserMessageRoomMember.getSourceUser() != null ? snsUserMessageRoomMember.getSourceUser().getId().toString() : null,
			snsUserMessageRoomMember.getTargetUser() != null ? snsUserMessageRoomMember.getTargetUser().getId().toString() : null,
			snsUserMessageRoomMember.getMsgRoomType(),
			snsUserMessageRoomMember.getReadAt(),
			snsUserMessageRoomMember.getIsHidden(),
			snsUserMessageRoomMember.getIsBlocked(),
			snsUserMessageRoomMember.getCreatedAt()
			);
	}
}
