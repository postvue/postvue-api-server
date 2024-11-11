package com.postvue.feelogserver.app.auth.dto.req.post;

import jakarta.validation.constraints.NotBlank;

public record NaverLoginReq(@NotBlank String naverAccessToken) {
}
