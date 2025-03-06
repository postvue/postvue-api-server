package com.postvue.feelogserver.global.util.validation;

import java.util.regex.Pattern;

public final class StringValidUtil {
	private static final Pattern BLANK_PATTERN = Pattern.compile("^\\s*$");

	public static boolean isNotBlank(String input) {
		return input != null && !BLANK_PATTERN.matcher(input).matches();
	}

	public static String nullIfEmpty(String value) {
		return (value == null || value.isBlank()) ? null : value;
	}
}
