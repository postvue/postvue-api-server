package com.postvue.feelogserver.endpoint.dto;

import java.time.LocalDateTime;

import com.postvue.feelogserver.domain.adminserviceadjustments.AdminServiceAdjustment;
import com.postvue.feelogserver.domain.adminserviceerrormanagements.AdminServiceErrorManagement;

public record AdminServiceErrorManagementEndpointDto(
	String id,
	String serviceErrorType,
	String propMsgString1,

	String propMsgString2,

	String propMsgString3,

	String propMsgString4,
	LocalDateTime createdAt,

	LocalDateTime lastUpdatedAt,

	String lastUpdatedByid
) {


	public static AdminServiceErrorManagementEndpointDto fromEntity(AdminServiceErrorManagement adminServiceErrorManagement){
		return new AdminServiceErrorManagementEndpointDto(
			adminServiceErrorManagement.getId().toString(),
			adminServiceErrorManagement.getServiceErrorType(),
			adminServiceErrorManagement.getPropMsgString1(),
			adminServiceErrorManagement.getPropMsgString2(),
			adminServiceErrorManagement.getPropMsgString3(),
			adminServiceErrorManagement.getPropMsgString4(),
			adminServiceErrorManagement.getCreatedAt(),
			adminServiceErrorManagement.getLastUpdatedAt(),
			adminServiceErrorManagement.getLastUpdatedByid() != null ? adminServiceErrorManagement.getLastUpdatedByid().toString() : null
		);
	}
}
