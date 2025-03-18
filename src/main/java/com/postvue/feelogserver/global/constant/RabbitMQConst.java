package com.postvue.feelogserver.global.constant;

public final class RabbitMQConst {
	// Prefix, Suffix
	private static final String EXCHANGE_PREFIX = "x";
	private static final String QUEUE_PREFIX = "q";
	private static final String ROUTE_PREFIX = "r";
	public static final String WORK_SUFFIX = ".work";
	public static final String DEAD_SUFFIX = ".dead";
	private static final String PARKING_LOT_SUFFIX = ".parking";
	private static final String RABBIT_MQ_VIDEO_CHANNEL = ".video";
	private static final String RABBIT_MQ_POST_IMAGE_UPLOAD_CHANNEL = ".post-image-upload";

	// Max Retry Num
	public static final Integer MAX_RETRY_COUNT = 3;


	// Route Key
	public static final String RABBIT_MQ_PARKING_LOT_TOPIC_ROUTE_KEY = ROUTE_PREFIX + PARKING_LOT_SUFFIX + ".#";
	public static final String RABBIT_MQ_VIDEO_CONVERT_UPLOAD_DIRECT_ROUTE_KEY = ROUTE_PREFIX + RABBIT_MQ_VIDEO_CHANNEL + ".upload";
	public static final String RABBIT_MQ_PARKING_LOT_VIDEO_ROUTE_KEY = ROUTE_PREFIX + PARKING_LOT_SUFFIX + RABBIT_MQ_VIDEO_CHANNEL;
	public static final String RABBIT_MQ_PARKING_LOT_POST_IMAGE_UPLOAD_ROUTE_KEY = ROUTE_PREFIX + PARKING_LOT_SUFFIX + RABBIT_MQ_POST_IMAGE_UPLOAD_CHANNEL;
	public static final String RABBIT_MQ_DEAD_LETTER_VIDEO_ROUTE_KEY = ROUTE_PREFIX + RABBIT_MQ_VIDEO_CHANNEL + ".#";

	public static final String RABBIT_MQ_DEAD_LETTER_POST_IMAGE_UPLOAD_ROUTE_KEY = ROUTE_PREFIX + RABBIT_MQ_POST_IMAGE_UPLOAD_CHANNEL + ".#";

	public static final String RABBIT_MQ_POST_IMAGE_UPLOAD_DIRECT_ROUTE_KEY = ROUTE_PREFIX + RABBIT_MQ_POST_IMAGE_UPLOAD_CHANNEL + ".upload";


	// Video
	public static final String RABBIT_MQ_VIDEO_QUEUE = QUEUE_PREFIX + RABBIT_MQ_VIDEO_CHANNEL + WORK_SUFFIX;
	public static final String RABBIT_MQ_VIDEO_EXCHANGE = EXCHANGE_PREFIX + RABBIT_MQ_VIDEO_CHANNEL + WORK_SUFFIX;

	// Video Dead Letter
	public static final String RABBIT_MQ_VIDEO_DLX_EXCHANGE = EXCHANGE_PREFIX + RABBIT_MQ_VIDEO_CHANNEL + DEAD_SUFFIX;
	public static final String RABBIT_MQ_VIDEO_DLX_QUEUE = QUEUE_PREFIX + RABBIT_MQ_VIDEO_CHANNEL + DEAD_SUFFIX;

	// Post Image Upload
	public static final String RABBIT_MQ_POST_IMAGE_UPLOAD_QUEUE = QUEUE_PREFIX + RABBIT_MQ_POST_IMAGE_UPLOAD_CHANNEL + WORK_SUFFIX;
	public static final String RABBIT_MQ_POST_IMAGE_UPLOAD_EXCHANGE = EXCHANGE_PREFIX + RABBIT_MQ_POST_IMAGE_UPLOAD_CHANNEL + WORK_SUFFIX;

	// Post Image Upload Dead Letter
	public static final String RABBIT_MQ_POST_IMAGE_UPLOAD_DLX_EXCHANGE = EXCHANGE_PREFIX + RABBIT_MQ_POST_IMAGE_UPLOAD_CHANNEL + DEAD_SUFFIX;
	public static final String RABBIT_MQ_POST_IMAGE_UPLOAD_DLX_QUEUE = QUEUE_PREFIX + RABBIT_MQ_POST_IMAGE_UPLOAD_CHANNEL + DEAD_SUFFIX;


	// Parking Lot
	public static final String RABBIT_MQ_PARKING_LOT_EXCHANGE = EXCHANGE_PREFIX + PARKING_LOT_SUFFIX;
	public static final String RABBIT_MQ_PARKING_LOT_QUEUE = QUEUE_PREFIX + PARKING_LOT_SUFFIX;

	// Configuration Variable
	public static final String X_RETRIES_COUNT = "x-retries-count";
	public static final String CONSUMER_ERROR_INFO = "consumer-error-info";

	// Error
	public static final String RABBIT_MQ_ERROR_TYPE = "rabbit.mq.error";

	public static final Integer RETRIES_CNT = 3;
}
