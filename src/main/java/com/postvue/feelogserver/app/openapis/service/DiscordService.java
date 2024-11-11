package com.postvue.feelogserver.app.openapis.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.postvue.feelogserver.global.api.discord.DiscordWebhookClient;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DiscordService {
	private final DiscordWebhookClient discordWebhookClient;

	@Value("${openapi.discord.webhook.postReport.channelId}")
	private String postReportChannelId;

	@Value("${openapi.discord.webhook.postReport.token}")
	private String postReportToken;

	public void sendMessageToPostReportChannel(){
		discordWebhookClient.postMsgToDiscordChannel(postReportChannelId, postReportToken);
	}
}
