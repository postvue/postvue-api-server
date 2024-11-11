package com.postvue.feelogserver.core.security;

import java.util.Objects;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageDeliveryException;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.core.Authentication;

import com.postvue.feelogserver.core.security.exception.JwtTokenExpiredException;
import com.postvue.feelogserver.core.security.exception.JwtTokenValidException;
import com.postvue.feelogserver.global.constant.WebSocketPathConst;
import com.postvue.feelogserver.global.constant.WebSocketStompErrorConst;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class StompHandler implements ChannelInterceptor {
	private final JwtTokenProvider jwtTokenProvider;
	private final JwtAuthenticationFilter jwtAuthenticationFilter;

	@Override
	public Message<?> preSend(Message<?> message, MessageChannel channel) {

		StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

		if (accessor == null) {
			throw new MessageDeliveryException(WebSocketStompErrorConst.EXCEPTION_ERROR_STOMP_DELIVERY_MESSAGE);
		}

		if (StompCommand.CONNECT == accessor.getCommand()) {
			String bearerTokenHeader = accessor.getFirstNativeHeader(HttpHeaders.AUTHORIZATION);

			String accessToken = jwtAuthenticationFilter.getToken(bearerTokenHeader);

			try {
				jwtTokenProvider.validateToken(accessToken);
				Authentication authentication = jwtTokenProvider.getAuthentication(accessToken);
				accessor.setUser(authentication);
			} catch (JwtTokenExpiredException | JwtTokenValidException ex) {
				throw new MessageDeliveryException(
					WebSocketStompErrorConst.ACCESS_TOKEN_EXPIRED_ERROR_STOMP_DELIVERY_MESSAGE);
			} catch (Exception ex) {
				throw new MessageDeliveryException(WebSocketStompErrorConst.EXCEPTION_ERROR_STOMP_DELIVERY_MESSAGE);
			}

		} else if (StompCommand.SUBSCRIBE == accessor.getCommand()) {
			Authentication auth = (Authentication)accessor.getUser();

			String destination = accessor.getDestination();

			if (destination == null) {
				throw new MessageDeliveryException(
					WebSocketStompErrorConst.NOT_DESTINATION_EXCEPTION_ERROR_STOMP_DELIVERY_MESSAGE);
			}

			int lastIndex = destination.lastIndexOf('/');
			String basePath = destination.substring(0, lastIndex);
			Long sessionUserId = Long.parseLong(destination.substring(lastIndex + 1));

			if (!WebSocketPathConst.BROKER_URL_LIST.contains(basePath)) {
				throw new MessageDeliveryException("해당 url 경로는 존재하지 않습니다.");
			}

			CustomUserDetails userDetails = auth != null ? (CustomUserDetails)auth.getPrincipal() : null;

			Long realUserId = (userDetails == null) ? null : Long.valueOf(userDetails.getUserId());

			if (!Objects.equals(realUserId, sessionUserId)) {
				throw new MessageDeliveryException("권한이 없습니다.");
			}

		} else if (StompCommand.SEND == accessor.getCommand()) {
			String bearerTokenHeader = accessor.getFirstNativeHeader(HttpHeaders.AUTHORIZATION);

			String accessToken = jwtAuthenticationFilter.getToken(bearerTokenHeader);

			jwtTokenProvider.validateToken(accessToken);
			Authentication authentication = jwtTokenProvider.getAuthentication(accessToken);
			accessor.setUser(authentication);
		}
		return ChannelInterceptor.super.preSend(message, channel);
	}
}
