package com.postvue.feelogserver.endpoint;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.transaction.annotation.Transactional;

import com.postvue.feelogserver.domain.snsuserreport.SnsUserReport;
import com.postvue.feelogserver.domain.snsuserreport.repository.SnsUserReportRepository;
import com.postvue.feelogserver.endpoint.converter.JpaFilterCustomConverter;
import com.postvue.feelogserver.endpoint.dto.SnsUserReportEndpointDto;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import com.vaadin.hilla.BrowserCallable;
import com.vaadin.hilla.Nullable;
import com.vaadin.hilla.crud.filter.Filter;

import lombok.RequiredArgsConstructor;

@BrowserCallable
@AnonymousAllowed
@RequiredArgsConstructor
public class SnsUserReportEndpointService {
	private final SnsUserReportRepository snsUserReportRepository;
	private final JpaFilterCustomConverter jpaFilterCustomConverter;


	@Transactional
	public List<SnsUserReport> listProcess(Pageable pageable, @Nullable Filter filter) {
		Specification<SnsUserReport> spec = filter != null
			? jpaFilterCustomConverter.toSpec(filter, SnsUserReport.class)
			: Specification.anyOf();

		return snsUserReportRepository.findAll(spec,pageable).toList();
	}


	@Transactional
	public SnsUserReport saveProcess(SnsUserReportEndpointDto value) {
		SnsUserReport snsUserReport = value.id() != null && Long.parseLong(value.id()) > 0
			? snsUserReportRepository.getReferenceById(Long.parseLong(value.id()))
			: new SnsUserReport();

		snsUserReport.setCreatedAt(value.createdAt());
		snsUserReport.setLastUpdatedAt(value.lastUpdatedAt());
		snsUserReport.setUserReportStatus(value.userReportStatus());

		return snsUserReportRepository.save(snsUserReport);
	}
}
