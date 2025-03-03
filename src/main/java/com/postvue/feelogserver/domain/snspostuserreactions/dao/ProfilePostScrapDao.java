package com.postvue.feelogserver.domain.snspostuserreactions.dao;

import java.time.LocalDateTime;
import java.util.List;

import com.postvue.feelogserver.domain.snsposts.vo.SnsPostContent;
import com.postvue.feelogserver.domain.snstags.vo.PostTag;

public interface ProfilePostScrapDao {
	Long getCursorId();

	Long getPostId();

	Boolean getIsLiked();

	Boolean getIsReposted();

	Boolean getIsClipped();


	Boolean getFollowable();

	Long getFollowingId();

	Float getLatitude();

	Float getLongitude();

	String getAddress();
	String getBuildName();

	String getPostTitle();

	String getPostBodyText();

	List<SnsPostContent> getSnsPostContents();

	List<PostTag> getTags();

	String getProfilePath();

	Long getSnsUserId();

	String getUsername();

	LocalDateTime getPostedAt();
}
