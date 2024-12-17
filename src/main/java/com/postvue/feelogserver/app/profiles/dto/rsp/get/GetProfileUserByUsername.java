package com.postvue.feelogserver.app.profiles.dto.rsp.get;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class GetProfileUserByUsername {
	private String userId;
	private String username;
	private String nickname;
	private String profilePath;
	private Boolean isFollowed;
}
