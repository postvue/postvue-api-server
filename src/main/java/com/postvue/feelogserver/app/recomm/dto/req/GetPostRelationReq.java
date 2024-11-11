package com.postvue.feelogserver.app.recomm.dto.req;

import java.util.List;

import lombok.Getter;

@Getter
public class GetPostRelationReq {
	private List<String> tagList;
}
