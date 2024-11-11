package com.postvue.feelogserver.domain.snspostuserreactions.dao;

import java.time.LocalDateTime;
import java.util.List;

import com.postvue.feelogserver.domain.snsposts.vo.SnsPostContent;

public interface ProfilePostListDao {
	Long getCursorId();

	Long getPostId();

	Float getLongitude();

	Float getLatitude();

	String getAddress();

	Long getUserId();

	String getUsername();

	List<SnsPostContent> getPostContents();

	LocalDateTime getPostedAt();
}
