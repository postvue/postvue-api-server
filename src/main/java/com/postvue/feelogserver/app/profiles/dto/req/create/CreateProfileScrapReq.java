package com.postvue.feelogserver.app.profiles.dto.req.create;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class CreateProfileScrapReq {
	private String scrapName;
	private String targetAudienceValue;
}
