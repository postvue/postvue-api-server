package com.postvue.feelogserver.global.constant;

import software.amazon.awssdk.services.s3.endpoints.internal.Value;

public final class ServerConfig {
	public static final String[] ALLOWED_IMAGE_TYPES = {
		"image/jpeg", "image/png", "image/gif", "image/bmp", "image/webp"
	};

	public static final String[] ALLOWED_VIDEO_TYPES = {
		"video/mp4", "video/webm", "video/avi", "video/mkv"
	};

	public static final Integer MAX_UPLOAD_FILE_NUM = 5;
}
