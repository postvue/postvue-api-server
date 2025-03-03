package com.postvue.feelogserver.app.notifications.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.postvue.feelogserver.domain.snsnotifications.SnsNotification;
import com.postvue.feelogserver.domain.snsnotifications.repository.SnsNotificationRepository;
import com.postvue.feelogserver.domain.snsnotifications.vo.SnsNotificationContent;
import com.postvue.feelogserver.domain.snsnotifications.vo.SnsNotificationContentType;
import com.postvue.feelogserver.domain.snsnotifications.vo.SnsNotificationType;
import com.postvue.feelogserver.domain.snspostcommentreactions.SnsPostCommentReaction;
import com.postvue.feelogserver.domain.snspostcommentreactions.dao.PostCommentNumDao;
import com.postvue.feelogserver.domain.snspostcommentreactions.repository.SnsPostCommentReactionRepository;
import com.postvue.feelogserver.domain.snspostcommentreactions.vo.PostCommentMediaType;
import com.postvue.feelogserver.domain.snsposts.SnsPost;
import com.postvue.feelogserver.domain.snsusers.SnsUser;
import com.postvue.feelogserver.global.constant.SnsNotificationConst;
import com.postvue.feelogserver.global.constant.SnsNotificationTemplateConst;
import com.postvue.feelogserver.global.util.generator.NotificationUtils;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PostCommentNotificationProcessService
	implements NotificationProcessServiceInterface<SnsPost, SnsPostCommentReaction, SnsNotification> {
	private final SnsNotificationRepository snsNotificationRepository;
	private final SnsPostCommentReactionRepository snsPostCommentReactionRepository;
	private final NotificationServiceTemplate notificationServiceTemplate;

	@Override
	@Transactional
	public void processNotification(SnsPost snsPost, SnsPostCommentReaction snsPostCommentReaction) {
		Optional<PostCommentNumDao> postCommentNumDaoOpt = snsPostCommentReactionRepository.findCommentNumWithoutMe(
			snsPost.getId(), snsPost.getSnsUser().getId());

		Integer postCommentNum = postCommentNumDaoOpt.isPresent() ? postCommentNumDaoOpt.get().getCommentNum() :
			SnsNotificationConst.ZERO_NOTIFICATION_NUM;

		boolean needNotification = NotificationUtils.isNotificationInSequence(postCommentNum,
			SnsNotificationConst.POST_COMMENT_NOTIFICATION_MIN_SEQUENCE_NUM,
			SnsNotificationConst.POST_COMMENT_NOTIFICATION_SEQUENCE_NUM,
			postCommentNum.equals(SnsNotificationConst.POST_COMMENT_NOTIFICATION_MIN_NUM));

		// 알림 카운터가 충족되지 않을 때는 알림하지 않음
		if (!needNotification) {
			return;
		}

		// 이미 같은 알림이 있는 경우 알림하지 않음
		if (getHasAlreadyNotification(snsPost, postCommentNum)) {
			return;
		}

		SnsNotification snsNotification = saveNotification(snsPost, snsPostCommentReaction, postCommentNum);
		sendNotification(snsNotification);
	}

	// 이미 같은 알림이 있는 지
	public Boolean getHasAlreadyNotification(SnsPost snsPost, Integer notificationCount) {
		return notificationServiceTemplate.getHasAlreadyNotificationByPost(snsPost, notificationCount,
			SnsNotificationType.POST_COMMENT_NOTIFICATION);
	}

	// 유저가 활성화 상태일 경우, 알림 전달
	@Override
	public void sendNotification(SnsNotification snsNotification) {
		notificationServiceTemplate.sendNotification(snsNotification);
	}

	// 특정 조건 일떄, 알림 저장
	@Transactional
	@Override
	public SnsNotification saveNotification(SnsPost snsPost, SnsPostCommentReaction snsPostCommentReaction,
		Integer notificationCount) {


		SnsUser snsUser = snsPostCommentReaction.getCommentUser();

		List<SnsNotificationContent> snsNotificationContentList = new ArrayList<>(Arrays.asList(
			SnsNotificationContent.builder()
				.snsNotificationContentType(SnsNotificationContentType.TEXT)
				.snsNotificationContent(
					SnsNotificationTemplateConst.getPostCommentNotificationMessage(snsUser.getUsername(),
						notificationCount))
				.build(),
			SnsNotificationContent.builder()
				.snsNotificationContentType(SnsNotificationContentType.TEXT)
				.snsNotificationContent(snsPostCommentReaction.getCommentMsg())
				.build()
		));

		if (snsPostCommentReaction.getCommentMediaType() != null
			&& snsPostCommentReaction.getCommentMediaType() == PostCommentMediaType.IMAGE
			&& snsPostCommentReaction.getCommentMediaContent() != null){
			snsNotificationContentList.add(
				SnsNotificationContent.builder()
					.snsNotificationContentType(SnsNotificationContentType.IMAGE)
					.snsNotificationContent(
						SnsNotificationTemplateConst.getPostCommentNotificationMessage(snsUser.getUsername(),
							notificationCount))
					.build());
		}
		SnsNotification snsNotification = notificationServiceTemplate.convertPostNotification(snsPost,
			snsUser,
			SnsNotificationType.POST_COMMENT_NOTIFICATION, snsNotificationContentList, notificationCount);

		return snsNotificationRepository.save(snsNotification);
	}

}
