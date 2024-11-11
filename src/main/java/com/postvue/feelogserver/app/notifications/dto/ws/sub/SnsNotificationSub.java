package com.postvue.feelogserver.app.notifications.dto.ws.sub;

import java.time.LocalDateTime;
import java.util.List;

import com.postvue.feelogserver.domain.snsnotifications.vo.SnsNotificationContent;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class SnsNotificationSub {
	private String notificationId;
	private String notificationUserId;
	private String notificationUsername;
	private String notificationUserProfilePath;
	private String notificationType;
	private List<SnsNotificationContent> notificationContents;
	private LocalDateTime notifiedAt;
	private Boolean isRead;
	private String userId;
	private String username;
	private String postId;
}
