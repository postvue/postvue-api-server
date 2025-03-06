package com.postvue.feelogserver.endpoint;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.transaction.annotation.Transactional;

import com.postvue.feelogserver.domain.snsuserfollowstatistics.SnsUserFollowStatistic;
import com.postvue.feelogserver.domain.snsuserfollowstatistics.repository.SnsUserFollowStatisticRepository;
import com.postvue.feelogserver.endpoint.converter.JpaFilterCustomConverter;
import com.postvue.feelogserver.endpoint.dto.SnsUserFollowStatisticEndpointDto;
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
public class SnsUserFollowStatisticEndpoint implements CrudService<SnsUserFollowStatisticEndpointDto, Long> {
	private final SnsUserFollowStatisticRepository snsUserFollowStatisticRepository;
	private final SnsUserFollowStatisticEndpointService snsUserFollowStatisticEndpointService;


	@Override
	@Nonnull
	public List<@Nonnull SnsUserFollowStatisticEndpointDto> list(Pageable pageable, @Nullable Filter filter) {
		try {
			return snsUserFollowStatisticEndpointService.listProcess(pageable, filter);
		} catch (Exception e) {
			throw new EndpointException("서버 오류 발생",
				LogTemplateConst.getLogInfoTemplate(e.getMessage() + "_" + e.toString(),
					LocalDateTime.now().toString()));
		}
	}

	@Override
	public @Nullable SnsUserFollowStatisticEndpointDto save(SnsUserFollowStatisticEndpointDto value) {
		try{
			return SnsUserFollowStatisticEndpointDto.fromEntity(snsUserFollowStatisticEndpointService.saveProcess(value));
		} catch (Exception e) {
			throw new EndpointException("서버 오류 발생",
				LogTemplateConst.getLogInfoTemplate(e.getMessage() + "_" + e.toString(),
					LocalDateTime.now().toString()));
		}
	}

	@Override
	@Transactional
	public void delete(Long id) {
		snsUserFollowStatisticRepository.deleteById(id);
	}
}
