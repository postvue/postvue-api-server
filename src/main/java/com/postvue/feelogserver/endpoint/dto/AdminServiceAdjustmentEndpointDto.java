package com.postvue.feelogserver.endpoint.dto;

import java.time.LocalDateTime;

import com.postvue.feelogserver.domain.adminserviceadjustments.AdminServiceAdjustment;

public record AdminServiceAdjustmentEndpointDto(
	String id,
	String serviceType,
	String propLong1,

	String propLong2,

	String propLong3,

	String propLong4,

	String propString1,

	String propString2,

	String propString3,

	String propString4,
	LocalDateTime createdAt,

	LocalDateTime lastUpdatedAt,

	Long lastUpdatedBy
) {


	public static AdminServiceAdjustmentEndpointDto fromEntity(AdminServiceAdjustment adminServiceAdjustment){
		return new AdminServiceAdjustmentEndpointDto(
			adminServiceAdjustment.getId().toString(),
			adminServiceAdjustment.getServiceType(),
			adminServiceAdjustment.getPropLong1() != null ? adminServiceAdjustment.getPropLong1().toString() : null,
			adminServiceAdjustment.getPropLong2() != null ? adminServiceAdjustment.getPropLong2().toString() : null,
			adminServiceAdjustment.getPropLong3() != null ? adminServiceAdjustment.getPropLong3().toString() : null,
			adminServiceAdjustment.getPropLong4() != null ? adminServiceAdjustment.getPropLong4().toString() : null,
			adminServiceAdjustment.getPropString1(),
			adminServiceAdjustment.getPropString2(),
			adminServiceAdjustment.getPropString3(),
			adminServiceAdjustment.getPropString4(),
			adminServiceAdjustment.getCreatedAt(),
			adminServiceAdjustment.getLastUpdatedAt(),
			adminServiceAdjustment.getLastUpdatedBy()
		);
	}
}
