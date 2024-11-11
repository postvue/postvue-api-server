package com.postvue.feelogserver.app.posts.dto.req.create;

import java.util.List;

import com.postvue.feelogserver.app.posts.dto.common.PostContent;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;

@Getter
public class SnsPostCreateByResourceLinkReq {
	private List<String> tagList;
	@Nullable
	private Float latitude;
	@Nullable
	private Float longitude;
	private String address;

	@NotEmpty(message = "콘텐츠가 비어 있습니다.")
	private List<PostContent> postContents;

	private String title;
	private String bodyText;
}
