package com.postvue.feelogserver.app.posts.dto.rsp.get;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
@AllArgsConstructor
public class GetPostRelationRsp {
	private String cursorId;
	private List<SnsPostRsp> snsPostRspList;
}
