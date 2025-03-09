package com.postvue.feelogserver.domain.snsuserfollowstatistics;

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
@Table(name = "SNS_USER_FOLLOW_STATISTICS_TB", indexes = {
	// 해당 게시물의 유저 빠르게 찾기
	@Index(name = "IDX__USER_ID_UNIQUE_BY_SNS_USER_FOLLOW_STATISTICS", columnList = "sns_user_id", unique = true),
})
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SnsUserFollowStatistic implements Serializable {
	@Id
	@SnowflakeId
	@Column(name = "sns_user_follow_statistic_id", updatable = false)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "sns_user_id", nullable = false, updatable = false, unique = true)
	private SnsUser snsUser;

	@Column(name = "follower_num")
	private Integer followerNum;

	@Column(name = "following_num")
	private Integer followingNum;
}
