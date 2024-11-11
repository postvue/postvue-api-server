package com.postvue.feelogserver.domain.snstagposts;

import java.io.Serializable;

import com.postvue.feelogserver.core.config.SnowflakeId;
import com.postvue.feelogserver.domain.snsposts.SnsPost;
import com.postvue.feelogserver.domain.snstags.SnsTag;

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
@Setter
@Getter
@Table(name = "SNS_TAG_POSTS_TB", indexes = {
	@Index(name = "IDX__TAG_BY_SNS_TAG_POSTS", columnList = "sns_tag_id"),
	@Index(name = "IDX__POST_BY_SNS_TAG_POSTS", columnList = "sns_post_id")})
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SnsTagPost implements Serializable {
	@Id
	@SnowflakeId
	@Column(name = "sns_tag_post_id", updatable = false)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "sns_tag_id", updatable = false)
	private SnsTag snsTag;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "sns_post_id", updatable = false)
	private SnsPost snsPost;
}
