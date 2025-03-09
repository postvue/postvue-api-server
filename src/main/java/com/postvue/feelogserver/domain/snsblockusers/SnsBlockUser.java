package com.postvue.feelogserver.domain.snsblockusers;

import java.io.Serializable;
import java.time.LocalDateTime;

import com.postvue.feelogserver.core.config.SnowflakeId;
import com.postvue.feelogserver.domain.snsusers.SnsUser;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
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
@Table(name = "SNS_BLOCK_USERS_TB",
	uniqueConstraints = {
		@UniqueConstraint(columnNames = {"sns_blocker_user_id", "sns_blocked_user_id"})
	})
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SnsBlockUser implements Serializable {
	@Id
	@SnowflakeId
	@Column(name = "sns_block_user_id" ,updatable = false)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "sns_blocker_user_id" ,updatable = false)
	private SnsUser snsBlockerUser;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "sns_blocked_user_id", updatable = false)
	private SnsUser snsBlockedUser;

	@Column(name = "is_blocked_at")
	private LocalDateTime isBlockedAt;
}

