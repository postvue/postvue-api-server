package com.postvue.feelogserver.app.search.controller;

import java.util.List;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.postvue.feelogserver.app.search.dto.req.PutFavoriteTerm;
import com.postvue.feelogserver.app.search.dto.rsp.GetFavoriteTermRsp;
import com.postvue.feelogserver.app.search.dto.rsp.GetTagInfoSearchRsp;
import com.postvue.feelogserver.app.search.service.SearchService;
import com.postvue.feelogserver.core.security.CustomUserDetails;
import com.postvue.feelogserver.global.constant.PageConfigConst;
import com.postvue.feelogserver.global.http.response.serverresponse.ServerGetOkRsp;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/search")
@RequiredArgsConstructor
public class SearchController {
	private final SearchService searchService;

	@GetMapping("/{searchQuery}")
	public ServerGetOkRsp<List<String>> getSearchQueryRelation(
		@PathVariable("searchQuery") String searchQuery) {
		return new ServerGetOkRsp<>(searchService.getSearchRelationList(searchQuery));
	}

	@GetMapping("/favorite/terms")
	public ServerGetOkRsp<List<GetFavoriteTermRsp>> getFavoriteSearchTermList(
		@AuthenticationPrincipal CustomUserDetails userDetails) {

		Long snsUserId = (userDetails == null) ? null : Long.valueOf(userDetails.getUserId());
		return new ServerGetOkRsp<>(searchService.getFavoriteTermList(snsUserId));
	}

	@GetMapping("/tags/{searchQuery}")
	public ServerGetOkRsp<List<String>> getTagInfoSearchQuery(
		@PathVariable("searchQuery") String searchQuery) {
		return new ServerGetOkRsp<>(searchService.getTagSearchList(searchQuery));
	}

	@GetMapping("/tags/info/{searchQuery}")
	public ServerGetOkRsp<List<GetTagInfoSearchRsp>> getTagSearchQuery(
		@PathVariable("searchQuery") String searchQuery,
		@RequestParam(value = "page", defaultValue = PageConfigConst.PAGE_INIT_NUM_STRING) Integer page) {
		return new ServerGetOkRsp<>(searchService.getTagInfoSearchList(searchQuery, page));
	}

	@PutMapping("/favorite/terms")
	public ServerGetOkRsp<Boolean> putFavoriteSearchTerm(
		@AuthenticationPrincipal CustomUserDetails userDetails,
		@RequestBody PutFavoriteTerm putFavoriteTerm) {

		Long snsUserId = (userDetails == null) ? null : Long.valueOf(userDetails.getUserId());
		boolean isFavorite = putFavoriteTerm.getIsFavorite()
			?
			searchService.createFavoriteTerm(snsUserId, putFavoriteTerm) :
			searchService.deleteFavoriteTerm(snsUserId, putFavoriteTerm.getFavoriteTerm());

		return new ServerGetOkRsp<>(isFavorite);
	}

	@PutMapping("/favorite/tags/{tagId}")
	public ServerGetOkRsp<Boolean> putFavoriteTag(
		@AuthenticationPrincipal CustomUserDetails userDetails,
		@PathVariable("tagId") Long tagId) {
		Long snsUserId = (userDetails == null) ? null : Long.valueOf(userDetails.getUserId());
		return new ServerGetOkRsp<>(searchService.modifyFavoriteTag(snsUserId, tagId));
	}
}
