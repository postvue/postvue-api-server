package com.postvue.feelogserver.app.messages.controller;

import java.security.Principal;
import java.time.LocalDateTime;

import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;

import com.postvue.feelogserver.app.messages.dto.session.ws.pub.MsgConversationUpdatePub;
import com.postvue.feelogserver.app.messages.dto.session.ws.pub.MsgDmConversationCreatePub;
import com.postvue.feelogserver.app.messages.dto.session.ws.sub.MsgConversationSub;
import com.postvue.feelogserver.app.messages.service.MessagesService;
import com.postvue.feelogserver.core.security.CustomUserDetails;
import com.postvue.feelogserver.global.constant.WebSocketPathConst;
import com.postvue.feelogserver.global.exception.UnauthorizedErrorException;
import com.postvue.feelogserver.global.util.generator.UrlUtils;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class MessageWsController {
	private final MessagesService messagesService;

	private final SimpMessagingTemplate messageTemplate;

	// publish: ws://app/conversations/create/:msgConversationSessionId
	// subscribe: ws://topic/conversations/:msgConversationSessionId
	@MessageMapping("/conversations/create/{targetUserId}")
	public void createMessageToUser(
		@DestinationVariable(value = "targetUserId") Long targetUserId,
		MsgDmConversationCreatePub msgDmConversationCreatePub,
		Principal principal) {
		String destination = WebSocketPathConst.MESSAGE_CONVERSATION_BROKER_PATH;

		if (principal instanceof Authentication) {
			Authentication authentication = (Authentication)principal;
			CustomUserDetails userDetails = (CustomUserDetails)authentication.getPrincipal();

			Long snsUserId = (userDetails == null) ? null : Long.valueOf(userDetails.getUserId());
			messagesService.createDirectNewMsgConversation(msgDmConversationCreatePub, destination, targetUserId,
				snsUserId);
		} else {
			throw new UnauthorizedErrorException("인증되지 않은 계정입니다.");
		}
	}

	// publish: ws://url/message/ws/update/conversation
	// subscribe: ws://topic/message/conversation/:msgConversationSessionId
	@MessageMapping("/conversations/update/{msgConversationSessionId}")
	public void updateMessageToUser(
		@DestinationVariable(value = "msgConversationSessionId") String msgConversationSessionId,
		MsgConversationUpdatePub message,
		Principal principal) {

		String destination = UrlUtils.getWebsocketTargetUri(WebSocketPathConst.MESSAGE_CONVERSATION_BROKER_PATH,
			Long.valueOf(msgConversationSessionId));

		if (principal instanceof Authentication) {
			Authentication authentication = (Authentication)principal;
			CustomUserDetails userDetails = (CustomUserDetails)authentication.getPrincipal();

			Long snsUserId = (userDetails == null) ? null : Long.valueOf(userDetails.getUserId());
			messageTemplate.convertAndSend(destination, MsgConversationSub.builder()
				.msgType(message.getMsgType())
				.msgContent(message.getMsgContent())
				.msgId(message.getMsgId())
				.hasMsgReaction(message.getHasMsgReaction())
				.msgReactionType(message.getMsgReactionType())
				.sendAt(LocalDateTime.now())
				.sourceUserId(snsUserId.toString())
				.build());
		} else {
			throw new UnauthorizedErrorException("인증되지 않은 계정입니다.");
		}
	}

	// publish: ws://url/message/ws/delete/conversations/:msgConversationSessionId/:msgConversationId
	// subscribe: ws://topic/message/conversations/:msgConversationSessionId
	@MessageMapping("/conversations/delete/{msgConversationId}")
	public void deleteMessageToUser(
		@DestinationVariable(value = "msgConversationId") Long msgConversationId,
		Principal principal) {
		String destination = WebSocketPathConst.MESSAGE_CONVERSATION_BROKER_PATH;

		if (principal instanceof Authentication) {
			Authentication authentication = (Authentication)principal;
			CustomUserDetails userDetails = (CustomUserDetails)authentication.getPrincipal();

			Long snsUserId = (userDetails == null) ? null : Long.valueOf(userDetails.getUserId());
			messagesService.deleteMsgConversation(destination, msgConversationId, snsUserId);
		} else {
			throw new UnauthorizedErrorException("인증되지 않은 계정입니다.");
		}

	}
}
