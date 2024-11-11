package com.postvue.feelogserver.domain.snsposts.vo;

public enum PostContentBusinessType {
	BUSINESS_GOOD_PLACE_TYPE(PostContentBusinessTypeValue.BUSINESS_GOOD_PLACE_TYPE_VALUE),
	BUSINESS_CAFE_TYPE(PostContentBusinessTypeValue.BUSINESS_CAFE_TYPE_VALUE),
	BUSINESS_ATTRACTION_TYPE(PostContentBusinessTypeValue.BUSINESS_ATTRACTION_TYPE_VALUE),
	BUSINESS_PARK_TYPE(PostContentBusinessTypeValue.BUSINESS_PARK_TYPE_VALUE),
	BUSINESS_DAILY_TYPE(PostContentBusinessTypeValue.BUSINESS_DAILY_TYPE_VALUE);

	private final String label;

	PostContentBusinessType(String label) {
		this.label = label;
	}

	public String label() {
		return label;
	}
}
