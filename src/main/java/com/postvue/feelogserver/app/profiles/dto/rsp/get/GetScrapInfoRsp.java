package com.postvue.feelogserver.app.profiles.dto.rsp.get;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Setter
@Getter
public class GetScrapInfoRsp {
	private String scrapId;
	private String scrapName;
	private Integer scrapNum;
	private LocalDateTime lastPostedAt;
	private Boolean isMe;
	private String targetAudience;

	private String userId;
	private String username;
	private String nickname;
	private String profilePath;
}
