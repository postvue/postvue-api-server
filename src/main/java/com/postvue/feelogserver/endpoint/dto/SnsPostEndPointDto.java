package com.postvue.feelogserver.endpoint.dto;

import java.util.List;

import com.postvue.feelogserver.domain.snsposts.SnsPost;
import com.postvue.feelogserver.domain.snsposts.vo.PostContentBusinessType;
import com.postvue.feelogserver.domain.snsposts.vo.SnsPostContent;
import com.postvue.feelogserver.domain.snsposts.vo.TgtAudType;
import com.postvue.feelogserver.domain.snstags.vo.PostTag;
import com.postvue.feelogserver.global.util.converter.JsonConverter;

public record SnsPostEndPointDto(
	String id,
	String snsUser_id,
	Boolean isExposed,
	String snsPostContents,
	String postTitle,
	String postBodyText,
	String postCaptionContent,
	Float latitude,
	Float longitude,
	String address,
	Boolean isShowAddress,
	String tags,
	Boolean isRepost,
	String repostOrigin_id,

	TgtAudType tgtAudType,

	PostContentBusinessType postContentBusinessType
) {

	public static SnsPostEndPointDto fromEntity(SnsPost snsPost){
		return new SnsPostEndPointDto(
			snsPost.getId().toString(),
			snsPost.getSnsUser().getId().toString(),
			snsPost.getIsExposed(),
			JsonConverter.convertToJsonString(snsPost.getSnsPostContents()),
			snsPost.getPostTitle(),
			snsPost.getPostBodyText(),
			snsPost.getPostCaptionContent(),
			snsPost.getLatitude(),
			snsPost.getLongitude(),
			snsPost.getAddress(),
			snsPost.getIsShowAddress(),
			JsonConverter.convertToJsonString(snsPost.getTags()),
			snsPost.getIsRepost(),
			snsPost.getRepostOrigin() != null ? snsPost.getId().toString() : null,
			snsPost.getTgtAudType(),
			snsPost.getPostContentBusinessType()
		);
	}
}
