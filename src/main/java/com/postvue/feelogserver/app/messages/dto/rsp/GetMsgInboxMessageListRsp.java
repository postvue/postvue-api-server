package com.postvue.feelogserver.app.messages.dto.rsp;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@AllArgsConstructor
@Getter
@Setter
public class GetMsgInboxMessageListRsp {
	private String cursorId;
	private List<GetMsgInboxMessage> msgInboxMessageList;
}
