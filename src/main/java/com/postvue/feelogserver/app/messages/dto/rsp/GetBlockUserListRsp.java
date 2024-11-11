package com.postvue.feelogserver.app.messages.dto.rsp;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
@AllArgsConstructor
public class GetBlockUserListRsp {
	private String cursorId;
	private List<GetBlockHiddenUserRsp> blockUserList;
}
