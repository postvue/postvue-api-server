package com.postvue.feelogserver.app.profiles.dto.req.put;

import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;

@Getter
public class PutMyProfileEmailInfoReq {
	@NotEmpty(message = "이메일이 비어 있습니다.")
	private String email;
}
