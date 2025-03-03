package com.postvue.feelogserver.app.session.controller;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.springframework.context.event.EventListener;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.simp.annotation.SubscribeMapping;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import com.postvue.feelogserver.app.messages.service.MessagesService;
import com.postvue.feelogserver.app.session.dto.sub.SessionActiveUserInfoSub;
import com.postvue.feelogserver.app.session.dto.sub.SessionActiveUserListSub;
import com.postvue.feelogserver.app.session.service.SessionService;
import com.postvue.feelogserver.core.security.CustomUserDetails;
import com.postvue.feelogserver.domain.sessionsnsactiveusers.SessionSnsActiveUser;
import com.postvue.feelogserver.domain.sessionsnsactiveusers.repository.SessionSnsActiveUserNativeRepository;
import com.postvue.feelogserver.domain.sessionsnsactiveusers.repository.SessionSnsActiveUserRepository;
import com.postvue.feelogserver.global.constant.WebSocketPathConst;
import com.postvue.feelogserver.global.exception.BadRequestErrorException;
import com.postvue.feelogserver.global.exception.UnauthorizedErrorException;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class SessionController {
	private final MessagesService messagesService;
	private final SessionService sessionService;
	private final SessionSnsActiveUserRepository sessionSnsActiveUserRepository;
	private final SessionSnsActiveUserNativeRepository sessionSnsActiveUserNativeRepository;

	// 연결시
	@EventListener
	public void handleWebSocketConnectListener(SessionConnectedEvent event) {
		StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
		Principal principal = accessor.getUser();

		if (principal instanceof Authentication authentication) {
			CustomUserDetails userDetails = (CustomUserDetails)authentication.getPrincipal();

			Long realUserId = Long.valueOf(userDetails.getUserId());

			// 세션 DB(Redis DB)에 해당 유저에 대한 sessionId 정보 남기기
			sessionService.notifyFollowers(
				realUserId,
				sessionService.updateSessionActiveUser(
					accessor, realUserId, true
				)
			);

		} else {
			throw new UnauthorizedErrorException("인증되지 않은 계정입니다.");
		}

	}

	@EventListener
	public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
		StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());

		Optional<SessionSnsActiveUser> sessionSnsActiveUserOpt = sessionSnsActiveUserRepository.findBySessionId(
			accessor.getSessionId());

		// 세션 DB(Redis DB)에 해당 유저에 대한 sessionId 정보 남기기
		sessionSnsActiveUserOpt.ifPresent(sessionSnsActiveUser -> sessionService.notifyFollowers(
			sessionSnsActiveUser.getUserId(),
			sessionService.updateSessionActiveUser(
				accessor, sessionSnsActiveUser.getUserId(), false
			)
		));
	}

	// @REFER: 코드 분석 필요
	// subscribe: ws://topic/session/:userId
	@SubscribeMapping(WebSocketPathConst.SESSIONS_PATH + "/{userId}")
	public SessionActiveUserListSub getSessionInit(
		@DestinationVariable(value = "userId") Long userId,
		Principal principal
	) {

		// @REFER: 수정 필요
		// Long realUserId = (userDetails == null) ? null : Long.valueOf(userDetails.getUserId());

		if (principal instanceof Authentication) {

			Authentication authentication = (Authentication)principal;
			CustomUserDetails userDetails = (CustomUserDetails)authentication.getPrincipal();

			Long snsUserId = (userDetails == null) ? null : Long.valueOf(userDetails.getUserId());

			if (!Objects.equals(snsUserId, userId)){
				throw new BadRequestErrorException("오류");
			}
			List<String> followers = sessionService.getFollowers(snsUserId);

			List<SessionSnsActiveUser> sessionSnsActiveUsers = new ArrayList<>();
			followers.forEach((follower) -> {
				Optional<SessionSnsActiveUser> sessionSnsActiveUserOpt = sessionSnsActiveUserRepository.findById(
					Long.valueOf(follower));
				sessionSnsActiveUserOpt.ifPresent(sessionSnsActiveUsers::add);
			});

			return SessionActiveUserListSub.builder()
				.sessionActiveUserInfoSubList(
					sessionSnsActiveUsers.stream().map((user) -> {
						return SessionActiveUserInfoSub.builder()
							.userId(user.getUserId().toString())
							.sessionState(user != null ? user.getSessionStatus() : false)
							.lastActivityDateTime(user.getLastActivityDateTime())
							.build();
					}).toList()).build();
		} else {
			throw new UnauthorizedErrorException("인증되지 않은 계정입니다.");
		}
	}
}
