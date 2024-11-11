package com.postvue.feelogserver.app.session.dto.sub;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class SessionActiveUserListSub {
	List<SessionActiveUserInfoSub> sessionActiveUserInfoSubList;
}
