package com.postvue.feelogserver.domain.snsscrap.repository;

import java.sql.PreparedStatement;
import java.sql.Types;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.postvue.feelogserver.core.config.SnowflakeComponent;
import com.postvue.feelogserver.domain.snsposts.SnsPost;
import com.postvue.feelogserver.domain.snspostuserreactions.SnsPostUserReaction;
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

	@Transactional(propagation = Propagation.REQUIRED)
	public void updateScrapDeletedByScrapBoard(Long snsScrapBoardId) {
		String sql = "UPDATE sns_scraps_tb SET "
			+ "deleted_at = ? "
			+ "WHERE sns_scrap_board_id = ?";

		LocalDateTime deletedTime = LocalDateTime.now();

		jdbcTemplate.update(connection -> {
			PreparedStatement ps = connection.prepareStatement(sql);
			ps.setObject(1, deletedTime);
			ps.setLong(2, snsScrapBoardId);
			return ps;
		});
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public void deleteScrapDeletedByScrapBoard(Long snsScrapBoardId) {
		String sql = "DELETE FROM sns_scraps_tb "
			+ "WHERE sns_scrap_board_id = ?";

		jdbcTemplate.update(connection -> {
			PreparedStatement ps = connection.prepareStatement(sql);
			ps.setLong(1, snsScrapBoardId);
			return ps;
		});
	}
}
