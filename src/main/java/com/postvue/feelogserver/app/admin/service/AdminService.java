package com.postvue.feelogserver.app.admin.service;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.postvue.feelogserver.app.openapis.req.DiscordWebhookRequest;
import com.postvue.feelogserver.app.openapis.service.DiscordService;
import com.postvue.feelogserver.endpoint.SnsPostEndpoint;
import com.postvue.feelogserver.global.constant.AdminConst;
import com.postvue.feelogserver.global.constant.LogTemplateConst;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class AdminService {
	private final DiscordService discordService;

	public void sendSaveAndUpdateErrorMsgToDiscord (Exception e) {
		log.error(e.getMessage());
		String errorTemplate = LogTemplateConst.getErrorLogTemplate(
			AdminConst.ADMIN_ERROR,
			"DB 업데이트에 실패했습니다.",
			e.getMessage(), SnsPostEndpoint.class.toString(),
			"save",
			null, HttpStatus.INTERNAL_SERVER_ERROR.value());

		DiscordWebhookRequest discordWebhookRequest = new DiscordWebhookRequest(errorTemplate);
		discordService.sendMessageToPostReportChannel(discordWebhookRequest);
	}
}
