package com.postvue.feelogserver.domain.snsuserfollows;

import java.io.Serializable;

import com.postvue.feelogserver.core.config.SnowflakeId;
import com.postvue.feelogserver.domain.snsusers.SnsUser;
import com.postvue.feelogserver.global.common.tables.mixin.BaseMixinImpl;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
@Table(name = "SNS_USER_FOLLOWS_TB", indexes = {
	@Index(name = "IDX__FOLLOWER_BY_SNS_USER_FOLLOWS", columnList = "follower_id")},
	uniqueConstraints = {
		@UniqueConstraint(columnNames = {"following_id", "follower_id"}, name = "IDX_FOLLOWING_FOLLOWER_ID_BY_SNS_USER_FOLLOWS")
	})
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SnsUserFollow extends BaseMixinImpl implements Serializable {
	@Id
	@SnowflakeId
	@Column(name = "sns_user_follow_id", updatable = false)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "following_id", updatable = false)
	private SnsUser followingUser;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "follower_id", updatable = false)
	private SnsUser followerUser;

	//@ANSWER: 불필요한 조회 때문에 제거
	// @Column(name = "is_follower_back", nullable = false)
	// @ColumnDefault(value = "false")
	// private Boolean isFollowerBack;
}
