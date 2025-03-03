package com.postvue.feelogserver.app.auth.dto.req.post;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record EmailInfoReq(
	@NotBlank
	@Email
	String email,
	@NotBlank String password
) {
}