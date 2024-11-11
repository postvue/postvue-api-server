package com.postvue.feelogserver.domain.snsscrapboard.dao;

import java.time.LocalDateTime;

public interface ScrapBoardInfoDao {
	String getScrapName();

	Long getScrapBoardId();

	Integer getScrapNum();

	LocalDateTime getRecentlyPostedAt();
}
