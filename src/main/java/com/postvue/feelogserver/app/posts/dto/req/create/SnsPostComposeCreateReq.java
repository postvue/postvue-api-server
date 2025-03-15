package com.postvue.feelogserver.app.posts.dto.req.create;

import java.util.List;

import com.postvue.feelogserver.app.posts.dto.common.PostContent;
import com.postvue.feelogserver.global.constant.PostConst;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class SnsPostComposeCreateReq {
	private List<String> tagList;

	@Nullable
	private String address;

	@Nullable
	private String buildName;

	@Nullable
	private Float latitude;

	@Nullable
	private Float longitude;

	private List<String> externalImgLinkList;

	private String title;

	private String bodyText;

	private Integer targetAudienceValue;

	private List<String> scrapIdList;
}
