package com.postvue.feelogserver.domain.snsposts.vo;

public enum AudShareScope {
	EVERYONE_SCOPE(AudShareScopeValue.EVERYONE_SCOPE_VALUE),
	FOLLOWERS_SCOPE(AudShareScopeValue.FOLLOWERS_SCOPE_VALUE),
	PRIVATE_SCOPE(AudShareScopeValue.PRIVATE_SCOPE_VALUE);

	private final String label;

	AudShareScope(String label) {
		this.label = label;
	}

	public String label() {
		return label;
	}
}
