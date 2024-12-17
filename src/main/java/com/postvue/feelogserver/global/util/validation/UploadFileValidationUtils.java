package com.postvue.feelogserver.global.util.validation;

import com.postvue.feelogserver.global.constant.MediaConfigConst;

public final class UploadFileValidationUtils {
	public static boolean isImage(String contentType) {
		for (String type : MediaConfigConst.ALLOWED_IMAGE_TYPES) {
			if (type.equalsIgnoreCase(contentType)) {
				return true;
			}
		}
		return false;
	}

	public static boolean isVideo(String contentType) {
		for (String type : MediaConfigConst.ALLOWED_VIDEO_TYPES) {
			if (type.equalsIgnoreCase(contentType)) {
				return true;
			}
		}
		return false;
	}
}
