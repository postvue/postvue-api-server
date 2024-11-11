package com.postvue.feelogserver.app.messages.dto.session.ws.pub;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MsgConversationUpdatePub {
	private String msgId;
	private Boolean hasMsg;
	private String msgType;
	private String msgContent;
	private Boolean hasMsgReaction;
	private String msgReactionType;
	private Boolean isRead;
}
