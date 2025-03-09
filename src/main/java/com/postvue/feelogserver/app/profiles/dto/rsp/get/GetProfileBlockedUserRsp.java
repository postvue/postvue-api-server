package com.postvue.feelogserver.app.profiles.dto.rsp.get;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class GetProfileBlockedUserRsp {
	private String blockedUserId;
	private String blockedNickname;
	private String blockedUsername;
	private String blockedUserProfilePath;
}
