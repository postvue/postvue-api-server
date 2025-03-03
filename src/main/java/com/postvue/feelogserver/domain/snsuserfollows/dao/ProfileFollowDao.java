package com.postvue.feelogserver.domain.snsuserfollows.dao;

public interface ProfileFollowDao {

	Long getSnsUserId();

	String getProfilePath();

	String getUsername();

	String getNickname();

	Boolean getIsFollowed();

	Boolean getIsMe();
}
