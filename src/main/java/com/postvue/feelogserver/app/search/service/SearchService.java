package com.postvue.feelogserver.app.search.service;

import java.util.List;
import java.util.Optional;

import org.hibernate.annotations.ColumnDefault;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.postvue.feelogserver.app.search.dao.SearchQueryDao;
import com.postvue.feelogserver.app.search.dto.req.PutFavoriteTerm;
import com.postvue.feelogserver.app.search.dto.rsp.GetFavoriteTermRsp;
import com.postvue.feelogserver.app.search.dto.rsp.GetTagInfoSearchRsp;
import com.postvue.feelogserver.app.search.dto.rsp.GetTermInfoRsp;
import com.postvue.feelogserver.domain.snsposts.vo.PostContentType;
import com.postvue.feelogserver.domain.snstagfollows.SnsTagFollow;
import com.postvue.feelogserver.domain.snstagfollows.repository.SnsTagFollowRepository;
import com.postvue.feelogserver.domain.snstags.SnsTag;
import com.postvue.feelogserver.domain.snstags.repository.SnsTagRepository;
import com.postvue.feelogserver.domain.snsuserfavoritetermbookmarks.SnsUserFavoriteTermBookmark;
import com.postvue.feelogserver.domain.snsuserfavoritetermbookmarks.respository.SnsUserFavoriteTermBookmarkRepository;
import com.postvue.feelogserver.domain.snsusers.SnsUser;
import com.postvue.feelogserver.global.constant.HashConst;
import com.postvue.feelogserver.global.constant.PageConfigConst;
import com.postvue.feelogserver.global.exception.BadRequestErrorException;
import com.postvue.feelogserver.global.exception.NotFoundErrorException;
import com.postvue.feelogserver.global.util.validation.StringValidUtil;

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
			.map((SearchQueryDao::getSearchQueryName))
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
	public GetTermInfoRsp getTermInfo(String term, Long userId) {
		Optional<SnsUserFavoriteTermBookmark> searchFavoriteBookmarkOpt = snsUserFavoriteTermBookmarkRepository
			.findByFavoriteTermNameAndSnsUser_id(term, userId);

		boolean isTag = false;
		boolean isFollowTag = false;
		boolean isExistTag = false;
		boolean isFavoriteTerm = searchFavoriteBookmarkOpt.isPresent();
		String favoriteTermContent = searchFavoriteBookmarkOpt.map(SnsUserFavoriteTermBookmark::getFavoriteTermContent)
			.orElse(null);
		String favoriteTermContentType = searchFavoriteBookmarkOpt.map(snsUserFavoriteTermBookmark -> snsUserFavoriteTermBookmark.getFavoriteTermContentType() != null ? snsUserFavoriteTermBookmark.getFavoriteTermContentType().toString() : null)
			.orElse(null);
		if (term.startsWith(HashConst.HAST_TAG_PREFIX)){
			isTag = true;
			Optional<SnsTag> snsTagOpt = snsTagRepository.findByTagName(HashConst.getHashTag(term));
			isExistTag = snsTagOpt.isPresent();
			isFollowTag = isFavoriteTerm && isExistTag;
		}
		return GetTermInfoRsp.builder()
			.isFavoriteTerm(isFavoriteTerm)
			.favoriteTermName(term)
			.favoriteTermContent(favoriteTermContent)
			.favoriteTermContentType(favoriteTermContentType)
			.isTag(isTag)
			.isFollowTag(isFollowTag)
			.isExistTag(isExistTag)
			.build();
	}

	@Transactional
	public List<GetFavoriteTermRsp> getFavoriteTermList(Long userId, Integer page) {
		List<SnsUserFavoriteTermBookmark> snsUserFavoriteTermBookmarkList = snsUserFavoriteTermBookmarkRepository
			.findAllBySnsUser_IdOrderByIdDesc(
				userId,
				PageRequest.of(page * PageConfigConst.SEARCH_FAVORITE_TERM_PAGE_SIZE, PageConfigConst.SEARCH_FAVORITE_TERM_PAGE_SIZE)
			);
		return snsUserFavoriteTermBookmarkList.stream().map((snsUserFavoriteTermBookmark -> GetFavoriteTermRsp.builder()
			.favoriteTermName(snsUserFavoriteTermBookmark.getFavoriteTermName())
			.favoriteTermContent(snsUserFavoriteTermBookmark.getFavoriteTermContent())
			.favoriteTermContentType(snsUserFavoriteTermBookmark.getFavoriteTermContentType() != null ?
				snsUserFavoriteTermBookmark.getFavoriteTermContentType().toString() : null)
			.isFavorite(true)
			.build())).toList();
	}

	@Transactional
	public Boolean createFavoriteTerm(Long userId, PutFavoriteTerm putFavoriteTerm) {

		SnsUserFavoriteTermBookmark snsUserFavoriteTermBookmark;
		if (putFavoriteTerm.getFavoriteTerm().startsWith(HashConst.HAST_TAG_PREFIX)){
			SnsTag snsTag = snsTagRepository.findByTagName(HashConst.getHashTag(putFavoriteTerm.getFavoriteTerm())).orElseThrow(
				() -> new NotFoundErrorException("해당 태그와 관련된 게시물이 없어 저장할 수 없습니다.")
			);

			SnsTagFollow snsTagFollow = SnsTagFollow.builder()
				.snsTag(snsTag)
				.tagName(putFavoriteTerm.getFavoriteTerm())
				.snsUser(SnsUser.builder().id(userId).build())
				.build();

			snsTagFollow = snsTagFollowRepository.save(snsTagFollow);
			snsTagFollowRepository.flush();

			snsUserFavoriteTermBookmark = SnsUserFavoriteTermBookmark.builder()
				.snsUser(SnsUser.builder().id(userId).build())
				.favoriteTermName(putFavoriteTerm.getFavoriteTerm())
				.favoriteTermContent(StringValidUtil.isNotBlank(putFavoriteTerm.getFavoriteTermContent()) ? putFavoriteTerm.getFavoriteTermContent() : null)
				.favoriteTermContentType(StringValidUtil.isNotBlank(putFavoriteTerm.getFavoriteTermContentType()) ? PostContentType.valueOf(putFavoriteTerm.getFavoriteTermContentType()) : null)
				.snsTagFollow(snsTagFollow)
				.build();
		}
		else{
			snsUserFavoriteTermBookmark = SnsUserFavoriteTermBookmark.builder()
				.snsUser(SnsUser.builder().id(userId).build())
				.favoriteTermName(putFavoriteTerm.getFavoriteTerm())
				.favoriteTermContent(StringValidUtil.isNotBlank(putFavoriteTerm.getFavoriteTermContent()) ? putFavoriteTerm.getFavoriteTermContent() : null)
				.favoriteTermContentType(StringValidUtil.isNotBlank(putFavoriteTerm.getFavoriteTermContentType()) ? PostContentType.valueOf(putFavoriteTerm.getFavoriteTermContentType()) : null)
				.build();
		}


		snsUserFavoriteTermBookmarkRepository.save(snsUserFavoriteTermBookmark);
		return true;
	}

	@Transactional
	public Boolean deleteFavoriteTerm(Long userId, String searchQuery) {
		if (searchQuery.startsWith(HashConst.HAST_TAG_PREFIX)){
			Optional<SnsUserFavoriteTermBookmark> favoriteTermBookmarkOpt = snsUserFavoriteTermBookmarkRepository
				.findBySnsUser_IdAndFavoriteTermName(
					userId, searchQuery);
			if (favoriteTermBookmarkOpt.isEmpty()) {
				throw new BadRequestErrorException("잘못된 요청입니다.");
			}
			snsUserFavoriteTermBookmarkRepository.delete(favoriteTermBookmarkOpt.get());
			snsUserFavoriteTermBookmarkRepository.flush();

			Optional<SnsTag> snsTagOpt = snsTagRepository.findByTagName(HashConst.getHashTag(searchQuery));

			if (snsTagOpt.isPresent()){
				Optional<SnsTagFollow> snsTagFollowOpt = snsTagFollowRepository.findBySnsTagAndSnsUser_Id(snsTagOpt.get(),userId);
				snsTagFollowOpt.ifPresent(snsTagFollowRepository::delete);
			}





			return false;
		}
		else{
			Optional<SnsUserFavoriteTermBookmark> favoriteTermBookmarkOpt = snsUserFavoriteTermBookmarkRepository
				.findBySnsUser_IdAndFavoriteTermName(
					userId, searchQuery);
			if (favoriteTermBookmarkOpt.isEmpty()) {
				throw new BadRequestErrorException("잘못된 요청입니다.");
			}
			snsUserFavoriteTermBookmarkRepository.delete(favoriteTermBookmarkOpt.get());
			return false;
		}
	}

	@Transactional
	public Boolean modifyFavoriteTag(Long userId, Long tagId) {
		return null;
	}

	public String makeTagSearchTerm(String tagName) {
		return String.format("#%s", tagName);
	}
}
