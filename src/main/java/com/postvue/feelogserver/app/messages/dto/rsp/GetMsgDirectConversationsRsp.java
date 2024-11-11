package com.postvue.feelogserver.app.messages.dto.rsp;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class GetMsgDirectConversationsRsp {
	private String cursorId;
	private List<MsgDirectConversationRsp> msgConversationRspList;
}
