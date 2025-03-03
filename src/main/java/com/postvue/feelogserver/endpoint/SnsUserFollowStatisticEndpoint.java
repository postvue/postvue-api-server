package com.postvue.feelogserver.endpoint;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.transaction.annotation.Transactional;

import com.postvue.feelogserver.domain.snsuserfollowstatistics.SnsUserFollowStatistic;
import com.postvue.feelogserver.domain.snsuserfollowstatistics.repository.SnsUserFollowStatisticRepository;
import com.postvue.feelogserver.endpoint.converter.JpaFilterCustomConverter;
import com.postvue.feelogserver.endpoint.dto.SnsUserFollowStatisticEndpointDto;
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
public class SnsUserFollowStatisticEndpoint implements CrudService<SnsUserFollowStatisticEndpointDto, Long> {
	private final SnsUserFollowStatisticRepository snsUserFollowStatisticRepository;
	private final JpaFilterCustomConverter jpaFilterCustomConverter;


	@Override
	@Nonnull
	@Transactional
	public List<@Nonnull SnsUserFollowStatisticEndpointDto> list(Pageable pageable, @Nullable Filter filter) {
		Specification<SnsUserFollowStatistic> spec = filter != null
			? jpaFilterCustomConverter.toSpec(filter, SnsUserFollowStatistic.class)
			: Specification.anyOf();
		return snsUserFollowStatisticRepository.findAll(spec,pageable).map((SnsUserFollowStatisticEndpointDto::fromEntity)).toList();
	}

	@Override
	@Transactional
	public @Nullable SnsUserFollowStatisticEndpointDto save(SnsUserFollowStatisticEndpointDto value) {
		SnsUserFollowStatistic snsTagFollow = value.id() != null && Long.parseLong(value.id()) > 0
			? snsUserFollowStatisticRepository.getReferenceById(Long.parseLong(value.id()))
			: new SnsUserFollowStatistic();

		return SnsUserFollowStatisticEndpointDto.fromEntity(snsUserFollowStatisticRepository.save(snsTagFollow));
	}

	@Override
	@Transactional
	public void delete(Long id) {
		snsUserFollowStatisticRepository.deleteById(id);
	}
}
