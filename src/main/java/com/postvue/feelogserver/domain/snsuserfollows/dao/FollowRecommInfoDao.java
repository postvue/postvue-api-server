package com.postvue.feelogserver.domain.snsuserfollows.dao;

import java.io.IOException;
import java.util.List;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

public interface FollowRecommInfoDao {
	ObjectMapper objectMapper = new ObjectMapper();

	Long getSnsUserId();

	String getProfilePath();

	String getUsername();

	String getPostIdContentsString();

	Integer getFollowerNum();

	Integer getFollowingNum();

	default List<FollowPostIdContentsDao> getPostIdContents() {
		String postIdContentsString = getPostIdContentsString();

		try {
			return objectMapper.readValue(getPostIdContentsString(),
				new TypeReference<List<FollowPostIdContentsDao>>() {
				});
		} catch (IOException e) {
			throw new RuntimeException("Failed to convert JSON to List<SnsPostContentDao>");
		}

	}

}
