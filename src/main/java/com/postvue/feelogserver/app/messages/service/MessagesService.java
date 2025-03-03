package com.postvue.feelogserver.app.messages.service;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

import org.apache.commons.io.FilenameUtils;
import org.jsoup.Jsoup;
import org.jsoup.UnsupportedMimeTypeException;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.postvue.feelogserver.app.cloud.service.R2CloudService;
import com.postvue.feelogserver.app.messages.dto.req.DirectMsgReq;
import com.postvue.feelogserver.app.messages.dto.rsp.GetBlockHiddenUserRsp;
import com.postvue.feelogserver.app.messages.dto.rsp.GetBlockUserListRsp;
import com.postvue.feelogserver.app.messages.dto.rsp.GetHiddenUserListRsp;
import com.postvue.feelogserver.app.messages.dto.rsp.GetMsgDirectConversationsRsp;
import com.postvue.feelogserver.app.messages.dto.rsp.GetMsgInboxMessage;
import com.postvue.feelogserver.app.messages.dto.rsp.MessageBlockUserRsp;
import com.postvue.feelogserver.app.messages.dto.rsp.MessageHiddenUserRsp;
import com.postvue.feelogserver.app.messages.dto.rsp.MsgDirectConversationRsp;
import com.postvue.feelogserver.app.messages.dto.rsp.MsgLinkMetaInfo;
import com.postvue.feelogserver.app.messages.dto.session.ws.sub.MsgConversationSub;
import com.postvue.feelogserver.app.messages.vo.MsgConversationSubEventType;
import com.postvue.feelogserver.app.posts.dto.rsp.get.GetPostImageDocResourceRsp;
import com.postvue.feelogserver.domain.snsblockusers.repository.SnsBlockUserRepository;
import com.postvue.feelogserver.domain.snsposts.vo.PostContentType;
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
import com.postvue.feelogserver.domain.snsusermessages.vo.MsgMediaType;
import com.postvue.feelogserver.domain.snsusermessages.vo.MsgMetaInfo;
import com.postvue.feelogserver.domain.snsusers.SnsUser;
import com.postvue.feelogserver.domain.snsusers.repository.SnsUserRepository;
import com.postvue.feelogserver.global.constant.MediaConfigConst;
import com.postvue.feelogserver.global.constant.MetaConst;
import com.postvue.feelogserver.global.constant.PageConfigConst;
import com.postvue.feelogserver.global.exception.BadRequestErrorException;
import com.postvue.feelogserver.global.exception.UnauthorizedErrorException;
import com.postvue.feelogserver.global.util.generator.UrlUtils;
import com.postvue.feelogserver.global.util.response.ObjectConvertRspUtil;
import com.postvue.feelogserver.global.util.validation.UploadFileValidationUtils;
import com.postvue.feelogserver.global.util.validation.UrlValidUtils;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MessagesService {
	private final SnsUserMessageRepository snsUserMessageRepository;
	private final SnsUserRepository snsUserRepository;

	private final SnsBlockUserRepository snsBlockUserRepository;

	private final SimpMessagingTemplate messageTemplate;

	private final SnsUserMessageRoomJdbcRepository snsUserMessageRoomJdbcRepository;
	private final SnsUserMessageRoomMemberRepository snsUserMessageRoomMemberRepository;
	private final SnsUserMessageRoomMemberJdbcRepository snsUserMessageRoomMemberJdbcRepository;
	private final R2CloudService r2CloudService;

	@Transactional
	public List<GetMsgInboxMessage> findMsgInboxMessage(Long userId, Integer page) {
		List<MsgInboxMessageDao> msgInboxMessageDaoList = snsUserMessageRepository.selectMsgInboxMessages(
			userId, PageConfigConst.MSG_INBOX_MESSAGE_PAGE_NUM, page);

		return msgInboxMessageDaoList.stream()
			.map(msgInboxMessageDao -> {

				SnsUser targetUser = snsUserRepository.findById(msgInboxMessageDao.getTargetUserId())
					.orElseThrow(
						() -> new BadRequestErrorException("해당 계정은 없습니다.")
					);

				MsgMetaInfo msgMetaInfo = msgInboxMessageDao.getStringToMetaInfo();
				return GetMsgInboxMessage.builder()
					.msgRoomId(msgInboxMessageDao.getMsgRoomId().toString())
					.targetUserId(msgInboxMessageDao.getTargetUserId().toString())
					.username(targetUser.getUsername())
					.nickname(targetUser.getNickname())
					.msgId(msgInboxMessageDao.getLatestMsgId().toString())
					.msgTextContent(msgInboxMessageDao.getLatestMsgTextContent())
					.hasMsgMedia(msgInboxMessageDao.getLatestMsgMediaType() != null && msgInboxMessageDao.getLatestMsgMediaContent() != null)
					.msgMediaType(msgInboxMessageDao.getLatestMsgMediaType())
					.msgMediaContent(msgInboxMessageDao.getLatestMsgMediaContent())
					.msgLinkMetaInfo(msgMetaInfo != null ?
						new MsgLinkMetaInfo(msgMetaInfo.getOgTitle(), msgMetaInfo.getOgImage(), msgMetaInfo.getOgDescription())
						: null)
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
			.map(msgConversationDao -> {
				Boolean hasMsgMedia = msgConversationDao.getMsgMediaContent() != null && msgConversationDao.getMsgMediaType() != null;
				MsgMetaInfo msgMetaInfo = msgConversationDao.getMsgMetaInfo();
				return MsgDirectConversationRsp.builder()
				.msgId(msgConversationDao.getSnsUserMessageId().toString())
				.msgRoomId(msgConversationDao.getSnsUserMessageRoomId().toString())
				.msgTextContent(msgConversationDao.getMsgTextContent())
				.hasMsgMedia(hasMsgMedia)
				.msgMediaType(hasMsgMedia ? msgConversationDao.getMsgMediaType().toString() : null)
				.msgMediaContent(msgConversationDao.getMsgMediaContent())
				.msgLinkMetaInfo(msgMetaInfo != null ?
					new MsgLinkMetaInfo(msgMetaInfo.getOgTitle(),msgMetaInfo.getOgImage(),msgMetaInfo.getOgDescription())
					: null)
				.sendAt(msgConversationDao.getSendAt())
				.sourceUserId(msgConversationDao.getIsOtherMsg() ? targetUserId.toString() : myUserId.toString())
				.isOtherMsg(msgConversationDao.getIsOtherMsg())
				.build();
			})
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

	// DM 메시지, Http
	@Transactional
	public void createDirectNewMsgConversation(
		DirectMsgReq directMsgReq,
		MultipartFile file,
		String destination,
		Long targetUserId,
		Long userId) {

		boolean isPrivate = snsBlockUserRepository.findIsBlockUser(userId, targetUserId);

		if (isPrivate){
			throw new BadRequestErrorException("비공개 계정하고 대화 할 수 없습니다.");
		}

		try {
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

			Map<String, String> metaInfo = UrlValidUtils.isValidURL(directMsgReq.getMsgTextContent()) ? getMetaDataByHtmlParser(directMsgReq.getMsgTextContent()) : null;


			SnsUserMessage snsUserMessage = SnsUserMessage.builder()
				.msgTextContent(directMsgReq.getMsgTextContent())
				.sourceUser(myUser)
				.snsUserMessageRoom(msgRoom)
				.msgMetaInfo(metaInfo != null
					? new MsgMetaInfo(
						metaInfo.get(MetaConst.OG_IMAGE),
						metaInfo.get(MetaConst.OG_TITLE),
						metaInfo.get(MetaConst.OG_DESCRIPTION)
					)
					: null)
				.build();

			SnsUserMessage newUserMessage = saveMsgWithMedia(file, snsUserMessage);

			// 보낸 사람, 받는 사람 둘 다 보내기
			for (Long sentUser : Arrays.asList(targetUserId, userId)) {
				messageTemplate.convertAndSend(UrlUtils.getWebsocketTargetUri(destination, sentUser),
					MsgConversationSub.builder()
						.msgRoomId(msgRoom.getId().toString())
						.targetUserId(Objects.equals(userId, sentUser) ? targetUserId.toString() : userId.toString())
						.isGroupedMsg(false)
						.eventType(MsgConversationSubEventType.CREATE.toString())
						.msgTextContent(newUserMessage.getMsgTextContent())
						.hasMsgMedia(newUserMessage.getMsgMediaContent() != null)
						.msgMediaType(newUserMessage.getMsgMediaType() != null ? newUserMessage.getMsgMediaType().toString() : null)
						.msgMediaContent(newUserMessage.getMsgMediaContent())
						.msgLinkMetaInfo(newUserMessage.getMsgMetaInfo() != null
							? new MsgLinkMetaInfo(
								newUserMessage.getMsgMetaInfo().getOgTitle(),
								newUserMessage.getMsgMetaInfo().getOgImage(),
								newUserMessage.getMsgMetaInfo().getOgDescription())
							: null)
						.msgId(newUserMessage.getId().toString())
						.sendAt(newUserMessage.getCreatedAt())
						.sourceUserId(userId.toString())
						.build());
			}
		}
		catch (Exception e){
			// messageTemplate.convertAndSend(UrlUtils.getWebsocketTargetUri(destination, userId),
			// 	MsgConversationSub.builder()
			// 		.targetUserId(targetUserId.toString())
			// 		.eventType(MsgConversationSubEventType.ERROR.toString())
			// 		.errorMsg("The request could not be processed due to a system error. 500")
			// 		.sourceUserId(userId.toString())
			// 		.build());
			throw new BadRequestErrorException(e.getMessage());
		}
	}

	// DM 메시지, Websocket
	// @Transactional
	// public void createDirectNewMsgConversationByWs(
	// 	MsgDmConversationCreatePub msgDmConversationCreatePub,
	// 	MultipartFile file,
	// 	String destination,
	// 	Long targetUserId,
	// 	Long userId) {
	//
	// 	boolean isPrivate = snsBlockUserRepository.findIsBlockUser(userId, targetUserId);
	//
	// 	if (isPrivate){
	// 		messageTemplate.convertAndSend(UrlUtils.getWebsocketTargetUri(destination, userId),
	// 			MsgConversationSub.builder()
	// 				.targetUserId(targetUserId.toString())
	// 				.isError(true)
	// 				.errorMsg("비공개 계정하고 대화 할 수 없습니다.")
	// 				.sourceUserId(userId.toString())
	// 				.build());
	// 		throw new BadRequestErrorException("비공개 계정하고 대화 할 수 없습니다.");
	// 	}
	//
	// 	try {
	// 		Optional<SnsUserMessageRoomMember> directRoomOpt = snsUserMessageRoomMemberRepository
	// 			.findBySourceUser_IdAndTargetUser_Id(
	// 				userId, targetUserId);
	//
	// 		SnsUserMessageRoom msgRoom;
	//
	// 		SnsUser myUser = SnsUser.builder().id(userId).build();
	// 		SnsUser otherUser = SnsUser.builder().id(targetUserId).build();
	//
	// 		if (directRoomOpt.isPresent()) {
	// 			msgRoom = directRoomOpt.get().getSnsUserMessageRoom();
	// 		} else {
	// 			SnsUserMessageRoom snsUserMessageRoom = SnsUserMessageRoom.builder()
	// 				.msgRoomType(MsgRoomType.DIRECT_MESSAGE_ROOM_TYPE)
	// 				.build();
	// 			SnsUserMessageRoom newMsgRoom = snsUserMessageRoomJdbcRepository.insertMessageRoom(snsUserMessageRoom);
	//
	// 			// DM
	// 			List<SnsUserMessageRoomMember> snsUserMessageRoomMemberList = Arrays.asList(
	// 				SnsUserMessageRoomMember.builder()
	// 					.snsUserMessageRoom(newMsgRoom)
	// 					.sourceUser(myUser)
	// 					.msgRoomType(MsgRoomType.DIRECT_MESSAGE_ROOM_TYPE)
	// 					.targetUser(otherUser)
	// 					.build(),
	// 				SnsUserMessageRoomMember.builder()
	// 					.snsUserMessageRoom(newMsgRoom)
	// 					.sourceUser(otherUser)
	// 					.msgRoomType(MsgRoomType.DIRECT_MESSAGE_ROOM_TYPE)
	// 					.targetUser(myUser)
	// 					.build()
	// 			);
	// 			snsUserMessageRoomMemberJdbcRepository.insertAllDirectMessageRoom(snsUserMessageRoomMemberList);
	// 			msgRoom = newMsgRoom;
	// 		}
	//
	// 		SnsUserMessage newUserMessage = snsUserMessageRepository.save(
	// 			SnsUserMessage.builder()
	// 				.msgType(SnsMsgType.valueOf(msgDmConversationCreatePub.getMsgType()))
	// 				.msgContent(msgDmConversationCreatePub.getMsgContent())
	// 				.sourceUser(myUser)
	// 				.snsUserMessageRoom(msgRoom)
	// 				.build());
	//
	// 		// 보낸 사람, 받는 사람 둘 다 보내기
	// 		for (Long sentUser : Arrays.asList(targetUserId, userId)) {
	// 			messageTemplate.convertAndSend(UrlUtils.getWebsocketTargetUri(destination, sentUser),
	// 				MsgConversationSub.builder()
	// 					.msgRoomId(msgRoom.getId().toString())
	// 					.targetUserId(userId == sentUser ? targetUserId.toString() : userId.toString())
	// 					.isGroupedMsg(false)
	// 					.msgType(newUserMessage.getMsgType().toString())
	// 					.msgContent(newUserMessage.getMsgContent())
	// 					.msgId(newUserMessage.getId().toString())
	// 					.sendAt(newUserMessage.getCreatedAt())
	// 					.sourceUserId(userId.toString())
	// 					.build());
	// 		}
	// 	}
	// 	catch (Exception e){
	// 		messageTemplate.convertAndSend(UrlUtils.getWebsocketTargetUri(destination, userId),
	// 			MsgConversationSub.builder()
	// 				.targetUserId(targetUserId.toString())
	// 				.isError(true)
	// 				.errorMsg("The request could not be processed due to a system error. 500")
	// 				.sourceUserId(userId.toString())
	// 				.build());
	// 		throw new BadRequestErrorException(e.getMessage());
	// 	}
	// }

	@Transactional
	public void deleteMsgConversation(String destination, Long msgConversationId,
		Long userId) {
		SnsUserMessage snsUserMessage = snsUserMessageRepository.findById(msgConversationId).orElseThrow(
			() -> new BadRequestErrorException("해당 메시지는 없습니다.")
		);

		List<MsgRoomMemberDao> msgRoomMemberDaoList = snsUserMessageRoomMemberRepository.findAllTargetByRoomId(
			snsUserMessage.getSnsUserMessageRoom().getId());

		if (Objects.equals(snsUserMessage.getSourceUser().getId(), userId)) {
			snsUserMessageRepository.delete(snsUserMessage);
		} else {
			throw new UnauthorizedErrorException("삭제 권한이 없습니다.");
		}

		msgRoomMemberDaoList.forEach((msgRoomMemberDao ->
			messageTemplate.convertAndSend(
				UrlUtils.getWebsocketTargetUri(destination, msgRoomMemberDao.getSourceUserId()),
				MsgConversationSub.builder()
					.msgRoomId(snsUserMessage.getSnsUserMessageRoom().getId().toString())
					.isGroupedMsg(msgRoomMemberDao.getMsgRoomType() == MsgRoomType.GROUP_MESSAGE_ROOM_TYPE)
					.eventType(MsgConversationSubEventType.DELETE.toString())
					.targetUserId(
						msgRoomMemberDao.getTargetUserId() != null ? msgRoomMemberDao.getTargetUserId().toString() :
							null)
					.msgId(msgConversationId.toString())
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

	private SnsUserMessage saveMsgWithMedia(MultipartFile file, SnsUserMessage snsUserMessage) {
		if (file != null){
			String contentType = file.getContentType();
			String originalFilename = file.getOriginalFilename();

			// 일단 이미지만 저장할 수 잏게
			String extension = FilenameUtils.getExtension(originalFilename);

			boolean isImage = UploadFileValidationUtils.isImage(contentType);


			if (!isImage){
				throw new BadRequestErrorException("업로드 파일 유형이 아닙니다.");
			}

			snsUserMessage.setMsgMediaType(MsgMediaType.IMAGE);

			String contentUrl = r2CloudService.getPostCommentImageContentUrlByR2(UUID.randomUUID() + MediaConfigConst.IMAGE_JPEG_FORMAT);

			snsUserMessage.setMsgMediaContent(r2CloudService.getPublicContentUrlByR2(contentUrl));

			// 파일 저장
			r2CloudService.uploadImageToR2(file, contentUrl);
		}

		return snsUserMessageRepository.save(snsUserMessage);
	}

	public Map<String, String> getMetaDataByHtmlParser(String url) {
		Map<String, String> metaData = new HashMap<>();

		try {
			// 1. 웹 페이지를 가져와서 HTML을 파싱

				Document doc = Jsoup.connect(url).get();

				// Open Graph meta 태그 추출
				Element ogTitle = doc.selectFirst("meta[property=og:title]");
				Element ogImage = doc.selectFirst("meta[property=og:image]");
				Element ogDescription = doc.selectFirst("meta[property=og:description]");

				metaData.put(MetaConst.OG_TITLE, ogTitle != null ? ogTitle.attr("content") : null);
				metaData.put(MetaConst.OG_IMAGE, ogImage != null ? ogImage.attr("content") : null);
				metaData.put(MetaConst.OG_DESCRIPTION, ogDescription != null ? ogDescription.attr("content") : null);

		} catch (IOException e) {
			metaData.put("error", "Failed to fetch metadata: " + e.getMessage());
		}
		return metaData;
	}

}

