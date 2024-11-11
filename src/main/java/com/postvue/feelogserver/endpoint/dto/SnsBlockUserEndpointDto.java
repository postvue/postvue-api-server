package com.postvue.feelogserver.endpoint.dto;

import java.time.LocalDateTime;

import com.postvue.feelogserver.domain.snsblockusers.SnsBlockUser;

public record SnsBlockUserEndpointDto(
	String id,
	String snsBlockerUser_id,
	String snsBlockerUser_username,
	String snsBlockedUser_id,
	String snsBlockedUser_username,
	LocalDateTime isBlockedAt) {
	public static SnsBlockUserEndpointDto fromEntity(SnsBlockUser snsBlockUser){
		return new SnsBlockUserEndpointDto(
			snsBlockUser.getId().toString(),snsBlockUser.getSnsBlockerUser().getId().toString()
			,snsBlockUser.getSnsBlockerUser().getUsername(),snsBlockUser.getSnsBlockedUser().getId().toString(),
			snsBlockUser.getSnsBlockedUser().getUsername(),snsBlockUser.getIsBlockedAt());
	}
}
