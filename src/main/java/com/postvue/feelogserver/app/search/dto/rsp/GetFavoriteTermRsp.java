package com.postvue.feelogserver.app.search.dto.rsp;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Setter
@Getter
public class GetFavoriteTermRsp {
	private String favoriteTermName;
	private String favoriteTermContent;
	private String favoriteTermContentType;
}
