package com.postvue.feelogserver.global.util.validation;

import java.net.MalformedURLException;
import java.net.URL;

public final class UrlValidUtils {
	public static boolean isValidURL(String url) {
		try {
			new URL(url);
			return true;
		} catch (MalformedURLException e) {
			return false;
		}
	}

}
