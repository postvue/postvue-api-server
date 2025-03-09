package com.postvue.feelogserver.endpoint;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.postvue.feelogserver.domain.snsuserfollowstatistics.SnsUserFollowStatistic;
import com.postvue.feelogserver.domain.snsuserfollowstatistics.repository.SnsUserFollowStatisticRepository;
import com.postvue.feelogserver.endpoint.converter.JpaFilterCustomConverter;
import com.postvue.feelogserver.endpoint.dto.SnsUserFollowStatisticEndpointDto;
import com.vaadin.hilla.Nullable;
import com.vaadin.hilla.crud.filter.Filter;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SnsUserFollowStatisticEndpointService {
	private final SnsUserFollowStatisticRepository snsUserFollowStatisticRepository;
	private final JpaFilterCustomConverter jpaFilterCustomConverter;


	@Transactional
	public List<SnsUserFollowStatisticEndpointDto> listProcess(Pageable pageable, @Nullable Filter filter) {
		Specification<SnsUserFollowStatistic> spec = filter != null
			? jpaFilterCustomConverter.toSpec(filter, SnsUserFollowStatistic.class)
			: Specification.anyOf();
		return snsUserFollowStatisticRepository.findAll(spec,pageable).stream()
			.map((SnsUserFollowStatisticEndpointDto::fromEntity))
			.toList();
	}

	@Transactional
	public SnsUserFollowStatistic saveProcess(SnsUserFollowStatisticEndpointDto value) {
		SnsUserFollowStatistic snsTagFollow = value.id() != null && Long.parseLong(value.id()) > 0
			? snsUserFollowStatisticRepository.getReferenceById(Long.parseLong(value.id()))
			: new SnsUserFollowStatistic();

		return snsUserFollowStatisticRepository.save(snsTagFollow);
	}
}
