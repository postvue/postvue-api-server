package com.postvue.feelogserver.domain.snsposts.vo;

public final class TgtAudTypeValue {
	// public static final String CONSUMER_VALUE = "CONSUMER";
	// public static final String BUSINESS_VALUE = "BUSINESS";

	public static final String PUBLIC_SCOPE_VALUE = "PUBLIC_SCOPE";
	public static final Integer PUBLIC_SCOPE_ID_VALUE = 0;
	public static final String FOLLOWERS_SCOPE_VALUE = "FOLLOWERS_SCOPE";
	public static final Integer FOLLOWERS_SCOPE_ID_VALUE = 1;
	public static final String PRIVATE_SCOPE_VALUE = "PRIVATE_SCOPE";
	public static final Integer PRIVATE_SCOPE_ID_VALUE = 2;

	public static final String DEFAULT_PUBLIC_AUDIENCE_VALUE = "'" + PUBLIC_SCOPE_VALUE + "'";
}
