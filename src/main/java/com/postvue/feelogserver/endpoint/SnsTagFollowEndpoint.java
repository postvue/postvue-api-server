package com.postvue.feelogserver.endpoint;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.transaction.annotation.Transactional;

import com.postvue.feelogserver.domain.snsscrapboard.SnsScrapBoard;
import com.postvue.feelogserver.domain.snstagfollows.SnsTagFollow;
import com.postvue.feelogserver.domain.snstagfollows.repository.SnsTagFollowRepository;
import com.postvue.feelogserver.domain.snstags.SnsTag;
import com.postvue.feelogserver.endpoint.converter.JpaFilterCustomConverter;
import com.postvue.feelogserver.endpoint.dto.SnsTagFollowEndpointDto;
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
public class SnsTagFollowEndpoint implements CrudService<SnsTagFollowEndpointDto, Long> {
	private final SnsTagFollowRepository snsTagFollowRepository;
	private final JpaFilterCustomConverter jpaFilterCustomConverter;


	@Override
	@Nonnull
	public List<@Nonnull SnsTagFollowEndpointDto> list(Pageable pageable, @Nullable Filter filter) {
		Specification<SnsTagFollow> spec = filter != null
			? jpaFilterCustomConverter.toSpec(filter, SnsTagFollow.class)
			: Specification.anyOf();
		return snsTagFollowRepository.findAll(spec,pageable).map((SnsTagFollowEndpointDto::fromEntity)).toList();
	}

	@Override
	@Transactional
	public @Nullable SnsTagFollowEndpointDto save(SnsTagFollowEndpointDto value) {
		SnsTagFollow snsTagFollow = value.id() != null && Long.parseLong(value.id()) > 0
			? snsTagFollowRepository.getReferenceById(Long.parseLong(value.id()))
			: new SnsTagFollow();

		return SnsTagFollowEndpointDto.fromEntity(snsTagFollowRepository.save(snsTagFollow));
	}

	@Override
	@Transactional
	public void delete(Long id) {
		snsTagFollowRepository.deleteById(id);
	}
}
