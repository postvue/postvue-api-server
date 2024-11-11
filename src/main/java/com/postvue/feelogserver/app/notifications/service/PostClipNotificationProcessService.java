package com.postvue.feelogserver.app.notifications.service;

import java.util.Collections;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.postvue.feelogserver.domain.snsnotifications.SnsNotification;
import com.postvue.feelogserver.domain.snsnotifications.repository.SnsNotificationRepository;
import com.postvue.feelogserver.domain.snsnotifications.vo.SnsNotificationContent;
import com.postvue.feelogserver.domain.snsnotifications.vo.SnsNotificationContentType;
import com.postvue.feelogserver.domain.snsnotifications.vo.SnsNotificationType;
import com.postvue.feelogserver.domain.snsposts.SnsPost;
import com.postvue.feelogserver.domain.snspostuserreactions.SnsPostUserReaction;
import com.postvue.feelogserver.domain.snspostuserreactions.dao.PostClipNumDao;
import com.postvue.feelogserver.domain.snspostuserreactions.repository.SnsPostUserReactionRepository;
import com.postvue.feelogserver.domain.snsusers.SnsUser;
import com.postvue.feelogserver.global.constant.SnsNotificationConst;
import com.postvue.feelogserver.global.constant.SnsNotificationTemplateConst;
import com.postvue.feelogserver.global.util.generator.NotificationUtils;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PostClipNotificationProcessService
	implements NotificationProcessServiceInterface<SnsPost, SnsPostUserReaction, SnsNotification> {
	private final SnsNotificationRepository snsNotificationRepository;
	private final SnsPostUserReactionRepository snsPostUserReactionRepository;
	private final NotificationServiceTemplate notificationServiceTemplate;

	@Override
	@Transactional
	public void processNotification(SnsPost snsPost, SnsPostUserReaction snsPostUserReaction) {
		Optional<PostClipNumDao> postClipNumDaoOpt = snsPostUserReactionRepository.findClipNumWithoutMe(
			snsPost.getId(), snsPost.getSnsUser().getId());

		Integer postClipNum = postClipNumDaoOpt.isPresent() ? postClipNumDaoOpt.get().getClipCount() :
			SnsNotificationConst.ZERO_NOTIFICATION_NUM;

		boolean needNotification = NotificationUtils.isNotificationInSequence(postClipNum,
			SnsNotificationConst.POST_CLIP_NOTIFICATION_MIN_NUM,
			SnsNotificationConst.POST_CLIP_NOTIFICATION_SEQUENCE_NUM);
		if (!needNotification) {
			return;
		}

		SnsNotification snsNotification = saveNotification(snsPost, snsPostUserReaction, postClipNum);
		sendNotification(snsNotification);
	}

	// 유저가 활성화 상태일 경우, 알림 전달
	@Override
	public void sendNotification(SnsNotification snsNotification) {
		notificationServiceTemplate.sendNotification(snsNotification);
	}

	// 이미 같은 알림이 있는 지
	public Boolean getHasAlreadyNotification(SnsPost snsPost, Integer notificationCount) {
		return notificationServiceTemplate.getHasAlreadyNotificationByPost(snsPost, notificationCount,
			SnsNotificationType.POST_CLIP_NOTIFICATION);
	}

	// 특정 조건 일떄, 알림 저장
	@Override
	public SnsNotification saveNotification(SnsPost snsPost, SnsPostUserReaction snsPostUserReaction,
		Integer notificationCount) {

		SnsUser snsUser = snsPostUserReaction.getSnsUser();

		SnsNotification snsNotification = notificationServiceTemplate.convertPostNotification(
			snsPost,
			snsUser,
			SnsNotificationType.POST_CLIP_NOTIFICATION, Collections.singletonList(
				SnsNotificationContent.builder()
					.snsNotificationContentType(SnsNotificationContentType.TEXT)
					.snsNotificationContent(
						SnsNotificationTemplateConst.getPostClipNotificationMessage(snsUser.getUsername(),
							notificationCount))
					.build()
			), notificationCount);

		return snsNotificationRepository.save(snsNotification);
	}

}
