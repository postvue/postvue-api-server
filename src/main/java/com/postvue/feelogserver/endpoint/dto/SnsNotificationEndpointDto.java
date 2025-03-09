package com.postvue.feelogserver.endpoint.dto;

import com.postvue.feelogserver.domain.snsnotifications.SnsNotification;
import com.postvue.feelogserver.domain.snsnotifications.vo.SnsNotificationContent;
import com.postvue.feelogserver.domain.snsnotifications.vo.SnsNotificationType;
import com.postvue.feelogserver.global.util.converter.JsonConverter;

public record SnsNotificationEndpointDto(
	String id,
	SnsNotificationType snsNotificationType,
	String username,
	String snsPost_id,
	String followerUser_username,
	String notificationContentUser_id,
	String notificationContentUser_username,
	String snsNotificationContents,
	Integer notificationCount){

	public static SnsNotificationEndpointDto fromEntity(SnsNotification snsNotification){
		return new SnsNotificationEndpointDto(
		snsNotification.getId().toString(),
			snsNotification.getSnsNotificationType(),
			snsNotification.getUsername(),
			snsNotification.getSnsPost() != null ? snsNotification.getSnsPost().getId().toString() : null,
			snsNotification.getFollowerUser() != null ? snsNotification.getFollowerUser().getUsername() : null,
			snsNotification.getNotificationContentUserId().toString(),
			snsNotification.getNotificationContentUsername(),
			JsonConverter.convertToJsonString(snsNotification.getSnsNotificationContents()),
			snsNotification.getNotificationCount()
			);
	}
}
