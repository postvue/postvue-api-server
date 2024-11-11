package com.postvue.feelogserver.domain.snspostcommentreactions.dao;

import java.time.LocalDateTime;

import com.postvue.feelogserver.domain.snspostcommentreactions.vo.PostCommentMediaType;

public interface PostCommentDao {
	Long getPostCommentId();

	Long getCommentUserId();
	String getCommentMsg();
	PostCommentMediaType getPostCommentMediaType();
	String getPostCommentMediaContent();

	Boolean getIsLiked();

	Integer getLikeCount();

	Integer getCommentCount();

	String getUsername();

	String getProfilePath();

	Long getCommentSourceId();

	Boolean getIsSource();

	LocalDateTime getPostedAt();
}
