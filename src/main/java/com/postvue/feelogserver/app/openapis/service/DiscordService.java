package com.postvue.feelogserver.app.openapis.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.postvue.feelogserver.app.openapis.req.DiscordWebhookRequest;
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


	@Value("${openapi.discord.webhook.serviceNotification.channelId}")
	private String serviceNotificationChannelId;


	@Value("${openapi.discord.webhook.serviceNotification.token}")
	private String serviceNotificationToken;

	public void sendMessageToPostReportChannel(DiscordWebhookRequest discordWebhookRequest){
		discordWebhookClient.postMsgToDiscordChannel(
			postReportChannelId,
			postReportToken,
			discordWebhookRequest);
	}

	public void sendMessageToServiceNotificationChannel(DiscordWebhookRequest discordWebhookRequest){
		discordWebhookClient.postMsgToDiscordChannel(
			serviceNotificationChannelId,
			serviceNotificationToken,
			discordWebhookRequest);
	}
}
