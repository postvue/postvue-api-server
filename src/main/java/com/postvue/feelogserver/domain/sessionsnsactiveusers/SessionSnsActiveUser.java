package com.postvue.feelogserver.domain.sessionsnsactiveusers;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@RedisHash(value = "session:sns:active:users")
@Builder
@Getter
@Setter
public class SessionSnsActiveUser {
	@Id
	private Long userId;

	@Indexed
	private String sessionId;

	private Boolean sessionStatus;

	private LocalDateTime lastActivityDateTime;

	// @REFER: 나중에 추가할 요소
	// private String refreshToken;
	// @TimeToLive(unit = TimeUnit.SECONDS)
	// private Long sessionExpiration;
	// private LocalDateTime loginDateTime;
	// private String userIp;
	//
	// private String browser;
	//
	// private String device;
	//
	// private String os;
	// private String language;
}
