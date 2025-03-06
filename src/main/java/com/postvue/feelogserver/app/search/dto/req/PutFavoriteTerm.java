package com.postvue.feelogserver.app.search.dto.req;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class PutFavoriteTerm {
	private Boolean isFavorite;
	private String favoriteTerm;
	private String favoriteTermContent;
	private String favoriteTermContentType;
}
