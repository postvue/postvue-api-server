package com.postvue.feelogserver.domain.snspostreports.vo;

public enum PostReportReasonType {
	DISLIKE, //게시물이 마음에 들지 않습니다.
	INACCURATE_LOCATION, //위치 정보가 정확하지 않습니다.
	SPAM_OR_SCAM, //스팸, 사기 또는 스팸
	SENSITIVE_CONTENT, //민감한 사진 또는 동영상을 보여주고 있습니다.
	HARMFUL_OR_ABUSIVE, //가학적이거나 유해한 내용입니다.
	OTHER, //직접 입력
}
