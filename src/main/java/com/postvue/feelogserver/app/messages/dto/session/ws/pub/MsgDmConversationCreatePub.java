package com.postvue.feelogserver.app.messages.dto.session.ws.pub;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MsgDmConversationCreatePub {
	private String msgType;
	private String msgContent;
	private String msgConversationSessionId;
}
