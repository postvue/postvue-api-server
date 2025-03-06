package com.postvue.feelogserver.endpoint;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Pageable;

import org.springframework.transaction.annotation.Transactional;

import com.postvue.feelogserver.endpoint.dto.SnsPostEndPointDto;
import com.postvue.feelogserver.global.constant.LogTemplateConst;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import com.vaadin.hilla.BrowserCallable;
import com.vaadin.hilla.Nonnull;
import com.vaadin.hilla.Nullable;
import com.vaadin.hilla.crud.CrudService;
import com.vaadin.hilla.crud.filter.Filter;
import com.vaadin.hilla.exception.EndpointException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@BrowserCallable
@AnonymousAllowed
@RequiredArgsConstructor
public class SnsPostEndpoint implements CrudService<SnsPostEndPointDto, Long> {
	private final SnsPostEndpointService snsPostEndpointService;

	@Override
	@Nonnull
	public List<@Nonnull SnsPostEndPointDto> list(Pageable pageable, @Nullable Filter filter) {
		try{
			return snsPostEndpointService.listProcess(pageable,filter);
		}
			catch (Exception e){
			throw new EndpointException("서버 오류 발생", LogTemplateConst.getLogInfoTemplate(e.getMessage() + "_" + e.toString(), LocalDateTime.now().toString()));
		}
	}

	@Override
	public @Nullable SnsPostEndPointDto save(SnsPostEndPointDto value) {
		try{
			return SnsPostEndPointDto.fromEntity(snsPostEndpointService.saveProcess(value));
		}
		catch (Exception e){
			throw new EndpointException("서버 오류 발생", LogTemplateConst.getLogInfoTemplate(e.getMessage() + "_" + e.toString(), LocalDateTime.now().toString()));
		}
	}

	@Override
	@Transactional
	public void delete(Long id) {
		return;
	}
}
