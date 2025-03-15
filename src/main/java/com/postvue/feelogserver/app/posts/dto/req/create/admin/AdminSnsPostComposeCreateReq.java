package com.postvue.feelogserver.app.posts.dto.req.create.admin;

import java.util.List;

import jakarta.annotation.Nullable;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class AdminSnsPostComposeCreateReq {
	private String username;

	private String title;

	private String bodyText;

	private List<String> tagList;

	@Nullable
	private String address;

	@Nullable
	private String buildName;

	@Nullable
	private Float latitude;

	@Nullable
	private Float longitude;

	private Integer targetAudienceValue;

	private List<String> imageFilePathList;
}
