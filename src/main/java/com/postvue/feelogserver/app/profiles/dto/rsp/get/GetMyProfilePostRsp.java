package com.postvue.feelogserver.app.profiles.dto.rsp.get;

import java.time.LocalDateTime;

import com.postvue.feelogserver.app.posts.dto.common.Location;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class GetMyProfilePostRsp {
	private String postId;
	private Location location;
	private String postThumbnailContent;
	private String postThumbnailContentType;
	private String username;
	private String userId;
	private LocalDateTime postedAt;
}
