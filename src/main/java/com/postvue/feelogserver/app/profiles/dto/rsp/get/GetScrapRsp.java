package com.postvue.feelogserver.app.profiles.dto.rsp.get;

import java.util.List;

import com.postvue.feelogserver.app.posts.dto.rsp.get.SnsPostRsp;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class GetScrapRsp {
	private String cursorId;
	private List<SnsPostRsp> snsPostRspList;
}
