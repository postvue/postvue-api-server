package com.postvue.feelogserver.domain.snsusers.dao;

public interface ProfileInfoWithFollowDao {
	Long getSnsUserId();

	String getUsername();

	String getUserDescription();

	String getNickname();

	String getProfilePath();

	String getUserLink();

	Boolean getIsFollowed();

	Boolean getIsBlocked();

	Integer getFollowingNum();

	Integer getFollowerNum();
}

