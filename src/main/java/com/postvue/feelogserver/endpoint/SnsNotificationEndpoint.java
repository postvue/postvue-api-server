package com.postvue.feelogserver.endpoint;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;


import com.postvue.feelogserver.domain.snsnotifications.repository.SnsNotificationRepository;
import com.postvue.feelogserver.endpoint.dto.SnsNotificationEndpointDto;
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
public class SnsNotificationEndpoint implements CrudService<SnsNotificationEndpointDto, Long> {
	private final SnsNotificationRepository snsNotificationRepository;
	private final SnsNotificationEndpointService snsNotificationEndpointService;

	@Override
	@Nonnull
	public List<@Nonnull SnsNotificationEndpointDto> list(Pageable pageable, @Nullable Filter filter) {
		try {
			return snsNotificationEndpointService.listProcess(pageable,filter);
		}
		catch (Exception e){
			throw new EndpointException("서버 오류 발생", LogTemplateConst.getLogInfoTemplate(e.getMessage() + "_" + e.toString(), LocalDateTime.now().toString()));
		}
	}

	@Override
	@Nonnull
	public @Nullable SnsNotificationEndpointDto save(SnsNotificationEndpointDto value) {
		try {
			return SnsNotificationEndpointDto.fromEntity(snsNotificationEndpointService.saveProcess(value));
		}
		catch (Exception e){
			throw new EndpointException("서버 오류 발생", LogTemplateConst.getLogInfoTemplate(e.getMessage() + "_" + e.toString(), LocalDateTime.now().toString()));
		}
	}

	@Override
	@Transactional
	public void delete(Long id) {
		snsNotificationRepository.deleteById(id);
	}
}
