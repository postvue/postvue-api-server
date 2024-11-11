package com.postvue.feelogserver.app.profiles.dto.rsp.put;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class PutProfilePasswordInfoRsp {
	private String accessToken;
}
