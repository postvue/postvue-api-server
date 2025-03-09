package com.postvue.feelogserver.domain.snsusermessages.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
@AllArgsConstructor
public class MsgMetaInfo {
	private String ogImage;
	private String ogTitle;
	private String ogDescription;
}
