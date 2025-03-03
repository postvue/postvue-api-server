package com.postvue.feelogserver.endpoint.dto;

import java.time.LocalDateTime;

import com.postvue.feelogserver.domain.snspostreports.SnsPostReport;
import com.postvue.feelogserver.domain.snspostreports.vo.PostReportReasonType;
import com.postvue.feelogserver.domain.snspostreports.vo.PostReportStatus;
import com.postvue.feelogserver.domain.snsuserreport.SnsUserReport;
import com.postvue.feelogserver.domain.snsuserreport.vo.UserReportReasonType;
import com.postvue.feelogserver.domain.snsuserreport.vo.UserReportStatus;

public record SnsUserReportEndpointDto(
	String id,
	String reporterUser_id,
	String reportedUser_id,
	String reportReason,
	UserReportReasonType userReportReasonType,
	UserReportStatus userReportStatus,
	LocalDateTime createdAt,
	LocalDateTime lastUpdatedAt
	) {
	public static SnsUserReportEndpointDto fromEntity(SnsUserReport snsUserReport){
		return new SnsUserReportEndpointDto(
			snsUserReport.getId().toString(),
			snsUserReport.getReporterUser().getId().toString(),
			snsUserReport.getReportedUser().getId().toString(),
			snsUserReport.getReportReason(),
			snsUserReport.getUserReportReasonType(),
			snsUserReport.getUserReportStatus(),
			snsUserReport.getCreatedAt(),
			snsUserReport.getLastUpdatedAt());
	}
}
