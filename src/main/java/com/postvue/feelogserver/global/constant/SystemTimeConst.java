package com.postvue.feelogserver.global.constant;

public final class SystemTimeConst {
	// 1시간
	public static final Long MILLISECOND_BY_ONE_SECOND = 1000L;
	public static final Long SYSTEM_1_HOUR_TIME_BY_SECOND = 60 * 60L;
	public static final Long SYSTEM_1_HOUR_TIME_BY_MILLISECOND =
		SYSTEM_1_HOUR_TIME_BY_SECOND * MILLISECOND_BY_ONE_SECOND;

	// 하루: 1일, 24시간
	public static final Long SYSTEM_1_DAY_BY_SECOND = SYSTEM_1_HOUR_TIME_BY_SECOND * 24;
	public static final Long SYSTEM_1_DAY_BY_MILLISECOND = SYSTEM_1_DAY_BY_SECOND * MILLISECOND_BY_ONE_SECOND;

	// 1달: 30일
	public static final Long SYSTEM_1_MONTH_BY_SECOND = SYSTEM_1_DAY_BY_SECOND * 30;
	public static final Long SYSTEM_1_MONTH_BY_MILLISECOND = SYSTEM_1_DAY_BY_MILLISECOND * 30;
}
