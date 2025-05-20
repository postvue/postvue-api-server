package com.postvue.feelogserver.app.subevent.dto;

import java.util.List;

import com.postvue.feelogserver.app.recomm.dto.GetPostContent;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class GetShortArticleRsp {
	private String articleName;
	private Long id;
	private Integer imageNum;
}
