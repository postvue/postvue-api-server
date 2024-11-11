package com.postvue.feelogserver.domain.snsnotifications.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
@AllArgsConstructor
public class SnsNotificationContent {
	private SnsNotificationContentType snsNotificationContentType;
	private String snsNotificationContent;
}
