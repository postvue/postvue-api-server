package com.postvue.feelogserver.endpoint;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import com.postvue.feelogserver.domain.snsuserreport.repository.SnsUserReportRepository;
import com.postvue.feelogserver.endpoint.dto.SnsUserReportEndpointDto;
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
public class SnsUserReportEndpoint implements CrudService<SnsUserReportEndpointDto, Long> {
	private final SnsUserReportRepository snsUserReportRepository;
	private final SnsUserReportEndpointService snsUserReportEndpointService;

	@Override
	@Nonnull
	public List<@Nonnull SnsUserReportEndpointDto> list(Pageable pageable, @Nullable Filter filter) {
		try{
			return snsUserReportEndpointService.listProcess(pageable,filter).stream().map((SnsUserReportEndpointDto::fromEntity)).toList();
		}
		catch (Exception e){
			throw new EndpointException("서버 오류 발생", LogTemplateConst.getLogInfoTemplate(e.getMessage() + "_" + e.toString(), LocalDateTime.now().toString()));
		}
	}


	@Override
	public @Nullable SnsUserReportEndpointDto save(SnsUserReportEndpointDto value) {
		try{
			return SnsUserReportEndpointDto.fromEntity(snsUserReportEndpointService.saveProcess(value));
		}
		catch (Exception e){
			throw new EndpointException("서버 오류 발생", LogTemplateConst.getLogInfoTemplate(e.getMessage() + "_" + e.toString(), LocalDateTime.now().toString()));
		}
	}

	@Override
	@Transactional
	public void delete(Long id) {
		snsUserReportRepository.deleteById(id);
	}
}
