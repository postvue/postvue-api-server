package com.postvue.feelogserver.domain.snsusermessages.dao;

import java.time.LocalDateTime;

import com.postvue.feelogserver.domain.snsusermessages.vo.MsgMediaType;
import com.postvue.feelogserver.domain.snsusermessages.vo.MsgMetaInfo;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MsgConversationDao {
	Long snsUserMessageId;
	Boolean isOtherMsg;
	String msgTextContent;
	MsgMediaType msgMediaType;
	String msgMediaContent;
	MsgMetaInfo msgMetaInfo;
	LocalDateTime sendAt;
	Boolean isHidden;
	Boolean isBlocked;
	Long snsUserMessageRoomId;
}
