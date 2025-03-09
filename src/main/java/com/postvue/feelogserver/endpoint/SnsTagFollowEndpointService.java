package com.postvue.feelogserver.endpoint;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.postvue.feelogserver.app.search.dto.req.PutFavoriteTerm;
import com.postvue.feelogserver.app.search.service.SearchService;
import com.postvue.feelogserver.domain.snstagfollows.SnsTagFollow;
import com.postvue.feelogserver.domain.snstagfollows.repository.SnsTagFollowRepository;
import com.postvue.feelogserver.domain.snsuserfavoritetermbookmarks.SnsUserFavoriteTermBookmark;
import com.postvue.feelogserver.domain.snsuserfavoritetermbookmarks.respository.SnsUserFavoriteTermBookmarkRepository;
import com.postvue.feelogserver.endpoint.converter.JpaFilterCustomConverter;
import com.postvue.feelogserver.endpoint.dto.SnsTagFollowEndpointDto;
import com.postvue.feelogserver.global.exception.BadRequestErrorException;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import com.vaadin.hilla.BrowserCallable;
import com.vaadin.hilla.Nonnull;
import com.vaadin.hilla.Nullable;
import com.vaadin.hilla.crud.CrudService;
import com.vaadin.hilla.crud.filter.Filter;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SnsTagFollowEndpointService {
	private final SnsTagFollowRepository snsTagFollowRepository;
	private final JpaFilterCustomConverter jpaFilterCustomConverter;
	private final SearchService searchService;


	@Transactional
	public List<SnsTagFollowEndpointDto> listProcess(Pageable pageable, @Nullable Filter filter) {
		Specification<SnsTagFollow> spec = filter != null
			? jpaFilterCustomConverter.toSpec(filter, SnsTagFollow.class)
			: Specification.anyOf();
		return snsTagFollowRepository.findAll(spec,pageable).map((SnsTagFollowEndpointDto::fromEntity)).toList();
	}

	@Transactional
	public @Nullable SnsTagFollowEndpointDto saveProcess(SnsTagFollowEndpointDto value) {
		SnsTagFollow snsTagFollow = value.id() != null && Long.parseLong(value.id()) > 0
			? snsTagFollowRepository.getReferenceById(Long.parseLong(value.id()))
			: new SnsTagFollow();

		return SnsTagFollowEndpointDto.fromEntity(snsTagFollowRepository.save(snsTagFollow));
	}

	@Transactional
	public void deleteProcess(Long id) {
		SnsTagFollow snsTagFollow = snsTagFollowRepository.findById(
			id
		).orElseThrow(
			() -> new BadRequestErrorException("해당 태그 팔로우는 없습니다.")
		);
		searchService.deleteFavoriteTerm(snsTagFollow.getSnsUser().getId(),
			"#" + snsTagFollow.getTagName());
	}
}
