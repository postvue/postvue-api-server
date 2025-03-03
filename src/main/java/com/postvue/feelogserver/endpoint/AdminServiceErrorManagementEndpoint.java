package com.postvue.feelogserver.endpoint;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.transaction.annotation.Transactional;

import com.postvue.feelogserver.core.config.SnowflakeComponent;
import com.postvue.feelogserver.domain.adminserviceadjustments.AdminServiceAdjustment;
import com.postvue.feelogserver.domain.adminserviceadjustments.repository.AdminServiceAdjustmentRepository;
import com.postvue.feelogserver.domain.adminserviceerrormanagements.AdminServiceErrorManagement;
import com.postvue.feelogserver.domain.adminserviceerrormanagements.repository.AdminServiceErrorManagementRepository;
import com.postvue.feelogserver.endpoint.converter.JpaFilterCustomConverter;
import com.postvue.feelogserver.endpoint.dto.AdminServiceAdjustmentEndpointDto;
import com.postvue.feelogserver.endpoint.dto.AdminServiceErrorManagementEndpointDto;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import com.vaadin.hilla.BrowserCallable;
import com.vaadin.hilla.Nonnull;
import com.vaadin.hilla.Nullable;
import com.vaadin.hilla.crud.CrudService;
import com.vaadin.hilla.crud.filter.Filter;

import lombok.RequiredArgsConstructor;

// @REFER: 조정 안됨 : 문자열은 안됨
@BrowserCallable
@AnonymousAllowed
@RequiredArgsConstructor
public class AdminServiceErrorManagementEndpoint implements CrudService<AdminServiceErrorManagementEndpointDto, Long> {
	private final AdminServiceErrorManagementRepository adminServiceErrorManagementRepository;
	private final JpaFilterCustomConverter jpaFilterCustomConverter;
	private final SnowflakeComponent snowflakeComponent;

	@Override
	@Nonnull
	@Transactional
	public List<@Nonnull AdminServiceErrorManagementEndpointDto> list(Pageable pageable, @Nullable Filter filter) {
		Specification<AdminServiceErrorManagement> spec = filter != null
			? jpaFilterCustomConverter.toSpec(filter, AdminServiceErrorManagement.class)
			: Specification.anyOf();
		return adminServiceErrorManagementRepository.findAll(spec, pageable)
			.map((AdminServiceErrorManagementEndpointDto::fromEntity))
			.toList();
	}

	@Override
	@Transactional
	public @Nullable AdminServiceErrorManagementEndpointDto save(AdminServiceErrorManagementEndpointDto value) {
		AdminServiceErrorManagement adminServiceErrorManagement = value.id() != null && Long.parseLong(value.id()) > 0
			? adminServiceErrorManagementRepository.getReferenceById(Long.parseLong(value.id()))
			: AdminServiceErrorManagement.builder().id(snowflakeComponent.nextId()).build();

		adminServiceErrorManagement.setServiceErrorType(value.serviceErrorType());
		adminServiceErrorManagement.setPropMsgString1(value.propMsgString1());
		adminServiceErrorManagement.setPropMsgString2(value.propMsgString2());
		adminServiceErrorManagement.setPropMsgString3(value.propMsgString3());
		adminServiceErrorManagement.setPropMsgString4(value.propMsgString4());

		return AdminServiceErrorManagementEndpointDto.fromEntity(adminServiceErrorManagementRepository.save(adminServiceErrorManagement));
	}


	@Override
	@Transactional
	public void delete(Long adminServiceErrorManageEndpoint) {
		adminServiceErrorManagementRepository.deleteById(adminServiceErrorManageEndpoint);
	}
}
