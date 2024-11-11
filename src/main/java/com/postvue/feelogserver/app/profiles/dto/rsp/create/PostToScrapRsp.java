package com.postvue.feelogserver.app.profiles.dto.rsp.create;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class PostToScrapRsp {
	private String scrapId;
	private Boolean isScraped;
	private Boolean isClipped;
}
