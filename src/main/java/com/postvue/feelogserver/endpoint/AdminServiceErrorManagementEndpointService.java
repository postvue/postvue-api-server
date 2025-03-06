package com.postvue.feelogserver.endpoint;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import com.postvue.feelogserver.core.config.SnowflakeComponent;
import com.postvue.feelogserver.domain.adminserviceerrormanagements.AdminServiceErrorManagement;
import com.postvue.feelogserver.domain.adminserviceerrormanagements.repository.AdminServiceErrorManagementRepository;
import com.postvue.feelogserver.endpoint.converter.JpaFilterCustomConverter;
import com.postvue.feelogserver.endpoint.dto.AdminServiceErrorManagementEndpointDto;
import com.postvue.feelogserver.global.constant.LogTemplateConst;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import com.vaadin.hilla.BrowserCallable;
import com.vaadin.hilla.Nonnull;
import com.vaadin.hilla.Nullable;
import com.vaadin.hilla.crud.CrudService;
import com.vaadin.hilla.crud.filter.Filter;
import com.vaadin.hilla.exception.EndpointException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AdminServiceErrorManagementEndpointService {
	private final AdminServiceErrorManagementRepository adminServiceErrorManagementRepository;
	private final SnowflakeComponent snowflakeComponent;
	private final JpaFilterCustomConverter jpaFilterCustomConverter;

	@Transactional
	public List<AdminServiceErrorManagement> listProcess(Pageable pageable, @Nullable Filter filter) {
		Specification<AdminServiceErrorManagement> spec = filter != null
			? jpaFilterCustomConverter.toSpec(filter, AdminServiceErrorManagement.class)
			: Specification.anyOf();
		return adminServiceErrorManagementRepository.findAll(spec, pageable).toList();
	}


	@Transactional
	public AdminServiceErrorManagement saveProcess(AdminServiceErrorManagementEndpointDto value) {
		AdminServiceErrorManagement adminServiceErrorManagement = value.id() != null && Long.parseLong(value.id()) > 0
			? adminServiceErrorManagementRepository.getReferenceById(Long.parseLong(value.id()))
			: AdminServiceErrorManagement.builder().id(snowflakeComponent.nextId()).build();

		adminServiceErrorManagement.setServiceErrorType(value.serviceErrorType());
		adminServiceErrorManagement.setPropMsgString1(value.propMsgString1());
		adminServiceErrorManagement.setPropMsgString2(value.propMsgString2());
		adminServiceErrorManagement.setPropMsgString3(value.propMsgString3());
		adminServiceErrorManagement.setPropMsgString4(value.propMsgString4());

		return adminServiceErrorManagementRepository.save(adminServiceErrorManagement);
	}
}
