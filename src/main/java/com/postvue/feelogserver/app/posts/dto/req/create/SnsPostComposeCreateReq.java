package com.postvue.feelogserver.app.posts.dto.req.create;

import java.util.List;

import com.postvue.feelogserver.app.posts.dto.common.PostContent;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;

@Getter
public class SnsPostComposeCreateReq {
	private List<String> tagList;
	@Nullable
	private String address;

	private List<PostContent> postContentLinkList;

	private String title;
	private String bodyText;

	private Integer targetAudienceValue;
}
