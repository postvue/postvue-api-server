package com.postvue.feelogserver.domain.snsusermessages.vo;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ReadUserInfo {
	private Boolean isRead;
	private Long snsUserId;
	private LocalDateTime readAt;
}
