package com.postvue.feelogserver.endpoint.dto;

import java.time.LocalDateTime;

import com.postvue.feelogserver.domain.adminserviceadjustments.AdminServiceAdjustment;

public record AdminServiceAdjustmentEndpointDto(
	String id,
	String serviceType,
	String propLong1id,

	String propLong2id,

	String propLong3id,

	String propLong4id,

	String propString1,

	String propString2,

	String propString3,

	String propString4,
	LocalDateTime createdAt,

	LocalDateTime lastUpdatedAt,

	String lastUpdatedByid
) {


	public static AdminServiceAdjustmentEndpointDto fromEntity(AdminServiceAdjustment adminServiceAdjustment){
		return new AdminServiceAdjustmentEndpointDto(
			adminServiceAdjustment.getId().toString(),
			adminServiceAdjustment.getServiceType(),
			adminServiceAdjustment.getPropLong1id() != null ? adminServiceAdjustment.getPropLong1id().toString() : null,
			adminServiceAdjustment.getPropLong2id() != null ? adminServiceAdjustment.getPropLong2id().toString() : null,
			adminServiceAdjustment.getPropLong3id() != null ? adminServiceAdjustment.getPropLong3id().toString() : null,
			adminServiceAdjustment.getPropLong4id() != null ? adminServiceAdjustment.getPropLong4id().toString() : null,
			adminServiceAdjustment.getPropString1(),
			adminServiceAdjustment.getPropString2(),
			adminServiceAdjustment.getPropString3(),
			adminServiceAdjustment.getPropString4(),
			adminServiceAdjustment.getCreatedAt(),
			adminServiceAdjustment.getLastUpdatedAt(),
			adminServiceAdjustment.getLastUpdatedByid() !=null ? adminServiceAdjustment.getLastUpdatedByid().toString() : null
		);
	}
}
