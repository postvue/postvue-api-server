package com.postvue.feelogserver.domain.snsscrapboard.dao;

import java.time.LocalDateTime;

import com.postvue.feelogserver.domain.snsscrapboard.vo.ScrapTargetAudience;

public interface ScrapPreviewDao {
	Long getScrapBoardId();
	String getScrapBoardName();

	Boolean getIsScraped();

}
