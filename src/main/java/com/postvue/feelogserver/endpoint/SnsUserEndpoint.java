package com.postvue.feelogserver.endpoint;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;

import com.postvue.feelogserver.endpoint.dto.SnsUserEndpointDto;
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
@Controller
@BrowserCallable
@AnonymousAllowed
@RequiredArgsConstructor
public class SnsUserEndpoint implements CrudService<SnsUserEndpointDto, Long> {
	private final SnsUserEndpointService snsUserEndpointService;

	@Override
	@Nonnull
	public List<@Nonnull SnsUserEndpointDto> list(Pageable pageable, @Nullable Filter filter) {
		try {
			return snsUserEndpointService.listProcess(pageable, filter).stream().map((SnsUserEndpointDto::fromEntity)).toList();
		}
		catch (Exception e){
			throw new EndpointException("서버 오류 발생", LogTemplateConst.getLogInfoTemplate(e.getMessage() + "_" + e.toString(), LocalDateTime.now().toString()));
		}
	}

	@Override
	public @Nullable SnsUserEndpointDto save(SnsUserEndpointDto value) {
		try {
			return SnsUserEndpointDto.fromEntity(snsUserEndpointService.saveProcess(value));
		}
		catch (Exception e){
			log.error(e.getMessage());
			throw new EndpointException("서버 오류 발생", LogTemplateConst.getLogInfoTemplate(e.getMessage() + "_" + e.toString(), LocalDateTime.now().toString()));
		}
	}

	@Override
	@Transactional
	public void delete(Long snsUserId) {
		return;
	}
}
