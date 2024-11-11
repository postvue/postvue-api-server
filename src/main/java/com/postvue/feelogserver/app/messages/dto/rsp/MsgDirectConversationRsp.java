package com.postvue.feelogserver.app.messages.dto.rsp;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@AllArgsConstructor
@Getter
@Setter
public class MsgDirectConversationRsp {
	private String msgRoomId;
	private String msgId;
	private String msgType;
	private String msgContent;
	private LocalDateTime sendAt;
	private Boolean hasMsgReaction;
	private String msgReactionType;
	private String sourceUserId;
	private Boolean isOtherMsg;
}
