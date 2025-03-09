package com.postvue.feelogserver.global.api.discord;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.postvue.feelogserver.app.openapis.req.DiscordWebhookRequest;
import com.postvue.feelogserver.global.api.kakao.dto.rsp.KakaoUserInfo;
import com.postvue.feelogserver.global.constant.DiscordApiConst;

@FeignClient(
	name = "discordWebhookClient",
	url = DiscordApiConst.DISCORD_WEBHOOK_URL
)
public interface DiscordWebhookClient {
	@PostMapping("/{channelId}/{channelToken}")
	void postMsgToDiscordChannel(
		@PathVariable("channelId") String channelId,
		@PathVariable("channelToken") String channelToken,
		@RequestBody DiscordWebhookRequest message);
}
