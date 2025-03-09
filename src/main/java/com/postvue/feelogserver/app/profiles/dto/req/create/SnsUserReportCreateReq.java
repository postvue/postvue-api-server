package com.postvue.feelogserver.app.profiles.dto.req.create;

import jakarta.annotation.Nullable;
import lombok.Getter;

@Getter
public class SnsUserReportCreateReq {

	@Nullable
	private String userReportReason;

	private String userReportReasonType;
}
