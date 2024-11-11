package com.postvue.feelogserver.domain.snsusermessagerooms.repository;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.postvue.feelogserver.core.config.SnowflakeComponent;
import com.postvue.feelogserver.domain.snsusermessagerooms.SnsUserMessageRoom;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class SnsUserMessageRoomJdbcRepository {

	private final JdbcTemplate jdbcTemplate;
	private final SnowflakeComponent snowflakeComponent;

	final String sql = "INSERT INTO sns_user_message_rooms_tb ("
		+ "sns_user_message_room_id, "
		+ "created_at, "
		+ "last_updated_at, "
		+ "msg_room_type) "
		+ "VALUES (?, ?, ?, ?)";

	@Transactional(propagation = Propagation.REQUIRED)
	public SnsUserMessageRoom insertMessageRoom(SnsUserMessageRoom snsUserMessageRoom) {

		snsUserMessageRoom.setId(snowflakeComponent.nextId());

		LocalDateTime localDateTime = LocalDateTime.now();
		snsUserMessageRoom.setCreatedAt(localDateTime);
		snsUserMessageRoom.setLastUpdatedAt(localDateTime);

		jdbcTemplate.update(connection -> {
			PreparedStatement ps = connection.prepareStatement(sql);
			createUserMessageRoomStatement(ps, snsUserMessageRoom);
			return ps;
		});

		return snsUserMessageRoom;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public List<SnsUserMessageRoom> saveAll(List<SnsUserMessageRoom> snsUserMessageRooms) {
		jdbcTemplate.batchUpdate(sql,
			snsUserMessageRooms,
			snsUserMessageRooms.size(),
			(PreparedStatement ps, SnsUserMessageRoom snsUserMessageRoom) -> {
				snsUserMessageRoom.setId(snowflakeComponent.nextId());
				createUserMessageRoomStatement(ps, snsUserMessageRoom);
			});

		return snsUserMessageRooms;
	}

	private void createUserMessageRoomStatement(PreparedStatement ps, SnsUserMessageRoom snsUserMessageRoom) throws
		SQLException {
		ps.setLong(1, snsUserMessageRoom.getId());
		ps.setObject(2, snsUserMessageRoom.getCreatedAt());
		ps.setObject(3, snsUserMessageRoom.getLastUpdatedAt());
		ps.setObject(4, snsUserMessageRoom.getMsgRoomType().toString());
	}
}
