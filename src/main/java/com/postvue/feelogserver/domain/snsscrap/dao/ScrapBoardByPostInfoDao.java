package com.postvue.feelogserver.domain.snsscrap.dao;

import java.time.LocalDateTime;

import com.postvue.feelogserver.domain.snsscrapboard.vo.ScrapTargetAudience;

public interface ScrapBoardByPostInfoDao {
	Long getScrapId();
	String getScrapName();
}
