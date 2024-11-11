package com.postvue.feelogserver.domain.snsposts.dto;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.postvue.feelogserver.domain.snstags.dao.PostTagDao;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class PostDao {
	private static final ObjectMapper objectMapper = new ObjectMapper();

	private Long postId;
	private Boolean isLiked;
	private Boolean isReposted;
	private Boolean isClipped;
	private Boolean isBookmarked;
	private Boolean followable;
	private Long followingId;
	private Float latitude;
	private Float longitude;
	private String address;
	private String postTitle;
	private String postBodyText;
	private String snsPostContents;
	private String tags;
	private String profilePath;
	private Long snsUserId;
	private String username;
	private LocalDateTime postedAt;

	// Constructor

	// Getters and Setters for each field
	// ...

	// Methods to convert JSON string to List<SnsPostContentDao>
	public List<SnsPostContentDao> getStringToSnsPostContents() {
		try {
			return objectMapper.readValue(snsPostContents, new TypeReference<List<SnsPostContentDao>>() {
			});
		} catch (IOException e) {
			throw new RuntimeException("Failed to convert JSON to List<SnsPostContentDao>");
		}
	}

	// Methods to convert JSON string to List<PostTagDao>
	public List<PostTagDao> getStringToTags() {
		try {
			return objectMapper.readValue(tags, new TypeReference<List<PostTagDao>>() {
			});
		} catch (IOException e) {
			throw new RuntimeException("Failed to convert JSON to List<PostTagDao>");
		}
	}
}
