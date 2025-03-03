package com.postvue.feelogserver.endpoint.dto;

import java.time.LocalDateTime;

import com.postvue.feelogserver.domain.snsscrapboard.SnsScrapBoard;
import com.postvue.feelogserver.domain.snsscrapboard.vo.ScrapTargetAudience;

public record SnsScrapBoardEndpointDto(
	String id,
	String snsUser_id,
	String scrapName,
	ScrapTargetAudience targetAudience,
	LocalDateTime createdAt,
	LocalDateTime lastUpdatedAt,
	String lastUpdatedBy,
	LocalDateTime deletedAt
) {
	public static SnsScrapBoardEndpointDto fromEntity(SnsScrapBoard snsScrapBoard){
		return new SnsScrapBoardEndpointDto(
			snsScrapBoard.getId().toString(),
			snsScrapBoard.getSnsUser().getId().toString(),
			snsScrapBoard.getScrapName(),
			snsScrapBoard.getTargetAudience(),
			snsScrapBoard.getCreatedAt(),
			snsScrapBoard.getLastUpdatedAt(),
			snsScrapBoard.getLastUpdatedBy() != null ? snsScrapBoard.getLastUpdatedBy().toString() : null,
			snsScrapBoard.getDeletedAt()
		);
	}
}
