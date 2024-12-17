package com.postvue.feelogserver.app.profiles.dto.rsp.common;

import java.time.LocalDateTime;
import java.util.List;

import com.postvue.feelogserver.app.profiles.dto.rsp.get.GetProfileMainScrapPostRsp;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Setter
@Getter
public class ScrapThumbnailRsp {
	private String scrapId;
	private String scrapName;
	private Integer scrapNum;
	private LocalDateTime lastPostedAt;
	private List<GetProfileMainScrapPostRsp> postScrapPreviewList;
	private Boolean isMe;
}
