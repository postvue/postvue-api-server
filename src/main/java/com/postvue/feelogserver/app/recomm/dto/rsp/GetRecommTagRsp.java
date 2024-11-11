package com.postvue.feelogserver.app.recomm.dto.rsp;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class GetRecommTagRsp {
	private String tagName;
	private String tagId;
	private String tagBkgdContent;
	private String tagBkgdContentType;
}
