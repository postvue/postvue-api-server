package com.postvue.feelogserver.app.notifications.service;

import java.util.List;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import com.postvue.feelogserver.app.notifications.dto.ws.sub.SnsNotificationSub;
import com.postvue.feelogserver.domain.snsnotifications.SnsNotification;
import com.postvue.feelogserver.domain.snsnotifications.repository.SnsNotificationRepository;
import com.postvue.feelogserver.domain.snsnotifications.vo.SnsNotificationContent;
import com.postvue.feelogserver.domain.snsnotifications.vo.SnsNotificationType;
import com.postvue.feelogserver.domain.snsposts.SnsPost;
import com.postvue.feelogserver.domain.snsuserfollows.SnsUserFollow;
import com.postvue.feelogserver.domain.snsusers.SnsUser;
import com.postvue.feelogserver.global.constant.SnsNotificationConst;
import com.postvue.feelogserver.global.constant.WebSocketPathConst;
import com.postvue.feelogserver.global.util.generator.UrlUtils;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class NotificationServiceTemplate {
	private final SimpMessagingTemplate messageTemplate;
	private final SnsNotificationRepository snsNotificationRepository;

	public void sendNotification(SnsNotification snsNotification) {
		String destination = UrlUtils.getWebsocketTargetUri(WebSocketPathConst.NOTIFICATION_BROKER_PATH,
			snsNotification.getSnsUser().getId());
		messageTemplate.convertAndSend(destination, SnsNotificationSub.builder()
			.notificationId(snsNotification.getId().toString())
			.userId(snsNotification.getSnsUser().getId().toString())
			.username(snsNotification.getUsername())
			.postId(
				snsNotification.getSnsPost() != null ? snsNotification.getSnsPost().getId().toString() : null)
			.notificationUserId(snsNotification.getNotificationContentUserId().toString())
			.notificationUsername(snsNotification.getNotificationContentUsername())
			.notificationUserProfilePath(snsNotification.getNotificationContentUserProfilePath())
			.notificationType(snsNotification.getSnsNotificationType().toString())
			.notificationContents(snsNotification.getSnsNotificationContents())
			.notifiedAt(snsNotification.getCreatedAt())
			.isRead(false)
			.build());
	}

	public Boolean getHasAlreadyNotificationByPost(SnsPost snsPost, Integer notificationCount,
		SnsNotificationType snsNotificationType) {
		return snsNotificationRepository.findNotificationByPost(
			snsPost.getSnsUser().getId(),
			snsPost.getId(),
			snsNotificationType, notificationCount).isPresent();
	}

	public Boolean getHasAlreadyNotificationByFollower(SnsUserFollow snsUserFollow) {
		return snsNotificationRepository.findNotificationByFollower(
			snsUserFollow.getFollowingUser().getId(),
			snsUserFollow.getFollowerUser().getId()).isPresent();
	}

	public SnsNotification convertPostNotification(
		SnsPost snsPost,
		SnsUser notificationUser,
		SnsNotificationType snsNotificationType,
		List<SnsNotificationContent> snsNotificationContents,
		Integer notificationCount
	) {

		return SnsNotification.builder()
			.snsUser(snsPost.getSnsUser())
			.username(snsPost.getSnsUser().getUsername())
			.snsPost(snsPost)
			.notificationContentUserId(notificationUser.getId())
			.notificationContentUsername(notificationUser.getUsername())
			.notificationContentUserProfilePath(notificationUser.getProfilePath())
			.notificationCount(notificationCount)
			.snsNotificationType(snsNotificationType)
			// 알림 메시지 내용
			.snsNotificationContents(snsNotificationContents)
			.build();
	}

	public SnsNotification convertFollowerNotification(
		SnsUserFollow snsUserFollow,
		List<SnsNotificationContent> snsNotificationContents
	) {

		return SnsNotification.builder()
			.snsUser(snsUserFollow.getFollowingUser())
			.username(snsUserFollow.getFollowingUser().getUsername())
			.followerUser(snsUserFollow.getFollowerUser())
			.snsNotificationType(SnsNotificationType.USER_FOLLOWER_NOTIFICATION)
			.notificationCount(SnsNotificationConst.ZERO_NOTIFICATION_NUM)
			.notificationContentUserId(snsUserFollow.getFollowerUser().getId())
			.notificationContentUsername(snsUserFollow.getFollowerUser().getUsername())
			.notificationContentUserProfilePath(snsUserFollow.getFollowerUser().getProfilePath())
			// 알림 메시지 내용
			.snsNotificationContents(snsNotificationContents)
			.build();
	}
}
