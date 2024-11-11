package com.postvue.feelogserver.domain.snspostcommentlikes;

import java.time.LocalDateTime;

import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.DynamicInsert;

import com.postvue.feelogserver.core.config.SnowflakeId;
import com.postvue.feelogserver.domain.snspostcommentreactions.SnsPostCommentReaction;
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
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "SNS_POST_COMMENT_LIKES_TB", indexes = {
	@Index(name = "IDX__sns_post_comment_reaction_id_BY_SNS_POST_COMMENT_LIKES",
		columnList = "sns_post_comment_reaction_id, is_liked")},
	uniqueConstraints = {
		@UniqueConstraint(columnNames = {"sns_post_comment_reaction_id", "sns_user_id"})
	}
)
@Builder
@NoArgsConstructor
@AllArgsConstructor
@DynamicInsert
public class SnsPostCommentLike extends BaseMixinImpl {
	@Id
	@SnowflakeId
	@Column(name = "sns_post_comment_like_id", updatable = false)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "sns_post_id", nullable = false, updatable = false)
	private SnsPost snsPost;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "sns_post_comment_reaction_id", updatable = false)
	private SnsPostCommentReaction snsPostCommentReaction;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "sns_user_id", nullable = false, updatable = false)
	private SnsUser snsUser;

	@Column(name = "is_liked", nullable = false)
	@ColumnDefault(value = "false")
	private Boolean isLiked;

	@Column(name = "is_liked_at")
	private LocalDateTime isLikedAt;
}
