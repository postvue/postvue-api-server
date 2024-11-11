package com.postvue.feelogserver.domain.snspostuserreactions.dao;

public interface PostIsRepostedDao {
	Long getUserId();

	String getUsername();

	String getNickname();

	String getProfilePath();

	Boolean getIsFollowed();

	Boolean getIsMe();
}
