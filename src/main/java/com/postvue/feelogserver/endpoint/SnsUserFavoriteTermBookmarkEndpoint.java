package com.postvue.feelogserver.endpoint;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.transaction.annotation.Transactional;

import com.postvue.feelogserver.domain.snstagfollows.SnsTagFollow;
import com.postvue.feelogserver.domain.snsuserfavoritetermbookmarks.SnsUserFavoriteTermBookmark;
import com.postvue.feelogserver.domain.snsuserfavoritetermbookmarks.respository.SnsUserFavoriteTermBookmarkRepository;
import com.postvue.feelogserver.domain.snsusers.SnsUser;
import com.postvue.feelogserver.endpoint.converter.JpaFilterCustomConverter;
import com.postvue.feelogserver.endpoint.dto.SnsUserFavoriteTermBookmarkEndpointDto;
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
public class SnsUserFavoriteTermBookmarkEndpoint implements CrudService<SnsUserFavoriteTermBookmarkEndpointDto, Long> {
	private final SnsUserFavoriteTermBookmarkRepository snsUserFavoriteTermBookmarkRepository;
	private final JpaFilterCustomConverter jpaFilterCustomConverter;

	@Override
	@Nonnull
	@Transactional
	public List<@Nonnull SnsUserFavoriteTermBookmarkEndpointDto> list(Pageable pageable, @Nullable Filter filter) {
		Specification<SnsUserFavoriteTermBookmark> spec = filter != null
			? jpaFilterCustomConverter.toSpec(filter, SnsUserFavoriteTermBookmark.class)
			: Specification.anyOf();
		return snsUserFavoriteTermBookmarkRepository.findAll(spec,pageable).map((SnsUserFavoriteTermBookmarkEndpointDto::fromEntity)).toList();
	}

	@Override
	@Transactional
	public @Nullable SnsUserFavoriteTermBookmarkEndpointDto save(SnsUserFavoriteTermBookmarkEndpointDto value) {
		SnsUserFavoriteTermBookmark snsUserFavoriteTermBookmark = value.id() != null && Long.parseLong(value.id()) > 0
			? snsUserFavoriteTermBookmarkRepository.getReferenceById(Long.parseLong(value.id()))
			: new SnsUserFavoriteTermBookmark();

		snsUserFavoriteTermBookmark.setSnsUser(SnsUser.builder().id(Long.parseLong(value.snsUser_id())).build());
		snsUserFavoriteTermBookmark.setFavoriteTermName(value.favoriteTermName());
		snsUserFavoriteTermBookmark.setFavoriteTermContent(value.favoriteTermContent());
		snsUserFavoriteTermBookmark.setFavoriteTermContentType(value.favoriteTermContentType());
		snsUserFavoriteTermBookmark.setSnsTagFollow(SnsTagFollow.builder().id(Long.parseLong(value.snsTagFollow_id())).build());

		return SnsUserFavoriteTermBookmarkEndpointDto.fromEntity(snsUserFavoriteTermBookmarkRepository.save(snsUserFavoriteTermBookmark));
	}

	@Override
	@Transactional
	public void delete(Long id) {
		snsUserFavoriteTermBookmarkRepository.deleteById(id);
	}
}
