package com.postvue.feelogserver.global.constant;

public final class HashConst {
	public static final String HAST_TAG_PREFIX = "#";

	public static String getHashTag (String term){
		return term.replaceFirst("^"+HAST_TAG_PREFIX, "");
	}
}
