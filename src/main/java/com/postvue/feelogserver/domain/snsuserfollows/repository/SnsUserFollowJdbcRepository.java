package com.postvue.feelogserver.domain.snsuserfollows.repository;

import java.sql.PreparedStatement;
import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.postvue.feelogserver.domain.snsnotifications.SnsNotification;
import com.postvue.feelogserver.domain.snsuserfollows.SnsUserFollow;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class SnsUserFollowJdbcRepository {
	private final JdbcTemplate jdbcTemplate;

	@Transactional(propagation = Propagation.REQUIRED)
	public void deleteAll(List<SnsUserFollow> snsUserFollowList) {
		String sql = "DELETE FROM sns_user_follows_tb WHERE sns_user_follow_id = ?";

		jdbcTemplate.batchUpdate(sql,
			snsUserFollowList,
			snsUserFollowList.size(),
			(PreparedStatement ps, SnsUserFollow snsUserFollow) -> {
				ps.setLong(1, snsUserFollow.getId());
			});
	}
}
