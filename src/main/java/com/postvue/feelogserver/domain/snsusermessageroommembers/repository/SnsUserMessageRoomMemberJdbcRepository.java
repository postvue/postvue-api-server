package com.postvue.feelogserver.domain.snsusermessageroommembers.repository;

import java.sql.PreparedStatement;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.postvue.feelogserver.core.config.SnowflakeComponent;
import com.postvue.feelogserver.domain.snsusermessageroommembers.SnsUserMessageRoomMember;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class SnsUserMessageRoomMemberJdbcRepository {

	private final JdbcTemplate jdbcTemplate;
	private final SnowflakeComponent snowflakeComponent;

	private final String sqlDirectMsgRoomExecute = "INSERT INTO SNS_USER_MESSAGE_ROOM_MEMBERS_TB ("
		+ "sns_user_message_room_member_id, "
		+ "sns_user_message_room_id, "
		+ "source_user_id, "
		+ "target_user_id, "
		+ "msg_room_type, "
		+ "read_at) VALUES (?, ?, ?, ?, ?, ?)";

	@Transactional(propagation = Propagation.REQUIRED)
	public void insertAllDirectMessageRoom(List<SnsUserMessageRoomMember> snsUserMessageRoomMembers) {

		jdbcTemplate.batchUpdate(sqlDirectMsgRoomExecute,
			snsUserMessageRoomMembers,
			snsUserMessageRoomMembers.size(),
			(PreparedStatement ps, SnsUserMessageRoomMember snsUserMessageRoomMember) -> {
				snsUserMessageRoomMember.setId(snowflakeComponent.nextId());
				snsUserMessageRoomMember.setReadAt(LocalDateTime.now());
				ps.setLong(1, snsUserMessageRoomMember.getId());
				ps.setLong(2, snsUserMessageRoomMember.getSnsUserMessageRoom().getId());
				ps.setLong(3, snsUserMessageRoomMember.getSourceUser().getId());
				ps.setLong(4, snsUserMessageRoomMember.getTargetUser().getId());
				ps.setObject(5, snsUserMessageRoomMember.getMsgRoomType().toString());
				ps.setObject(6, snsUserMessageRoomMember.getReadAt());
			});
	}
}
