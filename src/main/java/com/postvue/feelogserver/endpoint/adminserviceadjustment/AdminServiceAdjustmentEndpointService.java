package com.postvue.feelogserver.endpoint.adminserviceadjustment;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.postvue.feelogserver.core.config.SnowflakeComponent;
import com.postvue.feelogserver.domain.adminserviceadjustments.AdminServiceAdjustment;
import com.postvue.feelogserver.domain.adminserviceadjustments.repository.AdminServiceAdjustmentRepository;
import com.postvue.feelogserver.endpoint.converter.JpaFilterCustomConverter;
import com.postvue.feelogserver.endpoint.dto.AdminServiceAdjustmentEndpointDto;
import com.postvue.feelogserver.global.util.validation.StringValidUtil;
import com.vaadin.hilla.Nullable;
import com.vaadin.hilla.crud.filter.Filter;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AdminServiceAdjustmentEndpointService{
	private final AdminServiceAdjustmentRepository adminServiceAdjustmentRepository;
	private final SnowflakeComponent snowflakeComponent;
	private final JpaFilterCustomConverter jpaFilterCustomConverter;

	// @VERIFY1
	@Transactional
	public List<AdminServiceAdjustment> listProcess(Pageable pageable, @Nullable Filter filter) {
		Specification<AdminServiceAdjustment> spec = filter != null
			? jpaFilterCustomConverter.toSpec(filter, AdminServiceAdjustment.class)
			: Specification.anyOf();
		return adminServiceAdjustmentRepository.findAll(spec, pageable).toList();
	}

	// @VERIFY1
	@Transactional
	public AdminServiceAdjustment saveProcess(AdminServiceAdjustmentEndpointDto value) {
		AdminServiceAdjustment adminServiceAdjustment = value.id() != null && Long.parseLong(value.id()) > 0
			? adminServiceAdjustmentRepository.getReferenceById(Long.parseLong(value.id()))
			: AdminServiceAdjustment.builder().id(snowflakeComponent.nextId()).build();

		adminServiceAdjustment.setServiceType(value.serviceType());
		adminServiceAdjustment.setPropLong1id(StringValidUtil.isNotBlank(value.propLong1id()) ? Long.parseLong(value.propLong1id()) : null);
		adminServiceAdjustment.setPropLong2id(StringValidUtil.isNotBlank(value.propLong2id()) ? Long.parseLong(value.propLong2id()) : null);
		adminServiceAdjustment.setPropLong3id(StringValidUtil.isNotBlank(value.propLong3id()) ? Long.parseLong(value.propLong3id()) : null);
		adminServiceAdjustment.setPropLong4id(StringValidUtil.isNotBlank(value.propLong4id()) ? Long.parseLong(value.propLong4id()) : null);
		adminServiceAdjustment.setPropString1(StringValidUtil.nullIfEmpty(value.propString1()));
		adminServiceAdjustment.setPropString2(StringValidUtil.nullIfEmpty(value.propString2()));
		adminServiceAdjustment.setPropString3(StringValidUtil.nullIfEmpty(value.propString3()));
		adminServiceAdjustment.setPropString4(StringValidUtil.nullIfEmpty(value.propString4()));

		return adminServiceAdjustmentRepository.save(adminServiceAdjustment);
	}
}
