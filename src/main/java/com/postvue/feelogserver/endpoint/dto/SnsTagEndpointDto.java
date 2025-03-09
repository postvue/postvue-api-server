package com.postvue.feelogserver.endpoint.dto;

import java.time.LocalDateTime;

import com.postvue.feelogserver.domain.snsposts.vo.PostContentType;
import com.postvue.feelogserver.domain.snstags.SnsTag;

public record SnsTagEndpointDto(
	String id,
	String tagName,
	String tagRepsBatchContent,
	PostContentType tagRepsBatchContentType,
	Boolean isExposed,
	LocalDateTime createdAt,
	LocalDateTime deletedAt,
	LocalDateTime lastUpdatedAt

) {
	public static SnsTagEndpointDto fromEntity(SnsTag snsTag){
		return new SnsTagEndpointDto(snsTag.getId().toString(),
			snsTag.getTagName(),snsTag.getTagRepsBatchContent(),
			snsTag.getTagRepsBatchContentType(),
			snsTag.getIsExposed(),snsTag.getCreatedAt(),
			snsTag.getDeletedAt(),
			snsTag.getLastUpdatedAt()
		);
	}
}
