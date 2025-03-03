package com.postvue.feelogserver.endpoint.dto;

import java.time.LocalDateTime;

import com.postvue.feelogserver.domain.snsposts.SnsPost;
import com.postvue.feelogserver.domain.snsposts.vo.PostContentBusinessType;
import com.postvue.feelogserver.domain.snsposts.vo.TgtAudType;
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
	String buildName,

	Long h3Index,
	String geom,
	Boolean isShowAddress,
	String tags,
	Boolean isRepost,
	String repostOrigin_id,

	TgtAudType tgtAudType,

	PostContentBusinessType postContentBusinessType,
	Integer reactionCount,

	LocalDateTime createdAt,
	LocalDateTime deletedAt
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
			snsPost.getBuildName(),
			snsPost.getH3Index(),
			snsPost.getGeom() != null ? snsPost.getGeom().toText() : "",
			snsPost.getIsShowAddress(),
			JsonConverter.convertToJsonString(snsPost.getTags()),
			snsPost.getIsRepost(),
			snsPost.getRepostOrigin() != null ? snsPost.getId().toString() : null,
			snsPost.getTgtAudType(),
			snsPost.getPostContentBusinessType(),
			snsPost.getReactionCount(),
			snsPost.getCreatedAt(),
			snsPost.getDeletedAt()
		);
	}
}
