package com.postvue.feelogserver.domain.snstagposts.respository;

import java.sql.PreparedStatement;
import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.postvue.feelogserver.core.config.SnowflakeComponent;
import com.postvue.feelogserver.domain.snstagposts.SnsTagPost;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class SnsTagPostJdbcRepository {
	private final JdbcTemplate jdbcTemplate;
	private final SnowflakeComponent snowflakeComponent;

	@Transactional
	public void saveAll(List<SnsTagPost> snsTagPosts) {
		String sql = "INSERT INTO sns_tag_posts_tb (sns_tag_post_id, sns_tag_id, sns_post_id) VALUES (?, ?, ?)";

		jdbcTemplate.batchUpdate(sql,
			snsTagPosts,
			snsTagPosts.size(),
			(PreparedStatement ps, SnsTagPost snsTagPost) -> {
				snsTagPost.setId(snowflakeComponent.nextId());
				ps.setLong(1, snsTagPost.getId());
				ps.setLong(2, snsTagPost.getSnsTag().getId());
				ps.setLong(3, snsTagPost.getSnsPost().getId());
			});
	}
}
