package com.postvue.feelogserver.app.notifications.service;

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
import com.postvue.feelogserver.domain.snspostcommentreactions.dao.PostReplyNumDao;
import com.postvue.feelogserver.domain.snspostcommentreactions.repository.SnsPostCommentReactionRepository;
import com.postvue.feelogserver.domain.snsposts.SnsPost;
import com.postvue.feelogserver.domain.snsusers.SnsUser;
import com.postvue.feelogserver.global.constant.SnsNotificationConst;
import com.postvue.feelogserver.global.constant.SnsNotificationTemplateConst;
import com.postvue.feelogserver.global.util.generator.NotificationUtils;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PostCommentReplyNotificationProcessService
	implements NotificationProcessServiceInterface<SnsPost, SnsPostCommentReaction, SnsNotification> {
	private final SnsNotificationRepository snsNotificationRepository;
	private final SnsPostCommentReactionRepository snsPostCommentReactionRepository;
	private final NotificationServiceTemplate notificationServiceTemplate;

	@Override
	@Transactional
	public void processNotification(SnsPost snsPost, SnsPostCommentReaction snsPostCommentReaction) {
		Optional<PostReplyNumDao> postReplyNumDaoOpt = snsPostCommentReactionRepository.findReplyNumByCommentWithoutMe(
			snsPost.getId(), snsPost.getSnsUser().getId());

		Integer postReplyNum = postReplyNumDaoOpt.isPresent() ? postReplyNumDaoOpt.get().getReplyNum() :
			SnsNotificationConst.ZERO_NOTIFICATION_NUM;

		boolean needNotification = NotificationUtils.isNotificationInSequence(postReplyNum,
			SnsNotificationConst.POST_COMMENT_REPLY_NOTIFICATION_MIN_SEQUENCE_NUM,
			SnsNotificationConst.POST_COMMENT_REPLY_NOTIFICATION_SEQUENCE_NUM);
		if (!needNotification) {
			return;
		}

		// 이미 같은 알림이 있는 경우 알림하지 않음
		if (getHasAlreadyNotification(snsPost, postReplyNum)) {
			return;
		}

		SnsNotification snsNotification = saveNotification(snsPost, snsPostCommentReaction, postReplyNum);
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
			SnsNotificationType.POST_COMMENT_REPLY_NOTIFICATION);
	}

	@Transactional
	// 특정 조건 일떄, 알림 저장
	@Override
	public SnsNotification saveNotification(SnsPost snsPost, SnsPostCommentReaction snsPostCommentReaction,
		Integer notificationCount) {

		SnsUser snsUser = snsPostCommentReaction.getCommentUser();

		List<SnsNotificationContent> snsNotificationContentList = Arrays.asList(
			SnsNotificationContent.builder() // 알림 메시지 내용
				.snsNotificationContentType(SnsNotificationContentType.TEXT)
				.snsNotificationContent(
					SnsNotificationTemplateConst.getPostLikeNotificationMessage(snsUser.getUsername(),
						notificationCount))
				.build(),
			SnsNotificationContent.builder()
				// 답글 메시지 내용
				// 현재 댓글의 경우 텍스트만 보여주도록 함 => 나중에 이미지나 비디오 도 고려 필요
				.snsNotificationContentType(SnsNotificationContentType.TEXT)
				.snsNotificationContent(snsPostCommentReaction.getCommentMsg())
				.build()
		);

		if (snsPostCommentReaction.getCommentMediaType() != null && snsPostCommentReaction.getCommentMediaContent() != null){
			snsNotificationContentList.add(
				SnsNotificationContent.builder()
					.snsNotificationContentType(
						switch (snsPostCommentReaction.getCommentMediaType()){
							case IMAGE -> SnsNotificationContentType.IMAGE;
							case VIDEO -> SnsNotificationContentType.VIDEO;
							case NONE -> SnsNotificationContentType.NONE;
						}
					)
					.snsNotificationContent(
						SnsNotificationTemplateConst.getPostCommentNotificationMessage(snsUser.getUsername(),
							notificationCount))
					.build());
		}

		SnsNotification snsNotification = notificationServiceTemplate.convertPostNotification(snsPost,
			snsUser,
			SnsNotificationType.POST_COMMENT_REPLY_NOTIFICATION, snsNotificationContentList, notificationCount);

		return snsNotificationRepository.save(snsNotification);
	}

}
