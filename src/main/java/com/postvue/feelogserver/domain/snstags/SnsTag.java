package com.postvue.feelogserver.domain.snstags;

import java.io.Serializable;

import org.hibernate.annotations.ColumnDefault;

import com.postvue.feelogserver.core.config.SnowflakeId;
import com.postvue.feelogserver.domain.snsposts.vo.PostContentType;
import com.postvue.feelogserver.global.common.tables.mixin.BaseMixinImpl;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "SNS_TAGS_TB")
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

	@Column(name = "tag_reps_batch_content", nullable = true)
	private String tagRepsBatchContent;

	@Column(name = "tag_reps_batch_content_type", nullable = true)
	@Enumerated(EnumType.STRING)
	private PostContentType tagRepsBatchContentType;

	@Column(name = "is_exposed")
	@ColumnDefault(value = "true")
	private Boolean isExposed;

	// @Ref 속성이 아닌 테이블로 구현
	// @JdbcTypeCode(SqlTypes.JSON)
	// private List<PostTag> relatedTagIds;
}
