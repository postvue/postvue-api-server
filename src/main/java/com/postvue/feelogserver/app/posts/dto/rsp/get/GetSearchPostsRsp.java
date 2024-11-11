package com.postvue.feelogserver.app.posts.dto.rsp.get;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class GetSearchPostsRsp {
	private List<SnsPostRsp> snsPostRspList;
	private Boolean isFetchFavoriteState;
	private Boolean isBookMarkedFavoriteTerm;
}
