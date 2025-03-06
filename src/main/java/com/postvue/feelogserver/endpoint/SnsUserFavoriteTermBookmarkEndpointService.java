package com.postvue.feelogserver.endpoint;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.transaction.annotation.Transactional;

import com.postvue.feelogserver.app.search.dto.req.PutFavoriteTerm;
import com.postvue.feelogserver.app.search.service.SearchService;
import com.postvue.feelogserver.domain.snstagfollows.SnsTagFollow;
import com.postvue.feelogserver.domain.snsuserfavoritetermbookmarks.SnsUserFavoriteTermBookmark;
import com.postvue.feelogserver.domain.snsuserfavoritetermbookmarks.respository.SnsUserFavoriteTermBookmarkRepository;
import com.postvue.feelogserver.domain.snsusers.SnsUser;
import com.postvue.feelogserver.endpoint.converter.JpaFilterCustomConverter;
import com.postvue.feelogserver.endpoint.dto.SnsUserFavoriteTermBookmarkEndpointDto;
import com.postvue.feelogserver.global.exception.BadRequestErrorException;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import com.vaadin.hilla.BrowserCallable;
import com.vaadin.hilla.Nullable;
import com.vaadin.hilla.crud.filter.Filter;

import lombok.RequiredArgsConstructor;

@BrowserCallable
@AnonymousAllowed
@RequiredArgsConstructor
public class SnsUserFavoriteTermBookmarkEndpointService {
	private final SnsUserFavoriteTermBookmarkRepository snsUserFavoriteTermBookmarkRepository;
	private final JpaFilterCustomConverter jpaFilterCustomConverter;
	private final SearchService searchService;

	@Transactional
	public List<SnsUserFavoriteTermBookmark> listProcess(Pageable pageable, @Nullable Filter filter) {
		Specification<SnsUserFavoriteTermBookmark> spec = filter != null
			? jpaFilterCustomConverter.toSpec(filter, SnsUserFavoriteTermBookmark.class)
			: Specification.anyOf();
		return snsUserFavoriteTermBookmarkRepository.findAll(spec,pageable).toList();
	}

	@Transactional
	public SnsUserFavoriteTermBookmark saveProcess(SnsUserFavoriteTermBookmarkEndpointDto value) {
		SnsUserFavoriteTermBookmark snsUserFavoriteTermBookmark = value.id() != null && Long.parseLong(value.id()) > 0
			? snsUserFavoriteTermBookmarkRepository.getReferenceById(Long.parseLong(value.id()))
			: new SnsUserFavoriteTermBookmark();

		if (snsUserFavoriteTermBookmark.getId() == null){
			Optional<SnsUserFavoriteTermBookmark> snsUserFavoriteTermBookmarkOpt = snsUserFavoriteTermBookmarkRepository.findByFavoriteTermNameAndSnsUser_id(value.favoriteTermName(),
				Long.valueOf(value.snsUser_id()));

			if (snsUserFavoriteTermBookmarkOpt.isPresent()) throw new RuntimeException("해당 검색어는 이미 찜했습니다.");

			searchService.createFavoriteTerm(Long.valueOf(value.snsUser_id()), new PutFavoriteTerm(
				true,
				value.favoriteTermName(), value.favoriteTermContent(),
				value.favoriteTermContentType().toString()));


			return snsUserFavoriteTermBookmarkRepository.findByFavoriteTermNameAndSnsUser_id(
				value.favoriteTermName(),
				Long.parseLong(value.snsUser_id())
			).orElseThrow(
				() -> new BadRequestErrorException("오류 발생으로 태그가 없음")
			);

		}
		else{
			// snsUserFavoriteTermBookmark.setSnsUser(SnsUser.builder().id(Long.parseLong(value.snsUser_id())).build());
			// snsUserFavoriteTermBookmark.setFavoriteTermName(value.favoriteTermName());
			// snsUserFavoriteTermBookmark.setSnsTagFollow(SnsTagFollow.builder().id(Long.parseLong(value.snsTagFollow_id())).build());
			snsUserFavoriteTermBookmark.setFavoriteTermContent(value.favoriteTermContent());
			snsUserFavoriteTermBookmark.setFavoriteTermContentType(value.favoriteTermContentType());

			return snsUserFavoriteTermBookmarkRepository.save(snsUserFavoriteTermBookmark);
		}
	}

	@Transactional
	public void deleteProcess(Long id) {
		SnsUserFavoriteTermBookmark snsUserFavoriteTermBookmark = snsUserFavoriteTermBookmarkRepository.findById(id).orElseThrow(
			() -> new BadRequestErrorException("해당 찜은 없습니다.")
		);

		searchService.deleteFavoriteTerm(snsUserFavoriteTermBookmark.getSnsUser().getId(),
			snsUserFavoriteTermBookmark.getFavoriteTermName());
	}

}
