package com.postvue.feelogserver.app.posts.dto.req.create;

import java.util.List;

import lombok.Getter;

@Getter
public class SnsRepostCreateReq {
	List<String> tagList;
	Float latitude;
	Float longitude;

	String title;
	String bodyText;
}
