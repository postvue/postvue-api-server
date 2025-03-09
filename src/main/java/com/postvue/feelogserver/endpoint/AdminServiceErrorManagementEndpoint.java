package com.postvue.feelogserver.endpoint;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import com.postvue.feelogserver.domain.adminserviceerrormanagements.repository.AdminServiceErrorManagementRepository;
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

@BrowserCallable
@AnonymousAllowed
@RequiredArgsConstructor
public class AdminServiceErrorManagementEndpoint implements CrudService<AdminServiceErrorManagementEndpointDto, Long> {
	private final AdminServiceErrorManagementRepository adminServiceErrorManagementRepository;
	private final AdminServiceErrorManagementEndpointService adminServiceErrorManagementEndpointService;


	@Override
	@Nonnull
	public List<@Nonnull AdminServiceErrorManagementEndpointDto> list(Pageable pageable, @Nullable Filter filter) {
		try {
			return adminServiceErrorManagementEndpointService.listProcess(pageable,filter).stream()
				.map((AdminServiceErrorManagementEndpointDto::fromEntity))
				.toList();
			}
		catch (Exception e){
			throw new EndpointException("서버 오류 발생", LogTemplateConst.getLogInfoTemplate(e.getMessage() + "_" + e.toString(), LocalDateTime.now().toString()));
		}
	}

	// @VERIFY1
	@Override
	public @Nullable AdminServiceErrorManagementEndpointDto save(AdminServiceErrorManagementEndpointDto value) {
		try {
			return AdminServiceErrorManagementEndpointDto.fromEntity(adminServiceErrorManagementEndpointService.saveProcess(value));
		}
		catch (Exception e){
			throw new EndpointException("서버 오류 발생", LogTemplateConst.getLogInfoTemplate(e.getMessage() + "_" + e.toString(), LocalDateTime.now().toString()));
		}
	}


	// @VERIFY1
	@Override
	@Transactional
	public void delete(Long adminServiceErrorManageEndpoint) {
		adminServiceErrorManagementRepository.deleteById(adminServiceErrorManageEndpoint);
	}
}
