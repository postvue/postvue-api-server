package com.postvue.feelogserver.app.posts.dto.req.create;

import java.util.List;

import com.postvue.feelogserver.app.posts.dto.common.PostContent;
import com.postvue.feelogserver.global.constant.PostConst;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
public class SnsPostComposeUpdateReq extends SnsPostComposeCreateReq{
	private List<String> existPostContentList;

	public SnsPostComposeUpdateReq(List<String> tagList, String address, String buildName, Float latitude,
		Float longitude,
		List<String> externalImgLinkList, String title, String bodyText, Integer targetAudienceValue,
		List<String> scrapIdList) {
		super(tagList, address, buildName, latitude, longitude, externalImgLinkList, title, bodyText,
			targetAudienceValue,
			scrapIdList);
	}
}
