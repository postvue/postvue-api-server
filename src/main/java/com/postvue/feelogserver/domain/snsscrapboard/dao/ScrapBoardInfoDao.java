package com.postvue.feelogserver.domain.snsscrapboard.dao;

import java.time.LocalDateTime;

import com.postvue.feelogserver.domain.snsscrapboard.vo.ScrapTargetAudience;

public interface ScrapBoardInfoDao {
	String getScrapName();

	Long getScrapBoardId();

	Integer getScrapNum();

	LocalDateTime getRecentlyPostedAt();

	Boolean getIsMe();
	ScrapTargetAudience getTargetAudience();

	Long getUserId();
	String getUsername();
	String getNickname();
	String getProfilePath();

}
