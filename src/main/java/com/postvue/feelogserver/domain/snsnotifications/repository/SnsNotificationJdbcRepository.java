package com.postvue.feelogserver.domain.snsnotifications.repository;

import java.sql.PreparedStatement;
import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.postvue.feelogserver.domain.snsnotifications.SnsNotification;
import com.postvue.feelogserver.domain.snspostuserreactions.SnsPostUserReaction;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class SnsNotificationJdbcRepository {
	private final JdbcTemplate jdbcTemplate;

	@Transactional(propagation = Propagation.REQUIRED)
	public void deleteAll(List<SnsNotification> snsNotificationList) {
		String sql = "DELETE FROM sns_notifications_tb WHERE sns_notification_id = ?";

		jdbcTemplate.batchUpdate(sql,
			snsNotificationList,
			snsNotificationList.size(),
			(PreparedStatement ps, SnsNotification snsNotification) -> {
				ps.setLong(1, snsNotification.getId());
			});
	}
}
