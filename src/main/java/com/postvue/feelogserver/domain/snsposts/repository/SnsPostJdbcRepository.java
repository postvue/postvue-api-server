package com.postvue.feelogserver.domain.snsposts.repository;

import java.sql.PreparedStatement;
import java.sql.Types;
import java.time.LocalDateTime;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.postvue.feelogserver.core.config.SnowflakeComponent;
import com.postvue.feelogserver.domain.snsposts.SnsPost;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class SnsPostJdbcRepository {
	private final JdbcTemplate jdbcTemplate;
	private final SnowflakeComponent snowflakeComponent;
	private final ObjectMapper objectMapper;

	@Transactional(propagation = Propagation.REQUIRED)
	public void insertPost(SnsPost snsPost) throws JsonProcessingException {
		String sql = "INSERT INTO sns_posts_tb ("
			+ "sns_post_id, sns_post_contents, "
			+ "sns_user_id, "
			+ "post_title, "
			+ "post_body_text, "
			+ "latitude, "
			+ "longitude, "
			+ "address, "
			+ "tags, "
			+ "created_at, "
			+ "last_updated_at) "
			+ "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

		snsPost.setId(snowflakeComponent.nextId());

		String snsPostContentsString = objectMapper.writeValueAsString(snsPost.getSnsPostContents());
		String tagsString = objectMapper.writeValueAsString(snsPost.getTags());

		LocalDateTime localDateTime = LocalDateTime.now();

		jdbcTemplate.update(connection -> {
			PreparedStatement ps = connection.prepareStatement(sql);
			ps.setLong(1, snsPost.getId());
			ps.setObject(2, snsPostContentsString, Types.OTHER);
			ps.setLong(3, snsPost.getSnsUser().getId());
			ps.setString(4, snsPost.getPostTitle());
			ps.setString(5, snsPost.getPostBodyText());
			ps.setFloat(6, snsPost.getLatitude());
			ps.setFloat(7, snsPost.getLongitude());
			ps.setString(8, snsPost.getAddress());
			ps.setObject(9, tagsString, Types.OTHER);
			ps.setObject(10, localDateTime);
			ps.setObject(11, localDateTime);
			return ps;
		});
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public void insertRepost(SnsPost snsPost) throws JsonProcessingException {
		String sql = "INSERT INTO sns_posts_tb ("
			+ "sns_post_id, is_repost, repost_origin_id, "
			+ "sns_post_contents, sns_user_id, post_title, post_body_Text, latitude, longitude, tags, created_at, last_updated_at) "
			+ "VALUES (?, ?, ?, ?,?, ?, ?, ?, ?, ?, ?,?)";

		snsPost.setId(snowflakeComponent.nextId());

		String snsPostContentsString = objectMapper.writeValueAsString(snsPost.getSnsPostContents());
		String tagsString = objectMapper.writeValueAsString(snsPost.getTags());

		LocalDateTime localDateTime = LocalDateTime.now();

		jdbcTemplate.update(connection -> {
			PreparedStatement ps = connection.prepareStatement(sql);
			ps.setLong(1, snsPost.getId());
			ps.setBoolean(2, snsPost.getIsRepost());
			ps.setLong(3, snsPost.getRepostOrigin().getId());
			ps.setObject(4, snsPostContentsString, Types.OTHER);
			ps.setLong(5, snsPost.getSnsUser().getId());
			ps.setString(6, snsPost.getPostTitle());
			ps.setString(7, snsPost.getPostBodyText());
			ps.setFloat(8, snsPost.getLatitude());
			ps.setFloat(9, snsPost.getLongitude());
			ps.setObject(10, tagsString, Types.OTHER);
			ps.setObject(11, localDateTime);
			ps.setObject(12, localDateTime);
			return ps;
		});
	}
}
