package com.postvue.feelogserver.app.profiles.dto.rsp.get;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class GetProfileMainScrapPostRsp {
	private String postThumbnailContent;
	private String postThumbnailContentType;
}
