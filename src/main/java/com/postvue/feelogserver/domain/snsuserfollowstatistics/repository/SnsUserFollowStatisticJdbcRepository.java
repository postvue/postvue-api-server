package com.postvue.feelogserver.domain.snsuserfollowstatistics.repository;

import java.sql.PreparedStatement;
import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.postvue.feelogserver.domain.snsuserfollows.SnsUserFollow;
import com.postvue.feelogserver.domain.snsuserfollowstatistics.SnsUserFollowStatistic;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class SnsUserFollowStatisticJdbcRepository {
	private final JdbcTemplate jdbcTemplate;

	@Transactional(propagation = Propagation.REQUIRED)
	public void deleteAll(List<SnsUserFollowStatistic> snsUserFollowStatisticList) {
		String sql = "DELETE FROM SNS_USER_FOLLOW_STATISTICS_TB WHERE sns_user_follow_statistic_id = ?";

		jdbcTemplate.batchUpdate(sql,
			snsUserFollowStatisticList,
			snsUserFollowStatisticList.size(),
			(PreparedStatement ps, SnsUserFollowStatistic snsUserFollowStatistic) -> {
				ps.setLong(1, snsUserFollowStatistic.getId());
			});
	}
}
