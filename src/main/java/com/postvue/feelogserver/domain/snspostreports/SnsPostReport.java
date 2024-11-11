package com.postvue.feelogserver.domain.snspostreports;

import java.time.LocalDateTime;

import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.DynamicInsert;

import com.postvue.feelogserver.core.config.SnowflakeId;
import com.postvue.feelogserver.domain.snspostcommentreactions.SnsPostCommentReaction;
import com.postvue.feelogserver.domain.snspostreports.vo.PostReportReasonType;
import com.postvue.feelogserver.domain.snspostreports.vo.PostReportStatus;
import com.postvue.feelogserver.domain.snsposts.SnsPost;
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
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "SNS_POST_REPORTS_TB", indexes = {
	@Index(name = "IDX__reporter_user_id_BY_SNS_POST_REPORTS",
		columnList = "reporter_user_id"),
	@Index(name = "IDX__reported_user_id_BY_SNS_POST_REPORTS",
		columnList = "reported_user_id"),
	@Index(name = "IDX__sns_post_id_BY_SNS_POST_REPORTS",
		columnList = "sns_post_id"),
	}
)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SnsPostReport extends BaseMixinImpl {
	@Id
	@SnowflakeId
	@Column(name = "sns_post_report_id", updatable = false)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "reporter_user_id", nullable = false, updatable = false)
	private SnsUser reporterUser;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "reported_user_id", nullable = false, updatable = false)
	private SnsUser reportedUser;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "sns_post_id", nullable = false, updatable = false)
	private SnsPost snsPost;

	@Column(name = "report_reason", length = 2047, nullable = false)
	private String reportReason;

	@Enumerated(EnumType.STRING)
	@Column(name = "post_report_reason_type", nullable = false)
	private PostReportReasonType postReportReasonType;

	@Enumerated(EnumType.STRING)
	@Column(name = "post_report_status", nullable = false)
	private PostReportStatus postReportStatus;
}
