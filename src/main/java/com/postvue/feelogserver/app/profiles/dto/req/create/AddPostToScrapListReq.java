package com.postvue.feelogserver.app.profiles.dto.req.create;

import java.util.List;

import lombok.Getter;

@Getter
public class AddPostToScrapListReq {
	private List<String> scrapIdList;
}
