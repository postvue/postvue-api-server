package com.postvue.feelogserver.global.constant;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.MediaType;

public final class MediaConfigConst {
	public static final String BASE_FILE_TYPE = "application/octet-stream";

	// MIME TYPE
	public static final String VIDEO_MP4_TYPE = "video/mp4";
	public static final String VIDEO_WEBM_TYPE = "video/webm";
	public static final String VIDEO_AVI_TYPE = "video/avi";
	public static final String VIDEO_MKV_TYPE = "video/mkv";

	public static final String VIDEO_QUICKTIME_TYPE = "video/quicktime";
	public static final String VIDEO_HLS_TYPE = "application/x-mpegurl";
	public static final String VIDEO_HLS_TS_TYPE = "video/mp2t";

	public static final String IMAGE_JPEG_TYPE = "image/jpeg";
	public static final String IMAGE_PNG_TYPE = "image/png";
	public static final String IMAGE_GIF_TYPE = "image/gif";
	public static final String IMAGE_BMP_TYPE = "image/bmp";
	public static final String IMAGE_WEBP_TYPE = "image/webp";
	public static final String IMAGE_HEIC_TYPE = "image/heic";



	// file format
	public static final String IMAGE_JPEG = "jpg";
	public static final String IMAGE_JPEG_FORMAT = ".jpg" ;
	public static final String IMAGE_WEBP_FORMAT = ".webp";
	public static final String VIDEO_M3U8_FORMAT = ".m3u8";
	public static final String VIDEO_TS_FORMAT = ".ts";


	public static final Map<String, String> CONTENT_TYPE_MAP = new HashMap<>();

	static {
		CONTENT_TYPE_MAP.put("jpg", MediaType.IMAGE_JPEG_VALUE);
		CONTENT_TYPE_MAP.put("jpeg", MediaType.IMAGE_JPEG_VALUE);
		CONTENT_TYPE_MAP.put("png", MediaType.IMAGE_PNG_VALUE);
		CONTENT_TYPE_MAP.put("gif", MediaType.IMAGE_GIF_VALUE);
		CONTENT_TYPE_MAP.put("bmp", "image/bmp");
		CONTENT_TYPE_MAP.put("webp", "image/webp");
	}


	public static final String[] ALLOWED_IMAGE_TYPES = {
		IMAGE_JPEG_TYPE, IMAGE_PNG_TYPE, IMAGE_GIF_TYPE, IMAGE_BMP_TYPE, IMAGE_WEBP_TYPE, IMAGE_HEIC_TYPE, BASE_FILE_TYPE
	};


	public static final String[] ALLOWED_VIDEO_TYPES = {
		VIDEO_MP4_TYPE, VIDEO_WEBM_TYPE, VIDEO_AVI_TYPE, VIDEO_MKV_TYPE, VIDEO_QUICKTIME_TYPE
	};

	public static final String UPLOAD_TEMP_FILE_PREFIX_NAME = "upload-";

	public static final String UPLOAD_TEMP_FILE_SUFFIX_FORMAT = ".tmp";
	public static final String TEMP_IMAGE_NAME = "output.jpg";

	public static final Integer MAX_UPLOAD_FILE_NUM = 5;

	// 최대 동시 비디오 업로드 수
	public static final Integer MAX_ASYNC_VIDEO_UPLOAD_NUM = 12;

	public static final Integer MAX_VIDEO_DURATION = 180;
}
