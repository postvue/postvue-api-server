package com.postvue.feelogserver.app.messages.dto.rsp;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class MessageBlockUserRsp {
	private String targetUserId;
	private Boolean isBlocked;
}
