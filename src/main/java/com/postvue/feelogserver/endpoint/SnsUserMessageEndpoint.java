package com.postvue.feelogserver.endpoint;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import com.postvue.feelogserver.domain.snsusermessages.repository.SnsUserMessageRepository;
import com.postvue.feelogserver.endpoint.dto.SnsUserMessageEndpointDto;
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
public class SnsUserMessageEndpoint implements CrudService<SnsUserMessageEndpointDto, Long> {
	private final SnsUserMessageEndpointService snsUserMessageEndpointService;

	@Override
	@Nonnull
	public List<@Nonnull SnsUserMessageEndpointDto> list(Pageable pageable, @Nullable Filter filter) {
		try{
			return snsUserMessageEndpointService.listProcess(pageable,filter).stream().map((SnsUserMessageEndpointDto::fromEntity)).toList();
		} catch (Exception e) {
			throw new EndpointException("서버 오류 발생",
				LogTemplateConst.getLogInfoTemplate(e.getMessage() + "_" + e.toString(),
					LocalDateTime.now().toString()));
		}
	}

	@Override
	public @Nullable SnsUserMessageEndpointDto save(SnsUserMessageEndpointDto value) {
		try{
			return SnsUserMessageEndpointDto.fromEntity(snsUserMessageEndpointService.saveProcess(value));
		} catch (Exception e) {
			throw new EndpointException("서버 오류 발생",
				LogTemplateConst.getLogInfoTemplate(e.getMessage() + "_" + e.toString(),
					LocalDateTime.now().toString()));
		}
	}

	@Override
	public void delete(Long id) {
		try{
			snsUserMessageEndpointService.deleteProcess(id);
		} catch (Exception e) {
			throw new EndpointException("서버 오류 발생",
				LogTemplateConst.getLogInfoTemplate(e.getMessage() + "_" + e.toString(),
					LocalDateTime.now().toString()));
		}
	}
}
