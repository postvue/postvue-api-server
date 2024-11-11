package com.postvue.feelogserver.domain.snsusermessages.vo;

public enum SnsMsgReactionType {
	NOT_REACTION(SnsMsgReactionTypeValue.NOT_REACTION_VALUE),
	REACTION_LIKE(SnsMsgReactionTypeValue.REACTION_LIKE_VALUE),
	REACTION_HEART(SnsMsgReactionTypeValue.REACTION_HEART_VALUE),
	REACTION_LAUGH(SnsMsgReactionTypeValue.REACTION_LAUGH_VALUE),
	REACTION_SURPRISE(SnsMsgReactionTypeValue.REACTION_SURPRISE_VALUE),
	REACTION_SAD(SnsMsgReactionTypeValue.REACTION_SAD_VALUE),
	REACTION_ANGRY(SnsMsgReactionTypeValue.REACTION_ANGRY_VALUE);

	private final String label;

	SnsMsgReactionType(String label) {
		this.label = label;
	}

	public String label() {
		return label;
	}
}
