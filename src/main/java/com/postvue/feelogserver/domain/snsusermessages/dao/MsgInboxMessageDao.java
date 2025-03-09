package com.postvue.feelogserver.domain.snsusermessages.dao;

import java.time.LocalDateTime;

public interface MsgInboxMessageDao {
	Long getMsgRoomId();

	Integer getUnreadCount();

	Long getLatestMsgId();

	String getLatestMsgType();

	String getLatestMsgContent();

	LocalDateTime getPostedAt();

	Long getTargetUserId();
}
