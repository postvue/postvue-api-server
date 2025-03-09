package com.postvue.feelogserver.domain.snsposts.dto;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.postvue.feelogserver.domain.snsposts.vo.TgtAudType;
import com.postvue.feelogserver.domain.snstags.dao.PostTagDao;

public interface SnsPostInfoDao {
	ObjectMapper objectMapper = new ObjectMapper();

	Long getPostId();

	Float getLatitude();

	Float getLongitude();

	String getAddress();

	String getBuildName();

	TgtAudType getTgtAudType();

	String getPostTitle();

	String getPostBodyText();

	String getSnsPostContents();

	String getTags();

	Long getSnsUserId();

	String getUsername();

	LocalDateTime getPostedAt();

	default List<SnsPostContentDao> getStringToSnsPostContents() {
		try {
			return objectMapper.readValue(getSnsPostContents(), new TypeReference<List<SnsPostContentDao>>() {
			});
		} catch (IOException e) {
			throw new RuntimeException("Failed to convert JSON to List<SnsPostContentDao>");
		}
	}

	default List<PostTagDao> getStringToTags() {
		try {
			return objectMapper.readValue(getTags(), new TypeReference<List<PostTagDao>>() {
			});
		} catch (IOException e) {
			throw new RuntimeException("Failed to convert JSON to List<PostTag>");
		}
	}
}
