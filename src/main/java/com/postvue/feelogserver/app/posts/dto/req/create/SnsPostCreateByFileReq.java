package com.postvue.feelogserver.app.posts.dto.req.create;

import java.util.List;

import com.postvue.feelogserver.app.posts.dto.common.PostContent;

import lombok.Getter;

@Getter
public class SnsPostCreateByFileReq {
	private List<String> tagList;
	private Float latitude;
	private Float longitude;
	
	private List<PostContent> postContents;

	private String title;
	private String bodyText;
}
