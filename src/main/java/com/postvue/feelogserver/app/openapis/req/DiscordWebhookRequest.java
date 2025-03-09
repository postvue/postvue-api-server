package com.postvue.feelogserver.app.openapis.req;

import com.fasterxml.jackson.annotation.JsonProperty;

public class DiscordWebhookRequest {

	private String content;

	public DiscordWebhookRequest(String content) {
		this.content = content;
	}

	@JsonProperty("content")
	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}
}
