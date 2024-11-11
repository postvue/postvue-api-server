package com.postvue.feelogserver.domain.snsusermessages.dao;

import java.time.LocalDateTime;

import com.postvue.feelogserver.domain.snsusermessages.vo.SnsMsgType;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MsgConversationDao {
	Long snsUserMessageId;
	Boolean isOtherMsg;
	SnsMsgType msgType;
	String msgContent;
	LocalDateTime sendAt;
	Boolean isHidden;
	Boolean isBlocked;
	Long snsUserMessageRoomId;
}
