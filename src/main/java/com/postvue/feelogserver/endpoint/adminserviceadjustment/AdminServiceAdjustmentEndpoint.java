package com.postvue.feelogserver.endpoint.adminserviceadjustment;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Pageable;
import com.postvue.feelogserver.domain.adminserviceadjustments.repository.AdminServiceAdjustmentRepository;
import com.postvue.feelogserver.endpoint.dto.AdminServiceAdjustmentEndpointDto;
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
public class AdminServiceAdjustmentEndpoint implements CrudService<AdminServiceAdjustmentEndpointDto, Long> {
	private final AdminServiceAdjustmentRepository adminServiceAdjustmentRepository;
	private final AdminServiceAdjustmentEndpointService adminServiceAdjustmentEndpointService;

	// @VERIFY1
	@Override
	@Nonnull
	public List<@Nonnull AdminServiceAdjustmentEndpointDto> list(Pageable pageable, @Nullable Filter filter) {
		try {
			return adminServiceAdjustmentEndpointService.listProcess(pageable,filter).stream()
				.map((AdminServiceAdjustmentEndpointDto::fromEntity))
				.toList();
		}
		catch (Exception e){
			throw new EndpointException("서버 오류 발생", LogTemplateConst.getLogInfoTemplate(e.getMessage() + "_" + e.toString(), LocalDateTime.now().toString()));
		}
	}

	// @VERIFY1
	@Override
	@Nonnull
	public @Nullable AdminServiceAdjustmentEndpointDto save(AdminServiceAdjustmentEndpointDto value) {
		try {
			return AdminServiceAdjustmentEndpointDto.fromEntity(adminServiceAdjustmentEndpointService.saveProcess(value));
		}
		catch (Exception e){
			throw new EndpointException("서버 오류 발생", LogTemplateConst.getLogInfoTemplate(e.getMessage() + "_" + e.toString(), LocalDateTime.now().toString()));
		}
	}

	// @VERIFY1
	@Override
	public void delete(Long adminServiceAdjustmentEndpoint) {
		adminServiceAdjustmentRepository.deleteById(adminServiceAdjustmentEndpoint);
	}
}
