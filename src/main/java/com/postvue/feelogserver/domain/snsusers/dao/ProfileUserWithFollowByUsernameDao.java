package com.postvue.feelogserver.domain.snsusers.dao;

public interface ProfileUserWithFollowByUsernameDao {
	Long getSnsUserId();

	String getUsername();

	String getNickname();

	String getProfilePath();

	Boolean getIsFollowed();
}

