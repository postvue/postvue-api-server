package com.postvue.feelogserver.app.notifications.scheduler;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.postvue.feelogserver.app.notifications.service.NotificationService;
import com.postvue.feelogserver.global.constant.LogTemplateConst;
import com.postvue.feelogserver.global.constant.LogTypeConst;
import com.postvue.feelogserver.global.constant.SnsNotificationConst;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationScheduler {
	private final NotificationService notificationService;

	// 매일 새벽 4시에 실행
	@Scheduled(cron = "0 0 4 * * *")
	@Transactional
	public void startGroupOrder() {
		log.info(
			LogTemplateConst.getLogSuccessTemplate(
				LogTypeConst.SCHEDULING,
				SnsNotificationConst.MAX_NOTIFICATION_MSG_RETENTION_DAY.toString() + " 일이 지난 알림을 삭제했습니다.",
				HttpStatus.OK.value(), LocalDateTime.now().toString()));

		// 14일 전 데이터
		LocalDateTime daysAgo = LocalDateTime.now().minusDays(SnsNotificationConst.MAX_NOTIFICATION_MSG_RETENTION_DAY);

		notificationService.deleteNotificationList(daysAgo);
	}
}
