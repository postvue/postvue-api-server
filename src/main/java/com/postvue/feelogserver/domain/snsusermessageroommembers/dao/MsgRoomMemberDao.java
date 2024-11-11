package com.postvue.feelogserver.domain.snsusermessageroommembers.dao;

import com.postvue.feelogserver.domain.snsusermessagerooms.vo.MsgRoomType;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MsgRoomMemberDao {
	private Long sourceUserId;
	private MsgRoomType msgRoomType;
	private Long targetUserId;
}
