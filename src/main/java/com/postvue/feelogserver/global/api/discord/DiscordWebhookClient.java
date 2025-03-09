package com.postvue.feelogserver.global.api.discord;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import com.postvue.feelogserver.global.api.kakao.dto.rsp.KakaoUserInfo;
import com.postvue.feelogserver.global.constant.DiscordApiConst;

@FeignClient(
	name = "discordWebhookClient",
	url = DiscordApiConst.DISCORD_WEBHOOK_URL
)
public interface DiscordWebhookClient {
	@PostMapping("/{channelId}/{channelToken}")
	KakaoUserInfo postMsgToDiscordChannel(
		@PathVariable("channelId") String channelId,
		@PathVariable("channelToken") String channelToken);
}
