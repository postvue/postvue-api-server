package com.postvue.feelogserver.app.auth.dto.req.post;

import jakarta.validation.constraints.NotBlank;

public record GoogleLoginReq(@NotBlank String idToken) {
}
