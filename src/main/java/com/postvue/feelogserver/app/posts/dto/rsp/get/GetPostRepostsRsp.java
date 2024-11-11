package com.postvue.feelogserver.app.posts.dto.rsp.get;

import java.util.List;

import com.postvue.feelogserver.app.common.rsp.SnsPostFollowsGetRsp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class GetPostRepostsRsp {
	String cursorId;
	List<SnsPostFollowsGetRsp> snsReactionRepostedRspList;
}
