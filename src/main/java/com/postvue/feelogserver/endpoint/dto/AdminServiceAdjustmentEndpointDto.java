package com.postvue.feelogserver.endpoint.dto;

import java.time.LocalDateTime;

import com.postvue.feelogserver.domain.adminserviceadjustments.AdminServiceAdjustment;

public record AdminServiceAdjustmentEndpointDto(
	String id,
	String serviceType,
	Long propLong1,

	Long propLong2,

	Long propLong3,

	Long propLong4,

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
			adminServiceAdjustment.getPropLong1(),
			adminServiceAdjustment.getPropLong2(),
			adminServiceAdjustment.getPropLong3(),
			adminServiceAdjustment.getPropLong4(),
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
