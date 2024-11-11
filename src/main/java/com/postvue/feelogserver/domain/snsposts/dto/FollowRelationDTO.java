package com.postvue.feelogserver.domain.snsposts.dto;

public class FollowRelationDTO {
	private Long followingId;
	private Long followerId;

	public FollowRelationDTO(Long followingId, Long followerId) {
		this.followingId = followingId;
		this.followerId = followerId;
	}
}
