package com.postvue.feelogserver.app.posts.dto.rsp.get;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class GetPostImageDocResourceRsp {
	private String contentUrl;
	private String contentType;
}
