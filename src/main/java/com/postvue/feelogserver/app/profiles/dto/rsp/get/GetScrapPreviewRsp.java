package com.postvue.feelogserver.app.profiles.dto.rsp.get;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class GetScrapPreviewRsp {
	private String scrapBoardId;
	private Boolean isScraped;
	private String scrapBoardName;
}
