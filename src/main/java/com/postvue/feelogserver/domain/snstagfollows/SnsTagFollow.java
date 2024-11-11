package com.postvue.feelogserver.domain.snstagfollows;

import java.io.Serializable;

import com.postvue.feelogserver.core.config.SnowflakeId;
import com.postvue.feelogserver.domain.snstags.SnsTag;
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
@Table(name = "SNS_TAG_FOLLOWS_TB", indexes = {
	@Index(name = "IDX__TAG_BY_SNS_TAG_FOLLOWS", columnList = "sns_tag_id")})
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SnsTagFollow extends BaseMixinImpl implements Serializable {
	@Id
	@SnowflakeId
	@Column(name = "sns_tag_follow_id",updatable = false)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "sns_user_id",updatable = false)
	private SnsUser snsUser;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "sns_tag_id",updatable = false)
	private SnsTag snsTag;

	@Column(name = "tag_name")
	private String tagName;
}
