package com.postvue.feelogserver.app.profiles.dto.rsp.get;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class GetProfileBlockedUserRsp {
	private String blockedUserId;
	private String blockedUserName;
	private String blockedUserProfilePath;
}
