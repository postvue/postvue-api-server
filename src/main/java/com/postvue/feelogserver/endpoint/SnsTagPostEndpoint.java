package com.postvue.feelogserver.endpoint;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.transaction.annotation.Transactional;

import com.postvue.feelogserver.domain.snstagfollows.SnsTagFollow;
import com.postvue.feelogserver.domain.snstagfollows.repository.SnsTagFollowRepository;
import com.postvue.feelogserver.domain.snstagposts.SnsTagPost;
import com.postvue.feelogserver.domain.snstagposts.respository.SnsTagPostRepository;
import com.postvue.feelogserver.endpoint.converter.JpaFilterCustomConverter;
import com.postvue.feelogserver.endpoint.dto.SnsTagFollowEndpointDto;
import com.postvue.feelogserver.endpoint.dto.SnsTagPostEndpointDto;
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
public class SnsTagPostEndpoint implements CrudService<SnsTagPostEndpointDto, Long> {
	private final SnsTagPostRepository snsTagPostRepository;
	private final JpaFilterCustomConverter jpaFilterCustomConverter;

	@Override
	@Nonnull
	@Transactional
	public List<@Nonnull SnsTagPostEndpointDto> list(Pageable pageable, @Nullable Filter filter) {
		Specification<SnsTagPost> spec = filter != null
			? jpaFilterCustomConverter.toSpec(filter, SnsTagPost.class)
			: Specification.anyOf();
		return snsTagPostRepository.findAll(spec,pageable).map((SnsTagPostEndpointDto::fromEntity)).toList();
	}

	@Override
	@Transactional
	public @Nullable SnsTagPostEndpointDto save(SnsTagPostEndpointDto value) {
		SnsTagPost snsTagPost = value.id() != null && Long.parseLong(value.id()) > 0
			? snsTagPostRepository.getReferenceById(Long.parseLong(value.id()))
			: new SnsTagPost();

		return SnsTagPostEndpointDto.fromEntity(snsTagPostRepository.save(snsTagPost));
	}

	@Override
	@Transactional
	public void delete(Long id) {
		snsTagPostRepository.deleteById(id);
	}
}
