package com.postvue.feelogserver.endpoint;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.transaction.annotation.Transactional;

import com.postvue.feelogserver.core.config.SnowflakeComponent;
import com.postvue.feelogserver.domain.adminserviceadjustments.AdminServiceAdjustment;
import com.postvue.feelogserver.domain.adminserviceadjustments.repository.AdminServiceAdjustmentRepository;
import com.postvue.feelogserver.endpoint.converter.JpaFilterCustomConverter;
import com.postvue.feelogserver.endpoint.dto.AdminServiceAdjustmentEndpointDto;
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
public class AdminServiceAdjustmentEndpoint implements CrudService<AdminServiceAdjustmentEndpointDto, Long> {
	private final AdminServiceAdjustmentRepository adminServiceAdjustmentRepository;
	private final JpaFilterCustomConverter jpaFilterCustomConverter;
	private final SnowflakeComponent snowflakeComponent;

	@Override
	@Nonnull
	public List<@Nonnull AdminServiceAdjustmentEndpointDto> list(Pageable pageable, @Nullable Filter filter) {
		Specification<AdminServiceAdjustment> spec = filter != null
			? jpaFilterCustomConverter.toSpec(filter, AdminServiceAdjustment.class)
			: Specification.anyOf();
		return adminServiceAdjustmentRepository.findAll(spec,pageable).map((AdminServiceAdjustmentEndpointDto::fromEntity)).toList();
	}

	@Override
	@Transactional
	public @Nullable AdminServiceAdjustmentEndpointDto save(AdminServiceAdjustmentEndpointDto value) {
		AdminServiceAdjustment adminServiceAdjustment = value.id() != null && Long.parseLong(value.id()) > 0
			? adminServiceAdjustmentRepository.getReferenceById(Long.parseLong(value.id()))
			: AdminServiceAdjustment.builder().id(snowflakeComponent.nextId()).build();

		adminServiceAdjustment.setServiceType(value.serviceType());
		adminServiceAdjustment.setPropLong1(value.propLong1());
		adminServiceAdjustment.setPropLong2(value.propLong2());
		adminServiceAdjustment.setPropLong3(value.propLong3());
		adminServiceAdjustment.setPropLong4(value.propLong4());
		adminServiceAdjustment.setPropString1(value.propString1());
		adminServiceAdjustment.setPropString1(value.propString2());
		adminServiceAdjustment.setPropString1(value.propString3());
		adminServiceAdjustment.setPropString1(value.propString4());


		return AdminServiceAdjustmentEndpointDto.fromEntity(adminServiceAdjustmentRepository.save(adminServiceAdjustment));
	}

	@Override
	@Transactional
	public void delete(Long adminServiceAdjustmentEndpoint) {
		adminServiceAdjustmentRepository.deleteById(adminServiceAdjustmentEndpoint);
	}
}
