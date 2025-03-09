package com.postvue.feelogserver.domain.snsusermessages.repository;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.postvue.feelogserver.domain.snsusermessages.SnsUserMessage;
import com.postvue.feelogserver.domain.snsusermessages.dao.MsgConversationDao;
import com.postvue.feelogserver.domain.snsusermessages.dao.MsgInboxMessageDao;

import org.springframework.data.repository.query.Param;

@Repository
public interface SnsUserMessageRepository extends JpaRepository<SnsUserMessage, Long>, JpaSpecificationExecutor<SnsUserMessage> {
	String MSG_INBOX_FOLLOW_MESSAGES_SQL = "WITH "
		+ "MY_ROOM_LIST_TB AS (SELECT sns_user_message_room_id AS msg_room_id,target_user_id "
		+ "FROM sns_user_message_direct_rooms_tb "
		+ "WHERE source_user_id = :snsUserId AND is_hidden IS FALSE AND is_blocked IS FALSE), "
		+ " "
		+ "LATEST_MESSAGES AS(SELECT * "
		+ "FROM SNS_USER_MESSAGES_TB "
		+ "WHERE (sns_user_message_room_id, created_at) IN ( "
		+ "SELECT "
		+ "sns_user_message_room_id, MAX(created_at) "
		+ "FROM SNS_USER_MESSAGES_TB "
		+ "WHERE sns_user_message_room_id in (SELECT msg_room_id FROM MY_ROOM_LIST_TB)"
		+ "GROUP BY sns_user_message_room_id)),"
		+ " "
		+ "FIRST_NOT_READ_TB AS (SELECT "
		+ "sns_user_message_room_id, MIN(created_at) AS first_false_date "
		+ "FROM SNS_USER_MESSAGES_TB "
		+ "WHERE is_read = false AND sns_user_message_room_id in (SELECT msg_room_id FROM MY_ROOM_LIST_TB)"
		+ "GROUP BY sns_user_message_room_id),"
		+ " "
		+ "UNREAD_COUNT_TB AS (SELECT "
		+ "SNS_UM.sns_user_message_room_id, COUNT(*) AS unread_count "
		+ "FROM sns_user_messages_tb AS SNS_UM "
		+ "JOIN FIRST_NOT_READ_TB AS FIRST_NRT "
		+ "ON SNS_UM.sns_user_message_room_id = FIRST_NRT.sns_user_message_room_id "
		+ "AND SNS_UM.created_at >= FIRST_NRT.first_false_date "
		+ "WHERE SNS_UM.is_read = false AND NOT SNS_UM.source_user_id = :snsUserId "
		+ "GROUP BY SNS_UM.sns_user_message_room_id),"
		+ " "
		+ "UNREAD_COUNT_TB_BY_NOT_TARGET_MSG AS ( "
		+ "SELECT "
		+ "LATEST_M.sns_user_message_room_id, "
		+ "0 AS unread_count "
		+ "FROM LATEST_MESSAGES AS LATEST_M "
		+ "WHERE "
		+ "LATEST_M.source_user_id = :snsUserId AND "
		+ "LATEST_M.sns_user_message_room_id NOT IN (SELECT sns_user_message_room_id FROM UNREAD_COUNT_TB)),"
		+ " "
		+ "MSG_INBOX_TB AS ((SELECT * FROM UNREAD_COUNT_TB_BY_NOT_TARGET_MSG) UNION (SELECT * FROM UNREAD_COUNT_TB)),"
		+ " "
		+ "ROOM_LIST_WITH_RECENT_MSG_TB AS ( "
		+ "SELECT "
		+ "MSG_IT.sns_user_message_room_id AS msg_room_id, "
		+ "MSG_IT.unread_count, "
		+ "LATEST_MESSAGES.sns_user_message_id AS latest_msg_id, "
		+ "LATEST_MESSAGES.msg_type AS latest_msg_type, "
		+ "LATEST_MESSAGES.msg_content AS latest_msg_content, "
		+ "LATEST_MESSAGES.created_at AS posted_at "
		+ "FROM LATEST_MESSAGES "
		+ "INNER JOIN MSG_INBOX_TB AS MSG_IT "
		+ "ON LATEST_MESSAGES.sns_user_message_room_id = MSG_IT.sns_user_message_room_id) "
		+ " "
		+ "SELECT ROOM_LWRM.*, MY_ROOM_LIST_TB.target_user_id "
		+ "FROM ROOM_LIST_WITH_RECENT_MSG_TB AS ROOM_LWRM "
		+ "INNER JOIN MY_ROOM_LIST_TB ON ROOM_LWRM.msg_room_id = MY_ROOM_LIST_TB.msg_room_id "
		+ "WHERE ROOM_LWRM.latest_msg_id < :cursorId "
		+ "ORDER BY posted_at DESC LIMIT :pageSize";

	String MSG_INBOX_MESSAGES_QUERY = "WITH "
		+ "MY_ROOM_LIST_TB AS (SELECT sns_user_message_room_id AS msg_room_id, target_user_id, read_at "
		+ "FROM sns_user_message_room_members_tb "
		+ "WHERE source_user_id = :snsUserId AND is_hidden IS FALSE AND is_blocked IS FALSE), "
		+ " "
		+ "LATEST_MESSAGES AS (SELECT * "
		+ "FROM SNS_USER_MESSAGES_TB  "
		+ "WHERE "
		+ "(sns_user_message_room_id, created_at) "
		+ "IN (SELECT "
		+ "sns_user_message_room_id, MAX(created_at) "
		+ "FROM SNS_USER_MESSAGES_TB "
		+ "WHERE sns_user_message_room_id in "
		+ "(SELECT msg_room_id FROM MY_ROOM_LIST_TB) "
		+ "GROUP BY sns_user_message_room_id) "
		+ " "
		+ "), "
		+ " "
		+ "UNREAD_COUNT_READ_AT AS  "
		+ "(SELECT SNS_UM.sns_user_message_room_id AS msg_room_id,  "
		+ "SUM(CASE WHEN SNS_UM.sns_user_message_room_id IS NULL THEN 0 ELSE 1 END) AS unread_count  "
		+ "FROM sns_user_messages_tb AS SNS_UM  "
		+ "INNER JOIN MY_ROOM_LIST_TB AS MY_RL ON SNS_UM.sns_user_message_room_id = MY_RL.msg_room_id  "
		+ "WHERE SNS_UM.source_user_id != :snsUserId "
		+ "AND SNS_UM.created_at >= MY_RL.read_at  "
		+ "GROUP BY SNS_UM.sns_user_message_room_id  "
		+ "ORDER BY MAX(SNS_UM.created_at) DESC  "
		+ "),  "
		+ " "
		+ "UNREAD_COUNT AS ( "
		+ "SELECT * "
		+ "FROM UNREAD_COUNT_READ_AT "
		+ "WHERE EXISTS (SELECT 1 FROM UNREAD_COUNT_READ_AT) "
		+ "UNION "
		+ "SELECT  "
		+ "msg_room_id, "
		+ "0 AS unread_count  "
		+ "FROM MY_ROOM_LIST_TB "
		+ "WHERE NOT EXISTS (SELECT 1 FROM UNREAD_COUNT_READ_AT) "
		+ "),"
		+ " "
		+ "UNREAD_COUNT_BY_NOT_TARGET_MSG AS ( "
		+ "SELECT "
		+ "LATEST_M.sns_user_message_room_id AS msg_room_id, "
		+ "0 AS unread_count "
		+ "FROM LATEST_MESSAGES AS LATEST_M "
		+ "WHERE "
		+ "LATEST_M.source_user_id = :snsUserId AND "
		+ "LATEST_M.sns_user_message_room_id NOT IN (SELECT msg_room_id FROM UNREAD_COUNT)), "
		+ " "
		+ "MSG_INBOX AS "
		+ "(SELECT * FROM UNREAD_COUNT UNION SELECT * FROM UNREAD_COUNT_BY_NOT_TARGET_MSG), "
		+ " "
		+ "ROOM_LIST_WITH_RECENT_MSG_TB AS ( "
		+ "SELECT "
		+ "MSG_IT.msg_room_id AS msg_room_id, "
		+ "MSG_IT.unread_count, "
		+ "LATEST_MESSAGES.sns_user_message_id AS latest_msg_id, "
		+ "LATEST_MESSAGES.msg_type AS latest_msg_type, "
		+ "LATEST_MESSAGES.msg_content AS latest_msg_content, "
		+ "LATEST_MESSAGES.created_at AS posted_at "
		+ "FROM LATEST_MESSAGES "
		+ "INNER JOIN MSG_INBOX AS MSG_IT "
		+ "ON LATEST_MESSAGES.sns_user_message_room_id = MSG_IT.msg_room_id) "
		+ " "
		+ "SELECT ROOM_LWRM.*, MY_ROOM_LIST_TB.target_user_id "
		+ "FROM ROOM_LIST_WITH_RECENT_MSG_TB AS ROOM_LWRM "
		+ "INNER JOIN MY_ROOM_LIST_TB ON ROOM_LWRM.msg_room_id = MY_ROOM_LIST_TB.msg_room_id "
		+ "ORDER BY ROOM_LWRM.posted_at DESC OFFSET :page LIMIT :pageSize";

	@Query(value = MSG_INBOX_MESSAGES_QUERY, nativeQuery = true)
	List<MsgInboxMessageDao> selectMsgInboxMessages(
		@Param("snsUserId") Long snsUserId,
		@Param("pageSize") Integer pageSize,
		@Param("page") Integer page);

	// DM 메시지 방에 대해서
	@Query(
		"SELECT new com.postvue.feelogserver.domain.snsusermessages.dao.MsgConversationDao("
			+ "snsUserMessage.id, "
			+ "CASE WHEN snsUserMessage.sourceUser.id = :myUserId THEN FALSE ELSE TRUE END, "
			+ "snsUserMessage.msgType, snsUserMessage.msgContent, "
			+ "snsUserMessage.createdAt, SNS_URM.isHidden, SNS_URM.isBlocked, "
			+ "snsUserMessage.snsUserMessageRoom.id) "
			+ "from SnsUserMessage snsUserMessage "
			+ "INNER JOIN FETCH SnsUserMessageRoomMember AS SNS_URM "
			+ "ON snsUserMessage.snsUserMessageRoom = SNS_URM.snsUserMessageRoom "
			+ "WHERE snsUserMessage.id < :cursorId "
			+ "AND SNS_URM.snsUserMessageRoom IN "
			+ "(SELECT SNS_UMR.snsUserMessageRoom "
			+ "FROM SnsUserMessageRoomMember AS SNS_UMR "
			+ "WHERE SNS_UMR.sourceUser.id = :myUserId "
			+ "AND SNS_URM.targetUser.id = :targetUserId "
			+ "AND SNS_URM.msgRoomType = "
			+ "com.postvue.feelogserver.domain.snsusermessagerooms.vo.MsgRoomType.DIRECT_MESSAGE_ROOM_TYPE)"
			+ "AND snsUserMessage.deletedAt IS NULL "
			+ "ORDER BY snsUserMessage.createdAt DESC")
	List<MsgConversationDao> findDirectMsgConversationList(
		@Param("myUserId") Long myUserId,
		@Param("targetUserId") Long targetUserId,
		@Param("cursorId") Long cursorId,
		Pageable pageable);

	// @Query(value = "UPDATE FROM SnsUserMessage SNS_UM SET SNS_UM.isRead = true "
	// 	+ "WHERE SNS_UM.snsUserMessageRoom.snsUserMessageRoomId = :messageRoomId AND SNS_UM. SNS_UM IS FALSE ")
	// void updateMessageIsRead(
	// 	@Param("messageRoomID") Long messageRoomId,
	// 	@Param("snsUserId") Long snsUserId
	// );
}
