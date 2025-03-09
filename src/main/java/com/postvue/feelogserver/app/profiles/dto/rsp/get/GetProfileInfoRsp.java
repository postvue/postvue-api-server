package com.postvue.feelogserver.app.profiles.dto.rsp.get;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class GetProfileInfoRsp {
	private String userId;
	private String username;
	private String profilePath;
	private String nickname;
	private String introduce;
	private String website;
	private final Boolean isMe;
	private final Boolean isFollowed;
	private final Boolean isBlocked;
	private final Boolean isPrivate;
	private final Boolean isBlockerUser;
	private final Integer followerNum;
	private final Integer followingNum;
}

