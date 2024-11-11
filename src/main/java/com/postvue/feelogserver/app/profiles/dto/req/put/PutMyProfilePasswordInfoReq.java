package com.postvue.feelogserver.app.profiles.dto.req.put;

import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;

@Getter
public class PutMyProfilePasswordInfoReq {
	@NotEmpty(message = "값이 비어 있습니다.")
	private String currentPassword;

	@NotEmpty(message = "값이 비어 있습니다.")
	private String password;
}
