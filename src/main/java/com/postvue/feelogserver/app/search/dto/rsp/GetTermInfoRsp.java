package com.postvue.feelogserver.app.search.dto.rsp;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Setter
@Getter
public class GetTermInfoRsp {
	private String favoriteTermName;
	private String favoriteTermContent;
	private String favoriteTermContentType;
	private Boolean isTag;
	private Boolean isFavoriteTerm;
	private Boolean isFollowTag;
	private Boolean isExistTag;
}
