package com.postvue.feelogserver.endpoint.dto;

import com.postvue.feelogserver.domain.snsposts.vo.PostContentType;
import com.postvue.feelogserver.domain.snsuserfavoritetermbookmarks.SnsUserFavoriteTermBookmark;

public record SnsUserFavoriteTermBookmarkEndpointDto(
	String id,
	String snsUser_id,
	String favoriteTermName,
	String favoriteTermContent,
	PostContentType favoriteTermContentType,
	String snsTagFollow_id
) {

	public static SnsUserFavoriteTermBookmarkEndpointDto fromEntity(SnsUserFavoriteTermBookmark snsUserFavoriteTermBookmark){
		return new SnsUserFavoriteTermBookmarkEndpointDto(
			snsUserFavoriteTermBookmark.getId().toString(),
			snsUserFavoriteTermBookmark.getSnsUser().getId().toString(),
			snsUserFavoriteTermBookmark.getFavoriteTermName(),
			snsUserFavoriteTermBookmark.getFavoriteTermContent(),
			snsUserFavoriteTermBookmark.getFavoriteTermContentType(),
			snsUserFavoriteTermBookmark.getSnsTagFollow() != null ?snsUserFavoriteTermBookmark.getSnsTagFollow().getId().toString() : null
		);
	}
}
