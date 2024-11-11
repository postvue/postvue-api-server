package com.postvue.feelogserver.app.notifications.service;

public interface NotificationProcessServiceInterface<A, UR, SAVE_RETURN> {

	void processNotification(A object, UR ur);

	void sendNotification(SAVE_RETURN saveReturn);

	Boolean getHasAlreadyNotification(A object, Integer count);

	SAVE_RETURN saveNotification(A aObject, UR bObject, Integer notificationCount);
}
