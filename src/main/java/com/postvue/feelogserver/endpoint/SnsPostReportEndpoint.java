package com.postvue.feelogserver.endpoint;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import com.postvue.feelogserver.domain.snspostreports.SnsPostReport;
import com.postvue.feelogserver.domain.snspostreports.repository.SnsPostReportRepository;
import com.postvue.feelogserver.endpoint.converter.JpaFilterCustomConverter;
import com.postvue.feelogserver.endpoint.dto.SnsPostReportEndpointDto;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import com.vaadin.hilla.BrowserCallable;
import com.vaadin.hilla.Nonnull;
import com.vaadin.hilla.Nullable;
import com.vaadin.hilla.crud.CrudService;
import com.vaadin.hilla.crud.filter.Filter;

import lombok.RequiredArgsConstructor;

@BrowserCallable
@AnonymousAllowed
@RequiredArgsConstructor
public class SnsPostReportEndpoint implements CrudService<SnsPostReportEndpointDto, Long> {
	private final SnsPostReportRepository snsPostReportRepository;
	private final JpaFilterCustomConverter jpaFilterCustomConverter;

	@Override
	@Nonnull
	public List<@Nonnull SnsPostReportEndpointDto> list(Pageable pageable, @Nullable Filter filter) {
		Specification<SnsPostReport> spec = filter != null
			? jpaFilterCustomConverter.toSpec(filter, SnsPostReport.class)
			: Specification.anyOf();
		return snsPostReportRepository.findAll(spec,pageable).stream().map((SnsPostReportEndpointDto::fromEntity)).toList();
	}


	@Override
	public @Nullable SnsPostReportEndpointDto save(SnsPostReportEndpointDto value) {
		SnsPostReport snsBlockUser = value.id() != null && Long.parseLong(value.id()) > 0
			? snsPostReportRepository.getReferenceById(Long.parseLong(value.id()))
			: new SnsPostReport();
		return SnsPostReportEndpointDto.fromEntity(snsPostReportRepository.save(snsBlockUser));
	}

	@Override
	public void delete(Long id) {
		snsPostReportRepository.deleteById(id);
	}
}
