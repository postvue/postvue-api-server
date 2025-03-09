package com.postvue.feelogserver.app.common.rsp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class SnsPostFollowsGetRsp {
	private String userId;
	private String username;
	private String nickname;
	private String profilePath;
	private Boolean isFollowed;
	private Boolean isMe;
}
