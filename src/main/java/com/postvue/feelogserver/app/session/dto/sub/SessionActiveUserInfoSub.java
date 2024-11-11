package com.postvue.feelogserver.app.session.dto.sub;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Builder
@AllArgsConstructor
@Getter
public class SessionActiveUserInfoSub {
	private String userId;
	private Boolean sessionState;
	private LocalDateTime lastActivityDateTime;
}
