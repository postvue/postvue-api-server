package com.postvue.feelogserver.app.profiles.dto.req.put;

import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;

@Getter
public class PutMyProfileInfoReq {
	private String nickname;
	private String introduce;
	private String website;
}
