package com.postvue.feelogserver.domain.adminserviceerrormanagements;

import java.io.Serializable;

import com.postvue.feelogserver.core.config.SnowflakeId;
import com.postvue.feelogserver.global.common.tables.mixin.BaseMixinImpl;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
@Table(name = "ADMIN_SERVICE_ERROR_MANAGEMENTS_TB")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminServiceErrorManagement extends BaseMixinImpl implements Serializable {
	@Id
	@SnowflakeId
	@Column(name = "admin_service_error_management_id", updatable = false)
	private Long id;

	@Column(name = "service_error_type")
	private String serviceErrorType;

	@Column(name = "prop_msg_string1", length = 2047)
	private String propMsgString1;

	@Column(name = "prop_msg_string2", length = 2047)
	private String propMsgString2;

	@Column(name = "prop_msg_string3", length = 2047)
	private String propMsgString3;

	@Column(name = "prop_msg_string4", length = 2047)
	private String propMsgString4;
}

