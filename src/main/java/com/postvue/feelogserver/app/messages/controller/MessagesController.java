package com.postvue.feelogserver.app.messages.controller;

import java.util.List;
import java.util.Objects;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.postvue.feelogserver.app.messages.dto.req.DirectMsgReq;
import com.postvue.feelogserver.app.messages.dto.rsp.GetBlockUserListRsp;
import com.postvue.feelogserver.app.messages.dto.rsp.GetHiddenUserListRsp;
import com.postvue.feelogserver.app.messages.dto.rsp.GetMsgDirectConversationsRsp;
import com.postvue.feelogserver.app.messages.dto.rsp.GetMsgInboxMessage;
import com.postvue.feelogserver.app.messages.dto.rsp.MessageBlockUserRsp;
import com.postvue.feelogserver.app.messages.dto.rsp.MessageHiddenUserRsp;
import com.postvue.feelogserver.app.messages.service.MessagesService;
import com.postvue.feelogserver.core.security.CustomUserDetails;
import com.postvue.feelogserver.global.constant.PageConfigConst;
import com.postvue.feelogserver.global.constant.WebSocketPathConst;
import com.postvue.feelogserver.global.exception.ForbiddenErrorException;
import com.postvue.feelogserver.global.http.response.serverresponse.ServerDeleteRsp;
import com.postvue.feelogserver.global.http.response.serverresponse.ServerGetOkRsp;
import com.postvue.feelogserver.global.http.response.serverresponse.ServerPostCreatedRsp;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/messages")
@RequiredArgsConstructor
public class MessagesController {
	private final MessagesService messagesService;

	@GetMapping("/inbox/conversations")
	public ServerGetOkRsp<List<GetMsgInboxMessage>> getMsgInboxMessageList(
		@AuthenticationPrincipal CustomUserDetails userDetails,
		@RequestParam(name = "page", defaultValue = PageConfigConst.ZERO_ID) Integer page
	) {
		Long snsUserId = (userDetails == null) ? null : Long.valueOf(userDetails.getUserId());

		if (snsUserId == null){
			return new ServerGetOkRsp<>(List.of());
		}
		else{
			return new ServerGetOkRsp<>(messagesService.findMsgInboxMessage(snsUserId, page));
		}
	}

	// DM
	@GetMapping("/conversations")
	public ServerGetOkRsp<GetMsgDirectConversationsRsp> getDirectMsgConversationList(
		@AuthenticationPrincipal CustomUserDetails userDetails,
		@RequestParam("targetUserId") Long targetUserId,
		@RequestParam(name = "cursor", defaultValue = PageConfigConst.LAST_POST_ID) Long cursorId
	) {
		Long snsUserId = (userDetails == null) ? null : Long.valueOf(userDetails.getUserId());
		if (Objects.equals(targetUserId, snsUserId)){
			throw new ForbiddenErrorException("내 자신과 대화할 수 없습니다.");
		}
		return new ServerGetOkRsp<>(messagesService.findDirestMsgConversationList(snsUserId, targetUserId, cursorId));
	}

	@GetMapping("/blocks")
	public ServerGetOkRsp<GetBlockUserListRsp> getBlockRoomList(
		@AuthenticationPrincipal CustomUserDetails userDetails,
		@RequestParam(name = "cursor", defaultValue = PageConfigConst.LAST_POST_ID) Long cursorId) {
		Long snsUserId = (userDetails == null) ? null : Long.valueOf(userDetails.getUserId());
		return new ServerGetOkRsp<>(messagesService.getBlockRoomRspList(snsUserId, cursorId));
	}

	@GetMapping("/hiddens")
	public ServerGetOkRsp<GetHiddenUserListRsp> getHiddenRoomList(
		@AuthenticationPrincipal CustomUserDetails userDetails,
		@RequestParam(name = "cursor", defaultValue = PageConfigConst.LAST_POST_ID) Long cursorId) {
		Long snsUserId = (userDetails == null) ? null : Long.valueOf(userDetails.getUserId());
		return new ServerGetOkRsp<>(messagesService.getHiddenRoomRspList(snsUserId, cursorId));
	}

	@PutMapping("/blocks/{targetUserId}")
	public ServerGetOkRsp<MessageBlockUserRsp> putBlockedUser(
		@AuthenticationPrincipal CustomUserDetails userDetails,
		@PathVariable("targetUserId") Long targetUserId) {
		Long snsUserId = (userDetails == null) ? null : Long.valueOf(userDetails.getUserId());
		return new ServerGetOkRsp<>(messagesService.putBlockedUser(snsUserId, targetUserId, true));
	}

	@PutMapping("/unblocks/{targetUserId}")
	public ServerGetOkRsp<MessageBlockUserRsp> deleteBlockedUser(
		@AuthenticationPrincipal CustomUserDetails userDetails,
		@PathVariable("targetUserId") Long targetUserId) {
		Long snsUserId = (userDetails == null) ? null : Long.valueOf(userDetails.getUserId());
		return new ServerGetOkRsp<>(messagesService.putBlockedUser(snsUserId, targetUserId, false));
	}

	@PutMapping("/hiddens/{targetUserId}")
	public ServerGetOkRsp<MessageHiddenUserRsp> putHiddenUser(
		@AuthenticationPrincipal CustomUserDetails userDetails,
		@PathVariable("targetUserId") Long targetUserId) {
		Long snsUserId = (userDetails == null) ? null : Long.valueOf(userDetails.getUserId());
		return new ServerGetOkRsp<>(messagesService.putHiddenUser(snsUserId, targetUserId, true));
	}

	@PutMapping("/unhiddens/{targetUserId}")
	public ServerGetOkRsp<MessageHiddenUserRsp> putUnhiddenUser(
		@AuthenticationPrincipal CustomUserDetails userDetails,
		@PathVariable("targetUserId") Long targetUserId) {
		Long snsUserId = (userDetails == null) ? null : Long.valueOf(userDetails.getUserId());
		return new ServerGetOkRsp<>(messagesService.putHiddenUser(snsUserId, targetUserId, false));
	}

	@PostMapping("/direct-message/{targetUserId}")
	public ServerPostCreatedRsp<Boolean> createMessage(
		@AuthenticationPrincipal CustomUserDetails userDetails,
		@PathVariable("targetUserId") Long targetUserId,
		@RequestPart("directMsgReq") DirectMsgReq directMsgReq,
		@RequestPart(value = "file", required = false) MultipartFile file) {
		String destination = WebSocketPathConst.MESSAGE_CONVERSATION_BROKER_PATH;
		Long snsUserId = (userDetails == null) ? null : Long.valueOf(userDetails.getUserId());
		messagesService.createDirectNewMsgConversation(directMsgReq, file, destination,targetUserId, snsUserId);
		return new ServerPostCreatedRsp<>(true);
	}

	@DeleteMapping("/message/{msgId}")
	public ServerDeleteRsp<Boolean> deleteMessage(
		@AuthenticationPrincipal CustomUserDetails userDetails,
		@PathVariable("msgId") Long msgId) {
		Long snsUserId = (userDetails == null) ? null : Long.valueOf(userDetails.getUserId());
		String destination = WebSocketPathConst.MESSAGE_CONVERSATION_BROKER_PATH;

		messagesService.deleteMsgConversation(destination, msgId, snsUserId);
		return new ServerDeleteRsp<>(true);
	}
}
