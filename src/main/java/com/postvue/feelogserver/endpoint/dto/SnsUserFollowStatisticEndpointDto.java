package com.postvue.feelogserver.endpoint.dto;

import java.time.LocalDateTime;

import com.postvue.feelogserver.domain.snstagfollows.SnsTagFollow;
import com.postvue.feelogserver.domain.snsuserfollowstatistics.SnsUserFollowStatistic;
import com.postvue.feelogserver.domain.snsusers.SnsUser;

import jakarta.persistence.Column;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

public record SnsUserFollowStatisticEndpointDto(
	String id,
	String snsUser_id,
	String snsUser_username,
	Integer followerNum,
	Integer followingNum
) {

	public static SnsUserFollowStatisticEndpointDto fromEntity(SnsUserFollowStatistic snsUserFollowStatistic){
		return new SnsUserFollowStatisticEndpointDto(
			snsUserFollowStatistic.getId().toString(),
			snsUserFollowStatistic.getSnsUser().getId().toString(),
			snsUserFollowStatistic.getSnsUser().getUsername(),
			snsUserFollowStatistic.getFollowerNum(),
			snsUserFollowStatistic.getFollowingNum()
		);
	}
}
