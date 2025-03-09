package com.postvue.feelogserver.app.auth.scheduler;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.postvue.feelogserver.app.auth.service.AuthService;
import com.postvue.feelogserver.app.notifications.service.NotificationService;
import com.postvue.feelogserver.global.constant.AccountConst;
import com.postvue.feelogserver.global.constant.LogTemplateConst;
import com.postvue.feelogserver.global.constant.LogTypeConst;
import com.postvue.feelogserver.global.constant.SnsNotificationConst;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class AuthScheduler {
	private final AuthService authService;

	// 14일 지난 알림 삭제
	// 매일 새벽 4시에 실행
	@Scheduled(cron = "0 0 4 * * *")
	@Transactional
	public void scheduleDeleteNotificationByDay() {
		try {
			log.info(
				LogTemplateConst.getLogSuccessTemplate(
					LogTypeConst.SCHEDULING,
					AccountConst.MAX_DELETED_USER_RETENTION_DAY.toString() + " 일이 지난 알림을 삭제했습니다.",
					HttpStatus.OK.value(), LocalDateTime.now().toString()));

			// 14일 전 데이터
			LocalDateTime daysAgo = LocalDateTime.now()
				.minusDays(AccountConst.MAX_DELETED_USER_RETENTION_DAY);

			authService.updateDeletedUserToFullDeletedUser(daysAgo);
		} catch (Error error) {
			log.error(
				LogTemplateConst.getErrorLogTemplate(error.getClass().getName(),
					"유저 완전 삭제 업데이트 스케줄링 오류 발생했습니다.",
					error.getMessage(),
					this.getClass().getName(), Thread.currentThread().getStackTrace()[1].getMethodName(),
					new Object[] {},
					HttpStatus.INTERNAL_SERVER_ERROR.value()
				));
		}
	}
}
