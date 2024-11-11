package com.postvue.feelogserver.core.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.server.standard.ServletServerContainerFactoryBean;

import com.postvue.feelogserver.core.security.StompErrorHandler;
import com.postvue.feelogserver.core.security.StompHandler;
import com.postvue.feelogserver.global.constant.WebSocketPathConst;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSocketMessageBroker
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {
	private final StompHandler stompHandler;
	private final StompErrorHandler stompErrorHandler;

	@Value("${app-websocket.registry.endpoint}")
	private String appWebsocketEndpointPath;

	@Value("${app-websocket.registry.messagebroker.heartbeat.clientToServerTime}")
	private Long messageBrokerClientToServerHeartbeatTime;
	@Value("${app-websocket.registry.messagebroker.heartbeat.serverToClientTime}")
	private Long messageBrokerServerToClientHeartbeatTime;
	@Value("${app-websocket.registry.messagebroker.taskSchedular.poolSize}")
	private Integer messageBrokerPoolSize;
	@Value("${app-websocket.registry.messagebroker.taskSchedular.threadNamePrefix}")
	private String websocketThreadNamePrefix;

	@Value("${app-websocket.registry.application.config.maxTextMessageBufferSize}")
	private Integer maxTextMsgBufferSize;
	@Value("${app-websocket.registry.application.config.maxBinaryMessageBufferSize}")
	private Integer maxBinaryMsgBufferSize;

	@Value("${app-websocket.registry.fallback.heartbeatTime}")
	private Long sockJsHeartTime;

	@Override
	public void registerStompEndpoints(StompEndpointRegistry registry) {
		registry.addEndpoint(appWebsocketEndpointPath)
			.setAllowedOrigins("http://localhost:3000")
			.setAllowedOriginPatterns("*");
		// .withSockJS()
		// .setInterceptors(new HttpSessionHandshakeInterceptor())
		// .setHeartbeatTime(sockJsHeartTime); // 25초마다 heartbeat 신호 전송

		// 접속 에러 시, 에러 처리
		registry.setErrorHandler(stompErrorHandler);
	}

	@Override
	public void configureMessageBroker(MessageBrokerRegistry registry) {
		registry.enableSimpleBroker(WebSocketPathConst.MESSAGE_BROKER_PATH)
			.setTaskScheduler(taskScheduler())
			.setHeartbeatValue(new long[] {messageBrokerClientToServerHeartbeatTime,
				messageBrokerServerToClientHeartbeatTime});  // 클라이언트와 서버 간의 heartbeat 주기를 각각 5초로 설정

		WebSocketPathConst.APPLICATION_PATH_LIST.forEach(registry::setApplicationDestinationPrefixes);
	}

	@Bean
	public ServletServerContainerFactoryBean createWebSocketContainer() {
		ServletServerContainerFactoryBean container = new ServletServerContainerFactoryBean();
		container.setMaxTextMessageBufferSize(maxTextMsgBufferSize); // 최대 텍스트 메시지 버퍼 크기 설정
		container.setMaxBinaryMessageBufferSize(maxBinaryMsgBufferSize); // 최대 이진 메시지 버퍼 크기 설정
		return container;
	}

	@Bean
	public TaskScheduler taskScheduler() {
		ThreadPoolTaskScheduler taskScheduler = new ThreadPoolTaskScheduler();
		taskScheduler.setPoolSize(messageBrokerPoolSize);  // 스레드 풀의 크기 설정
		taskScheduler.setThreadNamePrefix(websocketThreadNamePrefix);  // 스레드 이름에 접두사 추가
		taskScheduler.initialize(); // 스케줄러 초기화
		return taskScheduler;
	}

	@Override
	public void configureClientInboundChannel(ChannelRegistration registration) {
		registration.interceptors(stompHandler);
	}
}
