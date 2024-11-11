package com.postvue.feelogserver.domain.snsscrap;

import java.io.Serializable;

import org.hibernate.annotations.DynamicInsert;

import com.postvue.feelogserver.core.config.SnowflakeId;
import com.postvue.feelogserver.domain.snsposts.SnsPost;
import com.postvue.feelogserver.domain.snsscrapboard.SnsScrapBoard;
import com.postvue.feelogserver.domain.snsusers.SnsUser;
import com.postvue.feelogserver.global.common.tables.mixin.BaseMixinImpl;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
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
@Table(name = "SNS_SCRAPS_TB",
	uniqueConstraints = {
		@UniqueConstraint(columnNames = {"sns_scrap_board_id", "sns_user_id", "sns_post_id"})
	})
@Builder
@NoArgsConstructor
@AllArgsConstructor
@DynamicInsert
public class SnsScrap extends BaseMixinImpl implements Serializable {
	@Id
	@SnowflakeId
	@Column(name = "sns_scrap_id",updatable = false)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "sns_scrap_board_id")
	private SnsScrapBoard snsScrapBoard;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "sns_user_id")
	private SnsUser snsUser;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "sns_post_id")
	private SnsPost snsPost;
}
