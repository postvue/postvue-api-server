package com.postvue.feelogserver.app.posts.dto.req.create;

import jakarta.annotation.Nullable;
import lombok.Getter;

@Getter
public class SnsPostReportCreateReq {
	@Nullable
	private String postReportReason;

	private String postReportReasonType;
}
