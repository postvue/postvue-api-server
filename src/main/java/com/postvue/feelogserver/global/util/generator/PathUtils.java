package com.postvue.feelogserver.global.util.generator;

import java.util.Map;

public final class PathUtils {
	public static String generatePath(String pattern, Map<String, String> params) {
		String path = pattern;
		for (Map.Entry<String, String> entry : params.entrySet()) {
			path = path.replace("{" + entry.getKey() + "}", entry.getValue());
		}
		return path;
	}
}
