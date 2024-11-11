package com.postvue.feelogserver.app.profiles.dto.rsp.get;

import java.util.List;

import com.postvue.feelogserver.app.common.rsp.SnsPostFollowsGetRsp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class GetMyProfileFollowingListRsp {
	String cursorId;
	List<SnsPostFollowsGetRsp> snsMyProfileFollowingList;
}
