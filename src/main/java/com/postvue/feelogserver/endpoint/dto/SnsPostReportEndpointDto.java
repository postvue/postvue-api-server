package com.postvue.feelogserver.endpoint.dto;

import java.time.LocalDateTime;

import com.postvue.feelogserver.domain.snsblockusers.SnsBlockUser;
import com.postvue.feelogserver.domain.snspostreports.SnsPostReport;
import com.postvue.feelogserver.domain.snspostreports.vo.PostReportReasonType;
import com.postvue.feelogserver.domain.snspostreports.vo.PostReportStatus;

public record SnsPostReportEndpointDto(
	String id,
	String reporterUser_id,
	String reportedUser_id,
	String snsPost_id,

	String snsPostCommentReaction_id,
	String reportReason,
	PostReportReasonType postReportReasonType,
	PostReportStatus postReportStatus,
	LocalDateTime createdAt,
	LocalDateTime lastUpdatedAt
	) {
	public static SnsPostReportEndpointDto fromEntity(SnsPostReport snsPostReport){
		return new SnsPostReportEndpointDto(
			snsPostReport.getId().toString(), snsPostReport.getReporterUser().getId().toString(),
			snsPostReport.getReportedUser().getId().toString(),
			snsPostReport.getSnsPost().getId().toString(),
			snsPostReport.getSnsPostCommentReaction() != null ? snsPostReport.getSnsPostCommentReaction().getId().toString() : null,
			snsPostReport.getReportReason(),snsPostReport.getPostReportReasonType(),
			snsPostReport.getPostReportStatus(),snsPostReport.getCreatedAt(),snsPostReport.getLastUpdatedAt());
	}
}
