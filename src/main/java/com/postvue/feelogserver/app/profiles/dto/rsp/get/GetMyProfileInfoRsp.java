package com.postvue.feelogserver.app.profiles.dto.rsp.get;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Builder
@AllArgsConstructor
@Getter
public class GetMyProfileInfoRsp {
	private String userId;
	private String username;
	private String profilePath;
	private String nickname;
	private String introduce;
	private String website;
	private String email;
	private LocalDate birthdate;
	private String gender;
	private Boolean isPrivateProfile;
}

