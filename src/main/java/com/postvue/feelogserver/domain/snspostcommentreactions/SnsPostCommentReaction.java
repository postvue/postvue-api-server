package com.postvue.feelogserver.domain.snspostcommentreactions;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.SQLRestriction;
import org.hibernate.annotations.Where;

import com.postvue.feelogserver.core.config.SnowflakeId;
import com.postvue.feelogserver.domain.snspostcommentlikes.SnsPostCommentLike;
import com.postvue.feelogserver.domain.snspostcommentreactions.vo.PostCommentMediaType;
import com.postvue.feelogserver.domain.snsposts.SnsPost;
import com.postvue.feelogserver.domain.snsusers.SnsUser;
import com.postvue.feelogserver.global.common.tables.mixin.BaseMixinImpl;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "SNS_POST_COMMENT_REACTIONS_TB", indexes = {
	@Index(
		name = "IDX__SOURCE_COMMENTS_BY_SNS_POST_COMMENT_REACTIONS",
		columnList = "source_comment_id, is_commented")})
@Builder
@DynamicInsert
@NoArgsConstructor
@AllArgsConstructor
@SQLRestriction("deleted_at is NULL")
public class SnsPostCommentReaction extends BaseMixinImpl implements Serializable {
	@Id
	@SnowflakeId
	@Column(name = "sns_post_comment_reaction_id", updatable = false)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "sns_post_id", nullable = false,updatable = false)
	private SnsPost snsPost;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "source_comment_id", updatable = false)
	private SnsPostCommentReaction sourceComment;

	@OneToMany(mappedBy = "sourceComment", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
	private List<SnsPostCommentReaction> sourceCommentList;

	@OneToMany(mappedBy = "snsPostCommentReaction", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
	private List<SnsPostCommentLike> snsPostCommentLikeList;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "comment_user_id", nullable = false, updatable = false)
	private SnsUser commentUser;

	// @Enumerated(EnumType.STRING)
	// @Column(name = "post_comment_type")
	// private PostCommentType postCommentType;
	//
	// @Column(name = "post_comment_content", length = 2048)
	// private String postCommentContent;

	@Column(name = "comment_msg", length = 2048)
	private String commentMsg;

	@Enumerated(EnumType.STRING)
	@Column(name = "comment_media_type")
	private PostCommentMediaType commentMediaType;

	@Column(name = "comment_media_content")
	private String commentMediaContent;

	// @REFER: sns_post_comment_likes_tb로 따로 관리
	// @Column(name = "is_liked", nullable = false)
	// @ColumnDefault(value = "false")
	// private Boolean isLiked;
	//
	// @Column(name = "is_liked_at")
	// private LocalDateTime isLikedAt;

	@Column(name = "is_source", nullable = false)
	@ColumnDefault(value = "false")
	private Boolean isSource;

	//@REFER: comment(댓글) 행이 있냐 없냐로 구분할 수 있는 데, 굳이 is_commented이 필요 할까?
	@Column(name = "is_commented")
	private Boolean isCommented;

	@Column(name = "deleted_at")
	private LocalDateTime deletedAt;
}
