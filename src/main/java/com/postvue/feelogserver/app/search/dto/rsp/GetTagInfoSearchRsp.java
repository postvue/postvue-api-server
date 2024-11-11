package com.postvue.feelogserver.app.search.dto.rsp;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Setter
@Getter
public class GetTagInfoSearchRsp {
	private String tagName;
	private String tagBkgdContent;
	private String tagBkgdContentType;
}
