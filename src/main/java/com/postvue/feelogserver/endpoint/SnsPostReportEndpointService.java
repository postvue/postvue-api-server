package com.postvue.feelogserver.endpoint;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.postvue.feelogserver.domain.snspostreports.SnsPostReport;
import com.postvue.feelogserver.domain.snspostreports.repository.SnsPostReportRepository;
import com.postvue.feelogserver.endpoint.converter.JpaFilterCustomConverter;
import com.postvue.feelogserver.endpoint.dto.SnsPostReportEndpointDto;
import com.vaadin.hilla.Nullable;
import com.vaadin.hilla.crud.filter.Filter;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SnsPostReportEndpointService {
	private final SnsPostReportRepository snsPostReportRepository;
	private final JpaFilterCustomConverter jpaFilterCustomConverter;

	@Transactional
	public List<SnsPostReportEndpointDto> listProcess(Pageable pageable, @Nullable Filter filter) {
		Specification<SnsPostReport> spec = filter != null
			? jpaFilterCustomConverter.toSpec(filter, SnsPostReport.class)
			: Specification.anyOf();

		return snsPostReportRepository.findAll(spec,pageable).map((SnsPostReportEndpointDto::fromEntity)).toList();
	}


	@Transactional
	public SnsPostReport saveProcess(SnsPostReportEndpointDto value) {
		SnsPostReport snsPostReport = value.id() != null && Long.parseLong(value.id()) > 0
			? snsPostReportRepository.getReferenceById(Long.parseLong(value.id()))
			: new SnsPostReport();

		snsPostReport.setCreatedAt(value.createdAt());
		snsPostReport.setLastUpdatedAt(value.lastUpdatedAt());
		snsPostReport.setPostReportStatus(value.postReportStatus());

		return snsPostReportRepository.save(snsPostReport);
	}
}
