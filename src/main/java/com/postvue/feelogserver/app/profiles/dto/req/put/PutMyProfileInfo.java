package com.postvue.feelogserver.app.profiles.dto.req.put;

import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;

@Getter
public class PutMyProfileInfo {
	//@REFER: 프로필 수정 할 수 있도록 파일도 같이
	private String profilePath;
	@NotEmpty(message = "이름이 비어 있습니다.")
	private String nickname;
	private String introduce;
	private String website;
}
