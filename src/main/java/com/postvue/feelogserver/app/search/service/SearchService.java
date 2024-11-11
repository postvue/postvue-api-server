package com.postvue.feelogserver.app.search.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.postvue.feelogserver.app.search.dao.SearchQueryDao;
import com.postvue.feelogserver.app.search.dto.req.PutFavoriteTerm;
import com.postvue.feelogserver.app.search.dto.rsp.GetFavoriteTermRsp;
import com.postvue.feelogserver.app.search.dto.rsp.GetTagInfoSearchRsp;
import com.postvue.feelogserver.domain.snsposts.vo.PostContentType;
import com.postvue.feelogserver.domain.snstagfollows.repository.SnsTagFollowRepository;
import com.postvue.feelogserver.domain.snstags.repository.SnsTagRepository;
import com.postvue.feelogserver.domain.snsuserfavoritetermbookmarks.SnsUserFavoriteTermBookmark;
import com.postvue.feelogserver.domain.snsuserfavoritetermbookmarks.respository.SnsUserFavoriteTermBookmarkRepository;
import com.postvue.feelogserver.domain.snsusers.SnsUser;
import com.postvue.feelogserver.global.constant.PageConfigConst;
import com.postvue.feelogserver.global.exception.BadRequestErrorException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SearchService {
	private final SnsTagRepository snsTagRepository;
	private final SnsTagFollowRepository snsTagFollowRepository;
	private final SnsUserFavoriteTermBookmarkRepository snsUserFavoriteTermBookmarkRepository;

	@Transactional
	public List<String> getSearchRelationList(String searchQuery) {
		return snsTagRepository.findAllBySearchQuery(searchQuery)
			.stream()
			.map((searchQueryDao -> searchQueryDao.getSearchQueryName()))
			.toList();
	}

	@Transactional
	public List<String> getTagSearchList(String searchQuery) {
		return snsTagRepository.findAllByTagSearch(searchQuery, PageConfigConst.TAG_SEARCH_PAGE_SIZE)
			.stream()
			.map((SearchQueryDao::getSearchQueryName))
			.toList();
	}

	@Transactional
	public List<GetTagInfoSearchRsp> getTagInfoSearchList(String searchQuery, Integer page) {
		return snsTagRepository.findAllByTagInfoSearchPageable(searchQuery, page * PageConfigConst.TAG_SEARCH_PAGE_SIZE,
				PageConfigConst.TAG_SEARCH_PAGE_SIZE)
			.stream()
			.map((searchTagInfoQueryDao -> GetTagInfoSearchRsp.builder()
				.tagName(searchTagInfoQueryDao.getSearchQueryName())
				.tagBkgdContent(searchTagInfoQueryDao.getTagRepsBatchContent())
				.tagBkgdContentType(searchTagInfoQueryDao.getTagRepsBatchContentType())
				.build()))
			.toList();
	}

	@Transactional
	public List<GetFavoriteTermRsp> getFavoriteTermList(Long userId) {
		List<SnsUserFavoriteTermBookmark> snsUserFavoriteTermBookmarkList = snsUserFavoriteTermBookmarkRepository
			.findAllBySnsUser_IdOrderByIdDesc(
				userId);
		return snsUserFavoriteTermBookmarkList.stream().map((snsUserFavoriteTermBookmark -> GetFavoriteTermRsp.builder()
			.favoriteTermName(snsUserFavoriteTermBookmark.getFavoriteTermName())
			.favoriteTermContent(snsUserFavoriteTermBookmark.getFavoriteTermContent())
			.favoriteTermContentType(snsUserFavoriteTermBookmark.getFavoriteTermContentType() != null ?
				snsUserFavoriteTermBookmark.getFavoriteTermContentType().toString() : null)
			.build())).toList();
	}

	@Transactional
	public Boolean createFavoriteTerm(Long userId, PutFavoriteTerm putFavoriteTerm) {
		SnsUserFavoriteTermBookmark snsUserFavoriteTermBookmark = SnsUserFavoriteTermBookmark.builder()
			.snsUser(SnsUser.builder().id(userId).build())
			.favoriteTermName(putFavoriteTerm.getFavoriteTerm())
			.favoriteTermContent(putFavoriteTerm.getFavoriteTermContent())
			.favoriteTermContentType(PostContentType.valueOf(putFavoriteTerm.getFavoriteTermContentType()))
			.build();

		snsUserFavoriteTermBookmarkRepository.save(snsUserFavoriteTermBookmark);
		return true;
	}

	@Transactional
	public Boolean deleteFavoriteTerm(Long userId, String searchQuery) {
		Optional<SnsUserFavoriteTermBookmark> favoriteTermBookmarkOpt = snsUserFavoriteTermBookmarkRepository
			.findBySnsUser_IdAndFavoriteTermName(
				userId, searchQuery);
		if (favoriteTermBookmarkOpt.isEmpty()) {
			throw new BadRequestErrorException("잘못된 요청입니다.");
		}
		snsUserFavoriteTermBookmarkRepository.delete(favoriteTermBookmarkOpt.get());
		return false;
	}

	@Transactional
	public Boolean modifyFavoriteTag(Long userId, Long tagId) {
		return null;
	}

	public String makeTagSearchTerm(String tagName) {
		return String.format("#%s", tagName);
	}
}
