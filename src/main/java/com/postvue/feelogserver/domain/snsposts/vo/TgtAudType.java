package com.postvue.feelogserver.domain.snsposts.vo;

public enum TgtAudType {
	// CONSUMER(TgtAudTypeValue.CONSUMER_VALUE),
	// BUSINESS(TgtAudTypeValue.BUSINESS_VALUE);
	PUBLIC_SCOPE(TgtAudTypeValue.PUBLIC_SCOPE_VALUE),
	FOLLOWERS_SCOPE(TgtAudTypeValue.FOLLOWERS_SCOPE_VALUE),
	PRIVATE_SCOPE(TgtAudTypeValue.PRIVATE_SCOPE_VALUE);

	private final String label;

	TgtAudType(String label) {
		this.label = label;
	}

	public String label() {
		return label;
	}
}
