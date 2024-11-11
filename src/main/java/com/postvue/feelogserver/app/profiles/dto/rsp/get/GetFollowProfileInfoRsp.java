package com.postvue.feelogserver.app.profiles.dto.rsp.get;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Builder
@AllArgsConstructor
@Getter
public class GetFollowProfileInfoRsp {
	private String targetUserId;
	private String username;
	private String profilePath;
	private String msgRoomId;
}

