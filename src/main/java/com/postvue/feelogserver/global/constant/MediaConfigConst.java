package com.postvue.feelogserver.global.constant;

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


	// file format
	public static final String IMAGE_JPEG_FORMAT = ".jpg";
	public static final String VIDEO_M3U8_FORMAT = ".m3u8";
	public static final String VIDEO_TS_FORMAT = ".ts";


	public static final String[] ALLOWED_IMAGE_TYPES = {
		IMAGE_JPEG_TYPE, IMAGE_PNG_TYPE, IMAGE_GIF_TYPE, IMAGE_BMP_TYPE, IMAGE_WEBP_TYPE
	};


	public static final String[] ALLOWED_VIDEO_TYPES = {
		VIDEO_MP4_TYPE, VIDEO_WEBM_TYPE, VIDEO_AVI_TYPE, VIDEO_MKV_TYPE, VIDEO_QUICKTIME_TYPE
	};

	public static final String UPLOAD_TEMP_FILE_PREFIX_NAME = "upload-";
	public static final String TEMP_IMAGE_NAME = "output.jpg";

	public static final Integer MAX_UPLOAD_FILE_NUM = 5;

	// 최대 동시 비디오 업로드 수
	public static final Integer MAX_ASYNC_VIDEO_UPLOAD_NUM = 12;
}
