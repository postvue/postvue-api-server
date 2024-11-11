package com.postvue.feelogserver.app.recomm.dto.rsp;

import java.util.List;

import com.postvue.feelogserver.app.recomm.dto.GetPostContent;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class GetRecommFollowRsp {
	private String followId;
	private String profilePath;
	private String username;
	private List<GetPostContent> postPreviewImgUrlList;
	private Integer followerNum;
	private Integer followingNum;
}
