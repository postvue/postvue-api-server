package com.postvue.feelogserver.domain.snsusermessages.dao;

import java.io.IOException;
import java.time.LocalDateTime;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.postvue.feelogserver.domain.snsusermessages.vo.MsgMetaInfo;

public interface MsgInboxMessageDao {
	ObjectMapper objectMapper = new ObjectMapper();
	Long getMsgRoomId();

	Integer getUnreadCount();

	Long getLatestMsgId();

	String getLatestMsgTextContent();

	String getLatestMsgMediaType();

	String getLatestMsgMediaContent();

	LocalDateTime getPostedAt();

	Long getTargetUserId();

	String getLatestMsgMetaInfo();



	default MsgMetaInfo getStringToMetaInfo() {
		try {
			return objectMapper.readValue(getLatestMsgMetaInfo(), new TypeReference<MsgMetaInfo>() {
			});
		} catch (IOException e) {
			return null;
		}
	}
}
