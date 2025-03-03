package com.postvue.feelogserver.endpoint.dto;

import java.time.LocalDateTime;

import com.postvue.feelogserver.domain.snsnotifications.SnsNotification;
import com.postvue.feelogserver.domain.snsnotifications.vo.SnsNotificationType;
import com.postvue.feelogserver.global.util.converter.JsonConverter;

public record SnsNotificationEndpointDto(
	String id,
	SnsNotificationType snsNotificationType,

	String snsUser_id,
	String username,
	String snsPost_id,
	String followerUser_username,
	String notificationContentUserid,
	String notificationContentUsername,
	String snsNotificationContents,
	Integer notificationCount,

	LocalDateTime createdAt
){

	public static SnsNotificationEndpointDto fromEntity(SnsNotification snsNotification){
		return new SnsNotificationEndpointDto(
		snsNotification.getId().toString(),
			snsNotification.getSnsNotificationType(),
			snsNotification.getSnsUser() != null ? snsNotification.getSnsUser().getId().toString() : null,
			snsNotification.getUsername(),
			snsNotification.getSnsPost() != null ? snsNotification.getSnsPost().getId().toString() : null,
			snsNotification.getFollowerUser() != null ? snsNotification.getFollowerUser().getUsername() : null,
			snsNotification.getNotificationContentUserid() !=null ? snsNotification.getNotificationContentUserid().toString() : null,
			snsNotification.getNotificationContentUsername() !=null ?snsNotification.getNotificationContentUsername() : null,
			JsonConverter.convertToJsonString(snsNotification.getSnsNotificationContents()),
			snsNotification.getNotificationCount(),
			snsNotification.getCreatedAt()
			);
	}
}
