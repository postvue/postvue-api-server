package com.postvue.feelogserver.domain.snspostuserreactions.dao;

public interface PostLikeDao {
	Long getUserId();

	Long getCursorId();

	String getUsername();

	String getNickname();

	String getProfilePath();

	Boolean getIsFollowed();

	Boolean getIsMe();

}
