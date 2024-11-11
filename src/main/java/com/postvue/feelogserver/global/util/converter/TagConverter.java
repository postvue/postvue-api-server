package com.postvue.feelogserver.global.util.converter;

import java.util.List;

public final class TagConverter {
	public static String convertTagListToTagListSqlString(List<String> tagList) {
		// &@~ '단어1 OR 단어2 OR ....';

		return String.join(" OR ", tagList.stream().map((tag -> String.format("\"%s\"", tag))).toList());
	}
}
