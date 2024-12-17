package com.postvue.feelogserver.app.notifications.controller;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.postvue.feelogserver.app.notifications.dto.ws.sub.SnsNotificationSub;
import com.postvue.feelogserver.app.notifications.service.NotificationService;
import com.postvue.feelogserver.core.security.CustomUserDetails;
import com.postvue.feelogserver.global.constant.SnsNotificationConst;
import com.postvue.feelogserver.global.http.response.serverresponse.ServerGetOkRsp;
import com.postvue.feelogserver.global.util.converter.DateConvertor;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/notifications")
@RequiredArgsConstructor
public class NotificationController {
	private final NotificationService notificationService;

	@GetMapping("messages")
	public ServerGetOkRsp<List<SnsNotificationSub>> getSnsNotificationList(
		@AuthenticationPrincipal CustomUserDetails userDetails,
		@RequestParam(name = "notifiedDateTime", required = false) String notifiedDateTimeString
	) {
		LocalDateTime notifiedDateTime = DateConvertor.parseOrDefault(notifiedDateTimeString);
		Long snsUserId = (userDetails == null) ? null : Long.valueOf(userDetails.getUserId());
		return new ServerGetOkRsp<>(notificationService.getNotificationList(snsUserId,
			notifiedDateTime != null ? notifiedDateTime : LocalDateTime.now().minusDays(
				SnsNotificationConst.MAX_NOTIFICATION_MSG_RETENTION_DAY)));
	}
}
