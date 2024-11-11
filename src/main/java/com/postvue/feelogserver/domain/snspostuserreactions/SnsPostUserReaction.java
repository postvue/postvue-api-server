package com.postvue.feelogserver.domain.snspostuserreactions;

import java.io.Serializable;
import java.time.LocalDateTime;

import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.DynamicInsert;

import com.postvue.feelogserver.core.config.SnowflakeId;
import com.postvue.feelogserver.domain.snsposts.SnsPost;
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
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "SNS_POST_USER_REACTIONS_TB", indexes = {
	@Index(name = "IDX__USER_BY_SNS_POST_USER_REACTIONS", columnList = "sns_user_id")})
@Builder
@NoArgsConstructor
@AllArgsConstructor
@DynamicInsert
public class SnsPostUserReaction extends BaseMixinImpl implements Serializable {
	@Id
	@SnowflakeId
	@Column(name = "sns_post_user_reaction_id",updatable = false)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "sns_post_id", nullable = false,updatable = false)
	private SnsPost snsPost;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "sns_user_id", nullable = false,updatable = false)
	private SnsUser snsUser;

	@Column(name = "is_liked", nullable = false)
	@ColumnDefault(value = "false")
	private Boolean isLiked;

	@Column(name = "is_liked_at")
	private LocalDateTime isLikedAt;

	@Column(name = "is_clipped", nullable = false)
	@ColumnDefault(value = "false")
	private Boolean isClipped;

	@Column(name = "is_clipped_at")
	private LocalDateTime isClippedAt;

	@Column(name = "is_reposted", nullable = false)
	@ColumnDefault(value = "false")
	private Boolean isReposted;

	@Column(name = "is_reposted_at")
	private LocalDateTime isRepostedAt;

	@Column(name = "is_bookmarked", nullable = false)
	@ColumnDefault(value = "false")
	private Boolean isBookmarked;

	@Column(name = "is_bookmarked_at")
	private LocalDateTime isBookmarkedAt;

	// @REFER: SnsPostCommentReaction 테이블 추가로 인해 삭제
	// @Column(name = "is_commented", nullable = false)
	// @ColumnDefault(value = "false")
	// private Boolean isCommented;

	// 이 게시물 숨기기
	@Column(name = "is_shown", nullable = false)
	@ColumnDefault(value = "true")
	private Boolean isShown;

	@Column(name = "not_shown_at")
	private LocalDateTime notShownAt;
}
