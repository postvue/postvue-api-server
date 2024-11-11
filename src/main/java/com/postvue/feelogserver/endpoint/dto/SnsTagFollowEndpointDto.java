package com.postvue.feelogserver.endpoint.dto;

import java.time.LocalDateTime;

import com.postvue.feelogserver.domain.snstagfollows.SnsTagFollow;

public record SnsTagFollowEndpointDto(
	String id,
	String snsUser_id,
	String snsTag_id,
	String tagName,
	LocalDateTime createdAt
) {

	public static SnsTagFollowEndpointDto fromEntity(SnsTagFollow snsTagFollow){
		return new SnsTagFollowEndpointDto(
			snsTagFollow.getId().toString(),
			snsTagFollow.getSnsUser().getId().toString(),
			snsTagFollow.getSnsTag().getId().toString(),
			snsTagFollow.getSnsTag().getTagName(),
			snsTagFollow.getCreatedAt()
		);
	}
}
