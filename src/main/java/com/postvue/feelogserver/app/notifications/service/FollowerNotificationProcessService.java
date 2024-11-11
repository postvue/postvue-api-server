package com.postvue.feelogserver.app.notifications.service;

import java.util.Collections;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.postvue.feelogserver.domain.snsnotifications.SnsNotification;
import com.postvue.feelogserver.domain.snsnotifications.repository.SnsNotificationRepository;
import com.postvue.feelogserver.domain.snsnotifications.vo.SnsNotificationContent;
import com.postvue.feelogserver.domain.snsnotifications.vo.SnsNotificationContentType;
import com.postvue.feelogserver.domain.snspostuserreactions.SnsPostUserReaction;
import com.postvue.feelogserver.domain.snsuserfollows.SnsUserFollow;
import com.postvue.feelogserver.domain.snsusers.SnsUser;
import com.postvue.feelogserver.global.constant.SnsNotificationConst;
import com.postvue.feelogserver.global.constant.SnsNotificationTemplateConst;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FollowerNotificationProcessService
	implements NotificationProcessServiceInterface<SnsUserFollow, SnsPostUserReaction, SnsNotification> {
	private final SnsNotificationRepository snsNotificationRepository;
	private final NotificationServiceTemplate notificationServiceTemplate;

	@Override
	@Transactional
	public void processNotification(SnsUserFollow snsUserFollow, SnsPostUserReaction snsPostUserReaction) {
		if (getHasAlreadyNotification(snsUserFollow, SnsNotificationConst.ZERO_NOTIFICATION_NUM)) {
			return;
		}
		SnsNotification snsNotification = saveNotification(snsUserFollow, snsPostUserReaction,
			SnsNotificationConst.ZERO_NOTIFICATION_NUM);
		sendNotification(snsNotification);
	}

	// 유저가 활성화 상태일 경우, 알림 전달
	@Override
	public void sendNotification(SnsNotification snsNotification) {
		notificationServiceTemplate.sendNotification(snsNotification);
	}

	// 이미 같은 알림이 있는 지
	public Boolean getHasAlreadyNotification(SnsUserFollow snsUserFollow, Integer notificationCount) {
		return notificationServiceTemplate.getHasAlreadyNotificationByFollower(snsUserFollow);
	}

	// 특정 조건 일떄, 알림 저장
	@Override
	@Transactional
	public SnsNotification saveNotification(SnsUserFollow snsUserFollow, SnsPostUserReaction snsPostUserReaction,
		Integer notificationCount) {
		SnsUser followerUser = snsUserFollow.getFollowerUser();

		SnsNotification snsNotification = notificationServiceTemplate.convertFollowerNotification(snsUserFollow,
			Collections.singletonList(
				SnsNotificationContent.builder() // 알림 메시지 내용
					.snsNotificationContentType(SnsNotificationContentType.TEXT)
					.snsNotificationContent(
						SnsNotificationTemplateConst.getFollowerNotificationMessage(followerUser.getUsername()))
					.build()
			));

		return snsNotificationRepository.save(snsNotification);
	}

}
