package com.postvue.feelogserver.app.messages.dto.session.ws.sub;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@AllArgsConstructor
@Getter
@Setter
public class MsgConversationSub {
	private String msgRoomId;
	private Boolean isGroupedMsg;
	private String targetUserId;
	private String msgId;
	private String msgType;
	private String msgContent;
	private LocalDateTime sendAt;
	private Boolean hasMsgReaction;
	private String msgReactionType;
	private String sourceUserId;
	private Boolean isDeleted;
}
