package com.postvue.feelogserver.domain.snsposts;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.Where;
import org.hibernate.type.SqlTypes;
import org.springframework.data.annotation.CreatedDate;

import com.postvue.feelogserver.core.config.SnowflakeId;
import com.postvue.feelogserver.domain.snsposts.vo.PostContentBusinessType;
import com.postvue.feelogserver.domain.snsposts.vo.PostContentBusinessTypeValue;
import com.postvue.feelogserver.domain.snsposts.vo.SnsPostContent;
import com.postvue.feelogserver.domain.snsposts.vo.TgtAudType;
import com.postvue.feelogserver.domain.snsposts.vo.TgtAudTypeValue;
import com.postvue.feelogserver.domain.snstags.vo.PostTag;
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
@Table(name = "SNS_POSTS_TB", indexes = {
	// 해당 게시물의 유저 빠르게 찾기
	@Index(name = "IDX__USER_BY_SNS_POSTS", columnList = "sns_user_id"),
	// 해당 위치를 가진 게시물이 존재하는 지 빠르게 검색
	@Index(name = "IDX__LATITUDE_BY_SNS_POSTS", columnList = "latitude,", unique = false)
})
@Builder
@NoArgsConstructor
@AllArgsConstructor
@DynamicInsert
@Where(clause = "deleted_at IS NULL")
public class SnsPost extends BaseMixinImpl implements Serializable {
	@Id
	@SnowflakeId
	@Column(name = "sns_post_id", updatable = false)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "sns_user_id", updatable = false)
	private SnsUser snsUser;

	@Column(name = "is_exposed", nullable = false)
	@ColumnDefault(value = "true")
	private Boolean isExposed;

	// @REFER: 스레드 와 포스트를 구분하지 않도록 함
	// @Column(name = "post_category", nullable = false)
	// @Enumerated(value = EnumType.STRING)
	// private SnsPostCategory postCategory;

	@JdbcTypeCode(SqlTypes.JSON)
	@Column(name = "sns_post_contents", nullable = false)
	@ColumnDefault(value = "'[]'")
	private List<SnsPostContent> snsPostContents;

	@Column(nullable = true, name = "post_title")
	private String postTitle;

	@Column(nullable = true, name = "post_body_text")
	private String postBodyText;

	@Column(name = "post_caption_content")
	private String postCaptionContent;

	private Float latitude;
	private Float longitude;

	private String address;

	@Column(name = "is_show_address", insertable = false)
	@ColumnDefault(value = "true")
	private Boolean isShowAddress;

	@JdbcTypeCode(SqlTypes.JSON)
	@Column(nullable = false)
	@ColumnDefault(value = "'[]'")
	private List<PostTag> tags;

	@Column(name = "reaction_count")
	@ColumnDefault(value = "1")
	private Integer reactionCount;

	@Column(name = "is_repost")
	@ColumnDefault(value = "false")
	private Boolean isRepost;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "repost_origin_id")
	private SnsPost repostOrigin;

	@Enumerated(value = EnumType.STRING)
	@Column(name = "tgt_aud_type", insertable = false)
	@ColumnDefault(value = TgtAudTypeValue.DEFAULT_PUBLIC_AUDIENCE_VALUE)
	private TgtAudType tgtAudType;

	@Enumerated(value = EnumType.STRING)
	@Column(name = "post_content_business_type", insertable = false)
	@ColumnDefault(value = PostContentBusinessTypeValue.DEFAULT_POST_CONTENT_BUSINESS_TYPE_VALUE)
	private PostContentBusinessType postContentBusinessType;

	@CreatedDate
	@Column(name = "deleted_at")
	private LocalDateTime deletedAt;

	// @Enumerated(value = EnumType.STRING)
	// @Column(name = "aud_share_scope", insertable = false)
	// @ColumnDefault(value = AudShareScopeValue.EVERYONE_SCOPE_VALUE)
	// private AudShareScope audShareScope;
}
