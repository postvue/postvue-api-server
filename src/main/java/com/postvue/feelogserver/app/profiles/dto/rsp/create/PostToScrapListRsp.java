package com.postvue.feelogserver.app.profiles.dto.rsp.create;

import java.util.List;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class PostToScrapListRsp {
	private List<String> scrapIdList;
	private Boolean isClipped;
}
