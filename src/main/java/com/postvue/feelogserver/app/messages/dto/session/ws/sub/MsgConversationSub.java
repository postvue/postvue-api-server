package com.postvue.feelogserver.app.messages.dto.session.ws.sub;

import java.time.LocalDateTime;

import com.postvue.feelogserver.app.messages.dto.rsp.MsgLinkMetaInfo;

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

	// 전달 메시지 유형: 생성, 삭제, 수정, 오류
	private String eventType;

	private String sourceUserId;
	private String targetUserId;
	private String msgId;

	// 콘텐츠
	private String msgTextContent;
	private Boolean hasMsgMedia;
	private String msgMediaType;
	private String msgMediaContent;
	private Boolean hasMsgReaction;
	private MsgLinkMetaInfo msgLinkMetaInfo;

	private String msgReactionType;
	private String errorMsg;

	private LocalDateTime sendAt;
}
