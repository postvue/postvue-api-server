package com.postvue.feelogserver.app.posts.dto.rsp.get;

import java.time.LocalDateTime;
import java.util.List;

import com.postvue.feelogserver.app.posts.dto.common.Location;
import com.postvue.feelogserver.app.posts.dto.common.PostContent;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
@AllArgsConstructor
public class SnsPostRsp {
	private String postId;
	private String userId;
	private String username;
	private String profilePath;
	private Location location;
	private List<String> tags;
	private Boolean isFollowed;
	private Boolean followable;
	private Boolean isLiked;
	private Boolean isClipped;
	private Boolean isReposted;
	private Boolean isBookmarked;
	private List<PostContent> postContents;
	private String postTitle;
	private String postBodyText;
	// @REFER: 제거
	// private String postCategory;
	private LocalDateTime postedAt;
}
