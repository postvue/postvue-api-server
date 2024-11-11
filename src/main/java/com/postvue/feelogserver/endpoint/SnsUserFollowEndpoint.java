package com.postvue.feelogserver.endpoint;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import com.postvue.feelogserver.domain.snstagposts.SnsTagPost;
import com.postvue.feelogserver.domain.snstagposts.respository.SnsTagPostRepository;
import com.postvue.feelogserver.domain.snsuserfavoritetermbookmarks.SnsUserFavoriteTermBookmark;
import com.postvue.feelogserver.domain.snsuserfollows.SnsUserFollow;
import com.postvue.feelogserver.domain.snsuserfollows.repository.SnsUserFollowRepository;
import com.postvue.feelogserver.endpoint.converter.JpaFilterCustomConverter;
import com.postvue.feelogserver.endpoint.dto.SnsTagPostEndpointDto;
import com.postvue.feelogserver.endpoint.dto.SnsUserFollowEndpointDto;
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
public class SnsUserFollowEndpoint implements CrudService<SnsUserFollowEndpointDto, Long> {
	private final SnsUserFollowRepository snsUserFollowRepository;
	private final JpaFilterCustomConverter jpaFilterCustomConverter;

	@Override
	@Nonnull
	public List<@Nonnull SnsUserFollowEndpointDto> list(Pageable pageable, @Nullable Filter filter) {
		Specification<SnsUserFollow> spec = filter != null
			? jpaFilterCustomConverter.toSpec(filter, SnsUserFollow.class)
			: Specification.anyOf();
		return snsUserFollowRepository.findAll(spec,pageable).map((SnsUserFollowEndpointDto::fromEntity)).toList();
	}

	@Override
	public @Nullable SnsUserFollowEndpointDto save(SnsUserFollowEndpointDto value) {
		SnsUserFollow snsUserFollow = value.id() != null && Long.parseLong(value.id()) > 0
			? snsUserFollowRepository.getReferenceById(Long.parseLong(value.id()))
			: new SnsUserFollow();

		return SnsUserFollowEndpointDto.fromEntity(snsUserFollowRepository.save(snsUserFollow));
	}

	@Override
	public void delete(Long id) {
		snsUserFollowRepository.deleteById(id);
	}
}
