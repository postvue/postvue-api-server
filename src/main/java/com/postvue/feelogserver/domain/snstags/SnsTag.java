package com.postvue.feelogserver.domain.snstags;

import java.io.Serializable;
import java.time.LocalDateTime;

import org.hibernate.annotations.ColumnDefault;

import com.postvue.feelogserver.core.config.SnowflakeId;
import com.postvue.feelogserver.domain.snsposts.vo.PostContentType;
import com.postvue.feelogserver.global.common.tables.mixin.BaseMixinImpl;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "SNS_TAGS_TB", indexes = {
	// 해당 게시물의 유저 빠르게 찾기
	@Index(name = "IDX__TAG_NAME_UNIQUE_BY_SNS_TAGS", columnList = "tag_name", unique = true),
})
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SnsTag extends BaseMixinImpl implements Serializable {
	@Id
	@SnowflakeId
	@Column(name = "sns_tag_id", updatable = false)
	private Long id;

	@Column(name = "tag_name", nullable = false, unique = true)
	private String tagName;

	// @REFER: not nullable 지정 -> 일단 나중에
	@Column(name = "tag_reps_batch_content", nullable = true)
	private String tagRepsBatchContent;

	// @REFER: not nullable 지정 -> 일단 나중에
	@Column(name = "tag_reps_batch_content_type", nullable = true)
	@Enumerated(EnumType.STRING)
	private PostContentType tagRepsBatchContentType;

	@Column(name = "is_exposed")
	@ColumnDefault(value = "true")
	private Boolean isExposed;

	@Column(name = "deleted_at")
	private LocalDateTime deletedAt;

	// @Ref 속성이 아닌 테이블로 구현
	// @JdbcTypeCode(SqlTypes.JSON)
	// private List<PostTag> relatedTagIds;
}
