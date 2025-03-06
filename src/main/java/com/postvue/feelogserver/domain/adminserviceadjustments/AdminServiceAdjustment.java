package com.postvue.feelogserver.domain.adminserviceadjustments;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.DynamicInsert;

import com.postvue.feelogserver.core.config.SnowflakeId;
import com.postvue.feelogserver.domain.snsusers.vo.SignUpType;
import com.postvue.feelogserver.domain.snsusers.vo.SnsAppRole;
import com.postvue.feelogserver.domain.snsusers.vo.SnsUserGender;
import com.postvue.feelogserver.domain.snsusers.vo.SnsUserState;
import com.postvue.feelogserver.global.common.tables.mixin.BaseMixinImpl;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "ADMIN_SERVICE_ADJUSTMENTS_TB")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminServiceAdjustment extends BaseMixinImpl implements Serializable {
	@Id
	@SnowflakeId
	@Column(name = "admin_service_adjustment_id", updatable = false)
	private Long id;

	@Column(name = "service_type")
	private String serviceType;

	@Column(name = "prop_long1")
	private Long propLong1id;

	@Column(name = "prop_long2")
	private Long propLong2id;

	@Column(name = "prop_long3")
	private Long propLong3id;

	@Column(name = "prop_long4")
	private Long propLong4id;

	@Column(name = "prop_string1", length = 512)
	private String propString1;

	@Column(name = "prop_string2", length = 512)
	private String propString2;

	@Column(name = "prop_string3", length = 512)
	private String propString3;

	@Column(name = "prop_string4", length = 512)
	private String propString4;
}

