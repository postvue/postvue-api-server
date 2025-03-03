package com.postvue.feelogserver.endpoint.dto;

import java.time.LocalDateTime;

import com.postvue.feelogserver.domain.snsscrap.SnsScrap;

public record SnsScrapEndpointDto(
	String id,
	String snsUser_id,
	String snsPost_id,
	String snsScrapBoard_id,
	LocalDateTime createdAt,
	LocalDateTime deletedAt
) {

	public static SnsScrapEndpointDto fromEntity(SnsScrap snsScrap){
		return new SnsScrapEndpointDto(
			snsScrap.getId().toString(),
			snsScrap.getSnsUser().getId().toString(),
			snsScrap.getSnsPost().getId().toString(),
			snsScrap.getSnsScrapBoard().getId().toString(),
			snsScrap.getCreatedAt(),
			snsScrap.getDeletedAt()
		);
	}
}
