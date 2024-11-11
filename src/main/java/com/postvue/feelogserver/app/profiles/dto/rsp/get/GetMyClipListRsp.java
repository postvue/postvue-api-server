package com.postvue.feelogserver.app.profiles.dto.rsp.get;

import java.util.List;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Setter
@Getter
public class GetMyClipListRsp {
	private String cursorId;
	private List<GetMyProfilePostRsp> myClipRspList;
}
