package com.postvue.feelogserver.app.messages.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import com.postvue.feelogserver.app.messages.dto.rsp.GetBlockHiddenUserRsp;
import com.postvue.feelogserver.app.messages.dto.rsp.GetBlockUserListRsp;
import com.postvue.feelogserver.app.messages.dto.rsp.GetHiddenUserListRsp;
import com.postvue.feelogserver.app.messages.dto.rsp.GetMsgDirectConversationsRsp;
import com.postvue.feelogserver.app.messages.dto.rsp.GetMsgInboxMessage;
import com.postvue.feelogserver.app.messages.dto.rsp.MessageBlockUserRsp;
import com.postvue.feelogserver.app.messages.dto.rsp.MessageHiddenUserRsp;
import com.postvue.feelogserver.app.messages.dto.rsp.MsgDirectConversationRsp;
import com.postvue.feelogserver.app.messages.dto.session.ws.pub.MsgDmConversationCreatePub;
import com.postvue.feelogserver.app.messages.dto.session.ws.sub.MsgConversationSub;
import com.postvue.feelogserver.domain.snsusermessageroommembers.SnsUserMessageRoomMember;
import com.postvue.feelogserver.domain.snsusermessageroommembers.dao.MsgRoomMemberDao;
import com.postvue.feelogserver.domain.snsusermessageroommembers.repository.SnsUserMessageRoomMemberJdbcRepository;
import com.postvue.feelogserver.domain.snsusermessageroommembers.repository.SnsUserMessageRoomMemberRepository;
import com.postvue.feelogserver.domain.snsusermessagerooms.SnsUserMessageRoom;
import com.postvue.feelogserver.domain.snsusermessagerooms.repository.SnsUserMessageRoomJdbcRepository;
import com.postvue.feelogserver.domain.snsusermessagerooms.vo.MsgRoomType;
import com.postvue.feelogserver.domain.snsusermessages.SnsUserMessage;
import com.postvue.feelogserver.domain.snsusermessages.dao.MsgConversationDao;
import com.postvue.feelogserver.domain.snsusermessages.dao.MsgInboxMessageDao;
import com.postvue.feelogserver.domain.snsusermessages.repository.SnsUserMessageRepository;
import com.postvue.feelogserver.domain.snsusermessages.vo.SnsMsgType;
import com.postvue.feelogserver.domain.snsusers.SnsUser;
import com.postvue.feelogserver.domain.snsusers.repository.SnsUserRepository;
import com.postvue.feelogserver.global.constant.PageConfigConst;
import com.postvue.feelogserver.global.exception.BadRequestErrorException;
import com.postvue.feelogserver.global.exception.UnauthorizedErrorException;
import com.postvue.feelogserver.global.util.generator.UrlUtils;
import com.postvue.feelogserver.global.util.response.ObjectConvertRspUtil;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MessagesService {
	private final SnsUserMessageRepository snsUserMessageRepository;
	private final SnsUserRepository snsUserRepository;

	private final SimpMessagingTemplate messageTemplate;

	private final SnsUserMessageRoomJdbcRepository snsUserMessageRoomJdbcRepository;
	private final SnsUserMessageRoomMemberRepository snsUserMessageRoomMemberRepository;
	private final SnsUserMessageRoomMemberJdbcRepository snsUserMessageRoomMemberJdbcRepository;

	@Transactional
	public List<GetMsgInboxMessage> findMsgInboxMessage(Long userId, Integer page) {
		List<MsgInboxMessageDao> msgInboxMessageDaoList = snsUserMessageRepository.selectMsgInboxMessages(
			userId, PageConfigConst.MSG_INBOX_MESSAGE_PAGE_NUM, page);

		return msgInboxMessageDaoList.stream()
			.map(msgInboxMessageDao -> {

				SnsUser targetUser = snsUserRepository.findById(msgInboxMessageDao.getTargetUserId())
					.orElseThrow();
				return GetMsgInboxMessage.builder()
					.msgRoomId(msgInboxMessageDao.getMsgRoomId().toString())
					.targetUserId(msgInboxMessageDao.getTargetUserId().toString())
					.username(targetUser.getUsername())
					.msgId(msgInboxMessageDao.getLatestMsgId().toString())
					.msgType(msgInboxMessageDao.getLatestMsgType())
					.msgContent(msgInboxMessageDao.getLatestMsgContent())
					.sendAt(msgInboxMessageDao.getPostedAt())
					.profilePath(targetUser.getProfilePath())
					.unreadCount(msgInboxMessageDao.getUnreadCount())
					.build();
			})
			.toList();
	}

	// DM: 메시지 방
	@Transactional
	public GetMsgDirectConversationsRsp findDirestMsgConversationList(Long myUserId, Long targetUserId, Long cursorId) {
		Pageable pageable = PageRequest.of(PageConfigConst.PAGE_INIT_NUM, PageConfigConst.MSG_CONVERSATION_PAGE_NUM);

		List<MsgConversationDao> msgConversationDaoList = snsUserMessageRepository.findDirectMsgConversationList(
			myUserId, targetUserId, cursorId, pageable);

		List<MsgDirectConversationRsp> msgConversationRspList = msgConversationDaoList
			.stream()
			.map(msgConversationDao -> MsgDirectConversationRsp.builder()
				.msgId(msgConversationDao.getSnsUserMessageId().toString())
				.msgRoomId(msgConversationDao.getSnsUserMessageRoomId().toString())
				.msgType(msgConversationDao.getMsgType().toString())
				.msgContent(msgConversationDao.getMsgContent())
				.sendAt(msgConversationDao.getSendAt())
				.sourceUserId(msgConversationDao.getIsOtherMsg() ? targetUserId.toString() : myUserId.toString())
				.isOtherMsg(msgConversationDao.getIsOtherMsg())
				.build())
			.toList();

		// private Boolean hasMsgReaction;
		// private String msgReactionType;

		if (msgConversationRspList.isEmpty()) {
			return new GetMsgDirectConversationsRsp(PageConfigConst.ZERO_ID, new ArrayList<>());
		} else {
			MsgConversationDao msgConversationDao = msgConversationDaoList.get(
				msgConversationDaoList.size() - 1);
			Boolean isBlocked = msgConversationDao.getIsBlocked();
			Boolean isHidden = msgConversationDao.getIsHidden();

			if (isHidden || isBlocked) {
				throw new BadRequestErrorException("차단 또는 숨김 처리 되어서 메시지를 볼 권한이 없습니다.");
			}

			// @REFER: 수정 필요
			// @REFER: 일단 Jpa로 작성함, 나중에 queryDsl로 적용 하람
			// snsUserMessageRoomMemberRepository.updateMessageRoomMemberReadAt(
			// 	myUserId, msgConversationDao.getSnsUserMessageRoomId(), LocalDateTime.now()
			// );
			snsUserMessageRoomJdbcRepository.updateReadAt(
				msgConversationDao.getSnsUserMessageRoomId(),
				myUserId,
				LocalDateTime.now()
			);

			return new GetMsgDirectConversationsRsp(
				msgConversationRspList.get(msgConversationRspList.size() - 1).getMsgId(),
				msgConversationRspList);
		}
	}

	@Transactional
	public GetBlockUserListRsp getBlockRoomRspList(Long userId, Long cursorId) {
		Pageable pageable = PageRequest.of(PageConfigConst.PAGE_INIT_NUM, PageConfigConst.MSG_BLOCK_ROOM_PAGE_NUM);
		List<SnsUserMessageRoomMember> snsUserMessageRoomMemberList =
			snsUserMessageRoomMemberRepository
				.findAllBlockedDirectRoom(
					userId, cursorId, pageable);

		List<GetBlockHiddenUserRsp> blockRoomRspList = convertToBlockHiddenUserRsp(snsUserMessageRoomMemberList);
		return ObjectConvertRspUtil.GenericObjectListRsp(blockRoomRspList, GetBlockUserListRsp::new,
			snsUserMessageRoomMemberList.isEmpty() ? "" :
				snsUserMessageRoomMemberList.get(snsUserMessageRoomMemberList.size() - 1)
					.getId()
					.toString());
	}

	@Transactional
	public GetHiddenUserListRsp getHiddenRoomRspList(Long userId, Long cursorId) {
		Pageable pageable = PageRequest.of(PageConfigConst.PAGE_INIT_NUM, PageConfigConst.MSG_BLOCK_ROOM_PAGE_NUM);
		List<SnsUserMessageRoomMember> snsUserMessageRoomMemberList =
			snsUserMessageRoomMemberRepository
				.findAllHiddenDirectRoom(
					userId, cursorId, pageable);

		List<GetBlockHiddenUserRsp> hiddenRoomRspList = convertToBlockHiddenUserRsp(snsUserMessageRoomMemberList);
		return ObjectConvertRspUtil.GenericObjectListRsp(hiddenRoomRspList, GetHiddenUserListRsp::new,
			snsUserMessageRoomMemberList.isEmpty() ? "" :
				snsUserMessageRoomMemberList.get(snsUserMessageRoomMemberList.size() - 1)
					.getId()
					.toString());
	}

	// DM 메시지 방에 대해서
	@Transactional
	public MessageBlockUserRsp putBlockedUser(Long userId, Long targetUserId, Boolean isBlocked) {
		SnsUserMessageRoomMember snsUserMessageRoomMember = snsUserMessageRoomMemberRepository
			.findBySourceUser_IdAndTargetUser_Id(userId, targetUserId).orElseThrow(
				() -> new BadRequestErrorException("해당 계정을 차단할 수 없습니다.")
			);

		snsUserMessageRoomMember.setIsBlocked(isBlocked);

		snsUserMessageRoomMemberRepository.save(snsUserMessageRoomMember);

		return MessageBlockUserRsp.builder()
			.targetUserId(snsUserMessageRoomMember.getTargetUser().getId().toString())
			.isBlocked(snsUserMessageRoomMember.getIsBlocked())
			.build();
	}

	@Transactional
	public MessageHiddenUserRsp putHiddenUser(Long userId, Long targetUserId, Boolean isHidden) {
		SnsUserMessageRoomMember snsUserMessageRoomMember = snsUserMessageRoomMemberRepository
			.findBySourceUser_IdAndTargetUser_Id(userId, targetUserId)
			.orElseThrow(
				() -> new BadRequestErrorException("해당 계정을 차단할 수 없습니다.")
			);

		snsUserMessageRoomMember.setIsHidden(isHidden);

		return MessageHiddenUserRsp.builder()
			.targetUserId(snsUserMessageRoomMember.getTargetUser().getId().toString())
			.isHidden(snsUserMessageRoomMember.getIsHidden())
			.build();
	}

	// DM 메시지
	@Transactional
	public void createDirectNewMsgConversation(MsgDmConversationCreatePub msgDmConversationCreatePub,
		String destination,
		Long targetUserId,
		Long userId) {

		Optional<SnsUserMessageRoomMember> directRoomOpt = snsUserMessageRoomMemberRepository
			.findBySourceUser_IdAndTargetUser_Id(
				userId, targetUserId);

		SnsUserMessageRoom msgRoom;

		SnsUser myUser = SnsUser.builder().id(userId).build();
		SnsUser otherUser = SnsUser.builder().id(targetUserId).build();

		if (directRoomOpt.isPresent()) {
			msgRoom = directRoomOpt.get().getSnsUserMessageRoom();
		} else {
			SnsUserMessageRoom snsUserMessageRoom = SnsUserMessageRoom.builder()
				.msgRoomType(MsgRoomType.DIRECT_MESSAGE_ROOM_TYPE)
				.build();
			SnsUserMessageRoom newMsgRoom = snsUserMessageRoomJdbcRepository.insertMessageRoom(snsUserMessageRoom);

			// DM
			List<SnsUserMessageRoomMember> snsUserMessageRoomMemberList = Arrays.asList(
				SnsUserMessageRoomMember.builder()
					.snsUserMessageRoom(newMsgRoom)
					.sourceUser(myUser)
					.msgRoomType(MsgRoomType.DIRECT_MESSAGE_ROOM_TYPE)
					.targetUser(otherUser)
					.build(),
				SnsUserMessageRoomMember.builder()
					.snsUserMessageRoom(newMsgRoom)
					.sourceUser(otherUser)
					.msgRoomType(MsgRoomType.DIRECT_MESSAGE_ROOM_TYPE)
					.targetUser(myUser)
					.build()
			);
			snsUserMessageRoomMemberJdbcRepository.insertAllDirectMessageRoom(snsUserMessageRoomMemberList);
			msgRoom = newMsgRoom;
		}

		SnsUserMessage newUserMessage = snsUserMessageRepository.save(
			SnsUserMessage.builder()
				.msgType(SnsMsgType.valueOf(msgDmConversationCreatePub.getMsgType()))
				.msgContent(msgDmConversationCreatePub.getMsgContent())
				.sourceUser(myUser)
				.snsUserMessageRoom(msgRoom)
				.build());

		// 보낸 사람, 받는 사람 둘 다 보내기
		for (Long sentUser : Arrays.asList(targetUserId, userId)) {
			messageTemplate.convertAndSend(UrlUtils.getWebsocketTargetUri(destination, sentUser),
				MsgConversationSub.builder()
					.msgRoomId(msgRoom.getId().toString())
					.targetUserId(userId == sentUser ? targetUserId.toString() : userId.toString())
					.isGroupedMsg(false)
					.msgType(newUserMessage.getMsgType().toString())
					.msgContent(newUserMessage.getMsgContent())
					.msgId(newUserMessage.getId().toString())
					.sendAt(newUserMessage.getCreatedAt())
					.sourceUserId(userId.toString())
					.build());
		}

	}

	@Transactional
	public void deleteMsgConversation(String destination, Long msgConversationId,
		Long userId) {

		SnsUserMessage snsUserMessage = snsUserMessageRepository.findById(msgConversationId).orElseThrow();

		List<MsgRoomMemberDao> msgRoomMemberDaoList = snsUserMessageRoomMemberRepository.findAllTargetByRoomId(
			snsUserMessage.getSnsUserMessageRoom().getId());

		if (Objects.equals(snsUserMessage.getSourceUser().getId(), userId)) {
			snsUserMessageRepository.delete(snsUserMessage);
		} else {
			throw new UnauthorizedErrorException("권한이 없습니다.");
		}

		msgRoomMemberDaoList.forEach((msgRoomMemberDao ->
				messageTemplate.convertAndSend(
					UrlUtils.getWebsocketTargetUri(destination, msgRoomMemberDao.getSourceUserId()),
					MsgConversationSub.builder()
						.msgRoomId(snsUserMessage.getSnsUserMessageRoom().getId().toString())
						.isGroupedMsg(msgRoomMemberDao.getMsgRoomType() == MsgRoomType.GROUP_MESSAGE_ROOM_TYPE)
						.targetUserId(
							msgRoomMemberDao.getTargetUserId() != null ? msgRoomMemberDao.getTargetUserId().toString() :
								null)
						.isDeleted(true)
						.msgType(null)
						.msgContent(null)
						.msgId(msgConversationId.toString())
						.sendAt(null)
						.sourceUserId(msgRoomMemberDao.getSourceUserId().toString())
						.build())
			)
		);

	}

	private List<GetBlockHiddenUserRsp> convertToBlockHiddenUserRsp(
		List<SnsUserMessageRoomMember> snsUserMessageRoomMemberList) {
		return snsUserMessageRoomMemberList.stream()
			.map((snsUserMessageRoomMember -> {
				SnsUser snsUser = snsUserMessageRoomMember.getTargetUser();
				return GetBlockHiddenUserRsp.builder()
					.targetUserId(snsUser.getId().toString())
					.profilePath(snsUser.getProfilePath())
					.username(snsUser.getUsername())
					.build();
			}
			))
			.toList();
	}

}

