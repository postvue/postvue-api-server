package com.postvue.feelogserver.domain.snspostuserreactions.repository;

import java.sql.PreparedStatement;
import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.postvue.feelogserver.core.config.SnowflakeComponent;
import com.postvue.feelogserver.domain.snspostuserreactions.SnsPostUserReaction;
import com.postvue.feelogserver.domain.snstags.SnsTag;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class SnsPostUserReactionJdbcRepository {
	private final JdbcTemplate jdbcTemplate;

	@Transactional(propagation = Propagation.REQUIRED)
	public void updateAll(List<SnsPostUserReaction> snsPostUserReactionList) {
		String sql = "UPDATE sns_post_user_reactions_tb SET is_clipped = ? WHERE sns_post_user_reaction_id = ?";

		jdbcTemplate.batchUpdate(sql,
			snsPostUserReactionList,
			snsPostUserReactionList.size(),
			(PreparedStatement ps, SnsPostUserReaction snsPostUserReaction) -> {
				ps.setBoolean(1, snsPostUserReaction.getIsClipped());
				ps.setLong(2, snsPostUserReaction.getId());
			});
	}
}
