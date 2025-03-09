package com.postvue.feelogserver.app.notifications.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.postvue.feelogserver.app.notifications.dto.ws.sub.SnsNotificationSub;
import com.postvue.feelogserver.domain.snsnotifications.SnsNotification;
import com.postvue.feelogserver.domain.snsnotifications.repository.SnsNotificationJdbcRepository;
import com.postvue.feelogserver.domain.snsnotifications.repository.SnsNotificationRepository;
import com.postvue.feelogserver.domain.snspostcommentreactions.SnsPostCommentReaction;
import com.postvue.feelogserver.domain.snsposts.SnsPost;
import com.postvue.feelogserver.domain.snspostuserreactions.SnsPostUserReaction;
import com.postvue.feelogserver.domain.snsuserfollows.SnsUserFollow;
import com.postvue.feelogserver.domain.snsusers.repository.SnsUserRepository;
import com.postvue.feelogserver.global.constant.SnsNotificationConst;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class NotificationService {
	private final SnsNotificationRepository snsNotificationRepository;
	private final SnsNotificationJdbcRepository snsNotificationJdbcRepository;
	private final PostLikeNotificationProcessService postLikeNotificationProcessService;
	private final PostClipNotificationProcessService postClipNotificationProcessService;
	private final PostCommentNotificationProcessService postCommentNotificationProcessService;
	private final PostCommentReplyNotificationProcessService postCommentReplyNotificationProcessService;
	private final FollowerNotificationProcessService followerNotificationProcessService;

	public List<SnsNotificationSub> getNotificationList(Long snsUserId, LocalDateTime notifiedDateTime) {
		List<SnsNotification> snsNotificationList = snsNotificationRepository.findNotificationByIdAndAfterNotifiedAt(
			snsUserId, notifiedDateTime);
		return snsNotificationList.stream().map((snsNotification -> {
			return SnsNotificationSub.builder()
				.notificationId(snsNotification.getId().toString())
				.userId(snsNotification.getSnsUser().getId().toString())
				.username(snsNotification.getUsername())
				.postId(snsNotification.getSnsPost() != null ? snsNotification.getSnsPost().getId().toString() :
					null)
				.notificationUserId(snsNotification.getNotificationContentUserId().toString())
				.notificationUsername(snsNotification.getNotificationContentUsername())
				.isRead(false)
				.notificationUserProfilePath(snsNotification.getNotificationContentUserProfilePath())
				.notifiedAt(snsNotification.getCreatedAt())
				.notificationType(snsNotification.getSnsNotificationType().toString())
				.notificationContents(snsNotification.getSnsNotificationContents())
				.build();
		})).toList();
	}

	@Transactional
	public boolean deleteNotificationList(LocalDateTime daysAgo) {
		List<SnsNotification> snsNotificationList = snsNotificationRepository.findNotificationsOlderThanDays(daysAgo);

		snsNotificationJdbcRepository.deleteAll(snsNotificationList);
		return true;
	}

	public void processPostLikeNotification(SnsPost snsPost, SnsPostUserReaction snsPostUserReaction) {
		postLikeNotificationProcessService.processNotification(snsPost, snsPostUserReaction);
	}

	public void processPostClipNotification(SnsPost snsPost, SnsPostUserReaction snsPostUserReaction) {
		postClipNotificationProcessService.processNotification(snsPost, snsPostUserReaction);
	}

	@Transactional
	public void processPostCommentNotification(SnsPost snsPost, SnsPostCommentReaction snsPostCommentReaction) {
		postCommentNotificationProcessService.processNotification(snsPost, snsPostCommentReaction);
	}

	public void processPostCommentReplyNotification(SnsPost snsPost, SnsPostCommentReaction snsPostCommentReaction) {
		postCommentReplyNotificationProcessService.processNotification(snsPost, snsPostCommentReaction);
	}

	public void processFollowerNotification(SnsUserFollow snsUserFollow) {
		followerNotificationProcessService.processNotification(snsUserFollow, null);
	}

}
