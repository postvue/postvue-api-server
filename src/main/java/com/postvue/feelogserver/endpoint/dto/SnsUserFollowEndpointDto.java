package com.postvue.feelogserver.endpoint.dto;

import java.time.LocalDateTime;

import com.postvue.feelogserver.domain.snsuserfollows.SnsUserFollow;

public record SnsUserFollowEndpointDto(
	String id,
	String followingUser_id,
	String followingUser_username,
	String followerUser_id,
	String followerUser_username,
	LocalDateTime createdAt
) {
	public static SnsUserFollowEndpointDto fromEntity(SnsUserFollow snsUserFollow){
		return new SnsUserFollowEndpointDto(snsUserFollow.getId().toString(),
			snsUserFollow.getFollowingUser().getId().toString(),
			snsUserFollow.getFollowingUser().getUsername(),
			snsUserFollow.getFollowerUser().getId().toString(),
			snsUserFollow.getFollowerUser().getUsername(),
			snsUserFollow.getCreatedAt());
	}
}
