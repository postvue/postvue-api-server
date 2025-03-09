package com.postvue.feelogserver.domain.snsnotifications;

import java.io.Serializable;
import java.util.List;

import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import com.postvue.feelogserver.core.config.SnowflakeId;
import com.postvue.feelogserver.domain.snsnotifications.vo.SnsNotificationContent;
import com.postvue.feelogserver.domain.snsnotifications.vo.SnsNotificationType;
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
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "SNS_NOTIFICATIONS_TB", indexes = {
	@Index(name = "IDX__USER_BY_SNS_NOTIFICATIONS", columnList = "sns_user_id")})
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SnsNotification extends BaseMixinImpl implements Serializable {
	@Id
	@SnowflakeId
	@Column(name = "sns_notification_id", updatable = false)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "sns_user_id", nullable = false)
	private SnsUser snsUser;

	@Column(name = "username", nullable = false)
	private String username;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "sns_post_id")
	private SnsPost snsPost;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "sns_follower_id")
	private SnsUser followerUser;

	@Column(name = "notification_content_user_id", nullable = false)
	private Long notificationContentUserid;

	@Column(name = "notification_content_username", nullable = false)
	private String notificationContentUsername;

	@Column(name = "notification_content_user_profile_path", nullable = false)
	private String notificationContentUserProfilePath;

	@Enumerated(value = EnumType.STRING)
	@Column(name = "notification_type", nullable = false)
	private SnsNotificationType snsNotificationType;

	@JdbcTypeCode(SqlTypes.JSON)
	@Column(name = "sns_notification_contents", nullable = false)
	@ColumnDefault(value = "'[]'")
	private List<SnsNotificationContent> snsNotificationContents;

	// @Column(name = "is_read", nullable = false)
	// private Boolean isRead;

	@Column(name = "notification_count", nullable = false)
	private Integer notificationCount;

}
