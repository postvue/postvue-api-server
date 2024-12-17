package com.postvue.feelogserver.global.util.converter;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;

import com.postvue.feelogserver.global.constant.SnsNotificationConst;

public class DateConvertor {
	public static LocalDateTime parseOrDefault(String dateString) {
		try {
			return LocalDateTime.parse(dateString);
		} catch (DateTimeParseException e) {
			return LocalDateTime.now().minusDays(SnsNotificationConst.MAX_NOTIFICATION_MSG_RETENTION_DAY);
		}
	}
}
