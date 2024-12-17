package com.postvue.feelogserver.app.messages.controller;

import java.util.Arrays;
import java.util.List;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.postvue.feelogserver.app.messages.dto.rsp.GetBlockUserListRsp;
import com.postvue.feelogserver.app.messages.dto.rsp.GetHiddenUserListRsp;
import com.postvue.feelogserver.app.messages.dto.rsp.GetMsgDirectConversationsRsp;
import com.postvue.feelogserver.app.messages.dto.rsp.GetMsgInboxMessage;
import com.postvue.feelogserver.app.messages.dto.rsp.MessageBlockUserRsp;
import com.postvue.feelogserver.app.messages.dto.rsp.MessageHiddenUserRsp;
import com.postvue.feelogserver.app.messages.service.MessagesService;
import com.postvue.feelogserver.core.security.CustomUserDetails;
import com.postvue.feelogserver.global.constant.PageConfigConst;
import com.postvue.feelogserver.global.http.response.serverresponse.ServerGetOkRsp;

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

}
