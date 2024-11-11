package com.postvue.feelogserver.domain.snstagfollows.repository;

import java.sql.PreparedStatement;
import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.postvue.feelogserver.core.config.SnowflakeComponent;
import com.postvue.feelogserver.domain.snstagfollows.SnsTagFollow;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class SnsTagFollowJdbcRepository {
	private final JdbcTemplate jdbcTemplate;
	private final SnowflakeComponent snowflakeComponent;

	@Transactional
	public void saveAll(List<SnsTagFollow> snsTagFollows) {
		String sql = "INSERT INTO sns_tag_follows_tb ("
			+ "sns_tag_follow_id, sns_user_id, sns_tag_id, "
			+ "tag_name) VALUES (?, ?, ?, ?)";

		jdbcTemplate.batchUpdate(sql,
			snsTagFollows,
			snsTagFollows.size(),
			(PreparedStatement ps, SnsTagFollow snsTagFollow) -> {
				snsTagFollow.setId(snowflakeComponent.nextId());
				ps.setLong(1, snsTagFollow.getId());
				ps.setLong(2, snsTagFollow.getSnsUser().getId());
				ps.setLong(3, snsTagFollow.getSnsTag().getId());
				ps.setString(4, snsTagFollow.getTagName());
			});
	}
}
