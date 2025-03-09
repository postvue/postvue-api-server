package com.postvue.feelogserver.domain.snsscrap.repository;

import java.sql.PreparedStatement;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.postvue.feelogserver.core.config.SnowflakeComponent;
import com.postvue.feelogserver.domain.snsscrap.SnsScrap;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class SnsScrapJdbcRepository {
	private final JdbcTemplate jdbcTemplate;
	private final SnowflakeComponent snowflakeComponent;

	public void saveAll(List<SnsScrap> snsScraps) {
		String sql = "INSERT INTO sns_scraps_tb "
			+ "(sns_scrap_id, sns_scrap_board_id, sns_user_id, sns_post_id, created_at, last_updated_by) "
			+ "VALUES (?, ?, ?, ?, ?, ?)";

		LocalDateTime localDateTime = LocalDateTime.now();
		jdbcTemplate.batchUpdate(sql,
			snsScraps,
			snsScraps.size(),
			(PreparedStatement ps, SnsScrap snsScrap) -> {
				snsScrap.setId(snowflakeComponent.nextId());
				ps.setLong(1, snsScrap.getId());
				ps.setLong(2, snsScrap.getSnsScrapBoard().getId());
				ps.setLong(3, snsScrap.getSnsUser().getId());
				ps.setLong(4, snsScrap.getSnsPost().getId());
				ps.setObject(5, localDateTime);
				ps.setLong(6, snsScrap.getSnsUser().getId());
			});
	}
}
