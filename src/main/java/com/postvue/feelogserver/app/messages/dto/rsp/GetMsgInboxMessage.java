package com.postvue.feelogserver.app.messages.dto.rsp;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
@AllArgsConstructor
public class GetMsgInboxMessage {
	private String msgRoomId;
	private String targetUserId;
	private String username;
	private String nickname;
	private String profilePath;
	private Integer unreadCount;
	private LocalDateTime sendAt;
	private String msgId;
	private Boolean hasMsgMedia;
	private String msgTextContent;
	private String msgMediaType;
	private String msgMediaContent;
	private MsgLinkMetaInfo msgLinkMetaInfo;
}
