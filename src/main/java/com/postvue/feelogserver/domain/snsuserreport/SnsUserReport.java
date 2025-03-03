package com.postvue.feelogserver.domain.snsuserreport;

import com.postvue.feelogserver.core.config.SnowflakeId;
import com.postvue.feelogserver.domain.snsuserreport.vo.UserReportReasonType;
import com.postvue.feelogserver.domain.snsuserreport.vo.UserReportStatus;
import com.postvue.feelogserver.domain.snsusers.SnsUser;
import com.postvue.feelogserver.global.common.tables.mixin.BaseMixinImpl;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "SNS_USER_REPORTS_TB", indexes = {
	@Index(name = "IDX__reporter_user_id_BY_SNS_USER_REPORTS",
		columnList = "reporter_user_id"),
	@Index(name = "IDX__reported_user_id_BY_SNS_USER_REPORTS",
		columnList = "reported_user_id"),
	}
)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SnsUserReport extends BaseMixinImpl {
	@Id
	@SnowflakeId
	@Column(name = "sns_user_report_id", updatable = false)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "reporter_user_id", nullable = false, updatable = false)
	private SnsUser reporterUser;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "reported_user_id", updatable = false)
	private SnsUser reportedUser;

	@Column(name = "report_reason", length = 2047, nullable = false)
	private String reportReason;

	@Enumerated(EnumType.STRING)
	@Column(name = "user_report_reason_type", nullable = false)
	private UserReportReasonType userReportReasonType;

	@Enumerated(EnumType.STRING)
	@Column(name = "user_report_status", nullable = false)
	private UserReportStatus userReportStatus;
}
