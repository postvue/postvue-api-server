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
public class SnsPostInfoRsp {
	private String postId;
	private String userId;
	private Location location;
	private List<String> tags;
	private List<PostContent> postContents;
	private String postTitle;
	private String postBodyText;
	private Integer targetAudTypeId;
	private LocalDateTime postedAt;
}
