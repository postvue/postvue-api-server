package com.postvue.feelogserver.domain.snsscrapboard.vo;

public enum ScrapTargetAudience {
	PUBLIC_AUDIENCE(ScrapTargetAudienceValue.PUBLIC_AUDIENCE_VALUE), // 모든 사람
	PRIVATE_AUDIENCE(ScrapTargetAudienceValue.PRIVATE_AUDIENCE_VALUE), // 비공개
	PROTECTED_AUDIENCE(ScrapTargetAudienceValue.PROTECTED_AUDIENCE_VALUE); // 팔로우 만

	private final String label;

	ScrapTargetAudience(String label) {
		this.label = label;
	}

	public String label() {
		return label;
	}
}
