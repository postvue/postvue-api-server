package com.postvue.feelogserver.app.profiles.dto.rsp.get;

import java.util.List;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class GetProfileUserRsp {
	private List<GetProfileUserByUsername> getProfileUserByUsernameList;
	private String cursorId;
}
