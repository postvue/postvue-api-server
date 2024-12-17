package com.postvue.feelogserver.domain.snsusers.repository;

import java.sql.PreparedStatement;
import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.postvue.feelogserver.domain.snsnotifications.SnsNotification;
import com.postvue.feelogserver.domain.snspostuserreactions.SnsPostUserReaction;
import com.postvue.feelogserver.domain.snsusers.SnsUser;
import com.postvue.feelogserver.domain.snsusers.vo.SnsUserState;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class SnsUserJdbcRepository {
	private final JdbcTemplate jdbcTemplate;

	@Transactional(propagation = Propagation.REQUIRED)
	public void updateAllFullDelete(List<SnsUser> snsUserList) {
		String sql = "UPDATE sns_users_tb SET sns_user_state = ? WHERE sns_user_id = ?";

		jdbcTemplate.batchUpdate(sql,
			snsUserList,
			snsUserList.size(),
			(PreparedStatement ps, SnsUser snsUser) -> {
				ps.setObject(1, SnsUserState.FULL_DELETED);
				ps.setLong(2, snsUser.getId());
			});
	}
}
