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
			+ "sns_post_id, "
			+ "sns_post_contents, "
			+ "sns_user_id, "
			+ "post_title, "
			+ "post_body_text, "
			+ "latitude, "
			+ "longitude, "
			+ "address, "
			+ "build_name, "
			+ "tags, "
			+ "created_at, "
			+ "last_updated_at, "
			+ "tgt_aud_type, "
			+ "h3_index, "
			+ "geom "
			+ ") VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ST_GeomFromText(?, 4326))";

		// ID 설정
		snsPost.setId(snowflakeComponent.nextId());

		// JSON 변환
		String snsPostContentsString = objectMapper.writeValueAsString(snsPost.getSnsPostContents());
		String tagsString = objectMapper.writeValueAsString(snsPost.getTags());

		// 현재 시간 설정
		LocalDateTime localDateTime = LocalDateTime.now();

		// JTS Point를 WKT 형식으로 변환 (null 체크 포함)
		String wkt = (snsPost.getGeom() != null)
			? String.format("POINT(%f %f)", snsPost.getGeom().getX(), snsPost.getGeom().getY())
			: null;

		// 데이터 삽입
		jdbcTemplate.update(connection -> {
			PreparedStatement ps = connection.prepareStatement(sql);
			ps.setLong(1, snsPost.getId());
			ps.setObject(2, snsPostContentsString, Types.OTHER);
			ps.setLong(3, snsPost.getSnsUser().getId());
			ps.setString(4, snsPost.getPostTitle());
			ps.setString(5, snsPost.getPostBodyText());
			ps.setObject(6, snsPost.getLatitude() != null ? snsPost.getLatitude() : null, Types.FLOAT);
			ps.setObject(7, snsPost.getLongitude() != null ? snsPost.getLongitude() : null, Types.FLOAT);
			ps.setString(8, snsPost.getAddress());
			ps.setString(9, snsPost.getBuildName());
			ps.setObject(10, tagsString, Types.OTHER);
			ps.setObject(11, localDateTime);
			ps.setObject(12, localDateTime);
			ps.setString(13, snsPost.getTgtAudType().toString());

			if (snsPost.getH3Index() != null){
				ps.setLong(14, snsPost.getH3Index());
			}
			else{
				ps.setNull(14, Types.BIGINT);
			}

			// geom 필드 처리
			if (wkt != null) {
				ps.setString(15, wkt);
			} else {
				ps.setNull(15, Types.VARCHAR);
			}

			return ps;
		});
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public void updatePost(SnsPost snsPost) throws JsonProcessingException {
		String sql = "UPDATE sns_posts_tb SET "
			+ "sns_post_contents = ?, "
			+ "sns_user_id = ?, "
			+ "post_title = ?, "
			+ "post_body_text = ?, "
			+ "latitude = ?, "
			+ "longitude = ?, "
			+ "address = ?, "
			+ "tags = ?, "
			+ "last_updated_at = ?, "
			+ "tgt_aud_type = ?  "
			+ "WHERE sns_post_id = ?";

		String snsPostContentsString = objectMapper.writeValueAsString(snsPost.getSnsPostContents());
		String tagsString = objectMapper.writeValueAsString(snsPost.getTags());

		LocalDateTime localDateTime = LocalDateTime.now();

		jdbcTemplate.update(connection -> {
			PreparedStatement ps = connection.prepareStatement(sql);
			ps.setObject(1, snsPostContentsString, Types.OTHER);
			ps.setLong(2, snsPost.getSnsUser().getId());
			ps.setString(3, snsPost.getPostTitle());
			ps.setString(4, snsPost.getPostBodyText());
			ps.setObject(5, snsPost.getLatitude() != null ? snsPost.getLatitude() : null, Types.FLOAT);
			ps.setObject(6, snsPost.getLongitude() != null ? snsPost.getLongitude() : null, Types.FLOAT);
			ps.setString(7, snsPost.getAddress());
			ps.setObject(8, tagsString, Types.OTHER);
			ps.setObject(9, localDateTime); // Update the `last_updated_at` field
			ps.setObject(10, snsPost.getTgtAudType().toString());
			ps.setLong(11, snsPost.getId()); // Use `sns_post_id` to find the record to update
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
