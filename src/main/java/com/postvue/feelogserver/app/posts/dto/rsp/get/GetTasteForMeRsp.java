package com.postvue.feelogserver.app.posts.dto.rsp.get;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class GetTasteForMeRsp {
	List<SnsPostRsp> snsPostRspList;
	String cursorId;
}
