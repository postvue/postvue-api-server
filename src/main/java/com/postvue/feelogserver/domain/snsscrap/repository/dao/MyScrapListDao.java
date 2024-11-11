package com.postvue.feelogserver.domain.snsscrap.repository.dao;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.postvue.feelogserver.domain.snsposts.dto.SnsPostContentDao;

public interface MyScrapListDao {
	ObjectMapper objectMapper = new ObjectMapper();

	Long getSnsScrapBoardId();

	String getScrapName();

	Integer getScrapNum();

	String getSnsPostContents();

	LocalDateTime getRecentlyPostedAt();

	default List<SnsPostContentDao> getStringToSnsPostContents() {
		try {
			return objectMapper.readValue(getSnsPostContents(), new TypeReference<List<SnsPostContentDao>>() {
			});
		} catch (IOException e) {
			throw new RuntimeException("Failed to convert JSON to List<SnsPostContentDao>");
		}
	}
}
