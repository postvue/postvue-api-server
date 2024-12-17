package com.postvue.feelogserver.domain.snsusermessageroommembers.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.postvue.feelogserver.domain.snsusermessageroommembers.SnsUserMessageRoomMember;
import com.postvue.feelogserver.domain.snsusermessageroommembers.dao.MsgRoomMemberDao;

import org.springframework.data.repository.query.Param;

@Repository
public interface SnsUserMessageRoomMemberRepository
	extends JpaRepository<SnsUserMessageRoomMember, Long>, JpaSpecificationExecutor<SnsUserMessageRoomMember> {


	Optional<SnsUserMessageRoomMember> findBySourceUser_IdAndTargetUser_Id(Long sourceId, Long targetId);

	@Query("SELECT SNS_UMDR FROM SnsUserMessageRoomMember AS SNS_UMDR "
		+ "WHERE SNS_UMDR.id < :cursorId "
		+ "AND SNS_UMDR.sourceUser.id = :snsUserId "
		+ "AND SNS_UMDR.isBlocked IS TRUE "
		+ "ORDER BY SNS_UMDR.createdAt DESC")
	List<SnsUserMessageRoomMember> findAllBlockedDirectRoom(
		@Param("snsUserId") Long snsUserId,
		@Param("cursorId") Long cursorId,
		Pageable pageable);

	@Query("SELECT SNS_UMDR FROM SnsUserMessageRoomMember AS SNS_UMDR "
		+ "WHERE SNS_UMDR.id < :cursorId "
		+ "AND SNS_UMDR.sourceUser.id = :snsUserId "
		+ "AND SNS_UMDR.isHidden IS TRUE "
		+ "ORDER BY SNS_UMDR.createdAt DESC ")
	List<SnsUserMessageRoomMember> findAllHiddenDirectRoom(
		@Param("snsUserId") Long snsUserId,
		@Param("cursorId") Long cursorId,
		Pageable pageable);

	@Query(
		"SELECT new com.postvue.feelogserver.domain.snsusermessageroommembers.dao.MsgRoomMemberDao("
			+ "SNS_UMRM.sourceUser.id, SNS_UMRM.msgRoomType, SNS_UMRM.targetUser.id) "
			+ "FROM SnsUserMessageRoomMember SNS_UMRM INNER JOIN SnsUserMessage SNS_M "
			+ "ON SNS_UMRM.snsUserMessageRoom = SNS_M.snsUserMessageRoom "
			+ "WHERE SNS_UMRM.snsUserMessageRoom.id = :messageRoomId")
	List<MsgRoomMemberDao> findAllTargetByRoomId(
		@Param("messageRoomId") Long messageRoomId);

}
