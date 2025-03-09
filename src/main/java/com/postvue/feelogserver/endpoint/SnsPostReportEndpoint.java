package com.postvue.feelogserver.endpoint;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import com.postvue.feelogserver.domain.snspostreports.repository.SnsPostReportRepository;
import com.postvue.feelogserver.endpoint.dto.SnsPostReportEndpointDto;
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
public class SnsPostReportEndpoint implements CrudService<SnsPostReportEndpointDto, Long> {
	private final SnsPostReportRepository snsPostReportRepository;
	private final SnsPostReportEndpointService snsPostReportEndpointService;

	@Override
	@Nonnull
	public List<@Nonnull SnsPostReportEndpointDto> list(Pageable pageable, @Nullable Filter filter) {
		try {
			return snsPostReportEndpointService.listProcess(pageable, filter);
		}
		catch (Exception e){
			throw new EndpointException("서버 오류 발생", LogTemplateConst.getLogInfoTemplate(e.getMessage() + "_" + e.toString(), LocalDateTime.now().toString()));
		}
	}


	@Override
	public @Nullable SnsPostReportEndpointDto save(SnsPostReportEndpointDto value) {
		try {
			return SnsPostReportEndpointDto.fromEntity(snsPostReportEndpointService.saveProcess(value));
		}
		catch (Exception e){
			throw new EndpointException("서버 오류 발생", LogTemplateConst.getLogInfoTemplate(e.getMessage() + "_" + e.toString(), LocalDateTime.now().toString()));
		}
	}

	@Override
	@Transactional
	public void delete(Long id) {
		snsPostReportRepository.deleteById(id);
	}
}
