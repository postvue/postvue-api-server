package com.postvue.feelogserver.domain.snsscrapboard;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.SQLRestriction;
import org.hibernate.annotations.Where;

import com.postvue.feelogserver.core.config.SnowflakeId;
import com.postvue.feelogserver.domain.snsscrap.SnsScrap;
import com.postvue.feelogserver.domain.snsscrapboard.vo.ScrapTargetAudience;
import com.postvue.feelogserver.domain.snsusers.SnsUser;
import com.postvue.feelogserver.global.common.tables.mixin.BaseMixinImpl;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
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
@Table(name = "SNS_SCRAP_BOARDS_TB")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@DynamicInsert
public class SnsScrapBoard extends BaseMixinImpl implements Serializable {
	@Id
	@SnowflakeId
	@Column(name = "sns_scrap_board_id",updatable = false)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "sns_user_id",updatable = false)
	private SnsUser snsUser;

	@Column(name = "scrap_name", nullable = false)
	private String scrapName;

	@Column(name = "target_audience", nullable = false)
	@Enumerated(EnumType.STRING)
	private ScrapTargetAudience targetAudience;

	@OneToMany(mappedBy = "snsScrapBoard", fetch = FetchType.LAZY)
	private List<SnsScrap> snsScraps;

	private Float latitude;
	private Float longitude;
	private String address;

	@Column(name = "deleted_at")
	private LocalDateTime deletedAt;
}
