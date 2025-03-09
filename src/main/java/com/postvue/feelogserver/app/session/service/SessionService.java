package com.postvue.feelogserver.app.session.service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Service;

import com.postvue.feelogserver.app.session.dto.sub.SessionActiveUserInfoSub;
import com.postvue.feelogserver.app.session.dto.sub.SessionActiveUserListSub;
import com.postvue.feelogserver.domain.sessionsnsactiveusers.SessionSnsActiveUser;
import com.postvue.feelogserver.domain.sessionsnsactiveusers.repository.SessionSnsActiveUserRepository;
import com.postvue.feelogserver.domain.snsuserfollows.SnsUserFollow;
import com.postvue.feelogserver.domain.snsuserfollows.repository.SnsUserFollowRepository;
import com.postvue.feelogserver.global.constant.WebSocketPathConst;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SessionService {
	private final SnsUserFollowRepository snsUserFollowRepository;

	private final SimpMessagingTemplate messageTemplate;

	private final SessionSnsActiveUserRepository sessionSnsActiveUserRepository;

	public List<String> getFollowers(Long myUserId) {
		List<SnsUserFollow> snsUserFollowList = snsUserFollowRepository
			.findAllFollowerList(myUserId);

		return snsUserFollowList.stream().map((snsUserFollow -> {
			if (Objects.equals(snsUserFollow.getFollowingUser().getId(), myUserId)) {
				return snsUserFollow.getFollowerUser().getId().toString();
			} else {
				return snsUserFollow.getFollowingUser().getId().toString();
			}
		})).toList();
	}

	public void notifyFollowers(Long userId, SessionSnsActiveUser snsActiveUser) {
		List<String> followers = getFollowers(userId);
		String destination = WebSocketPathConst.SESSION_BROKER_PATH;

		// 팔로우에게 내 정보 받기
		for (String follower : followers) {
			messageTemplate.convertAndSend(destination + "/" + follower,
				SessionActiveUserListSub.builder()
					.sessionActiveUserInfoSubList(
						Collections.singletonList(
							SessionActiveUserInfoSub.builder()
								.sessionState(snsActiveUser.getSessionStatus())
								.userId(userId.toString())
								.lastActivityDateTime(snsActiveUser.getLastActivityDateTime())
								.build()
						)
					)
					.build());
		}
	}

	public SessionSnsActiveUser updateSessionActiveUser(StompHeaderAccessor accessor, Long userId,
		Boolean sessionState) {
		// Session DB(Redis DB)에 해당 유저에 대한 sessionId 정보 남기기
		SessionSnsActiveUser sessionSnsActiveUser = sessionSnsActiveUserRepository.findById(userId).orElse(
			SessionSnsActiveUser.builder()
				.userId(userId)
				.build()
		);
		sessionSnsActiveUser.setSessionId(accessor.getSessionId());
		sessionSnsActiveUser.setLastActivityDateTime(LocalDateTime.now());
		sessionSnsActiveUser.setSessionStatus(sessionState);
		return sessionSnsActiveUserRepository.save(sessionSnsActiveUser);
	}

}
