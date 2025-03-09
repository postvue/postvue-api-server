package com.postvue.feelogserver.app.messagequeue.handler;


import java.io.IOException;
import java.util.Date;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import com.postvue.feelogserver.app.messagequeue.header.RabbitmqHeader;
import com.postvue.feelogserver.global.constant.RabbitMQConst;
import com.rabbitmq.client.Channel;
import com.vaadin.hilla.Nonnull;

import lombok.Getter;

public class DlxProcessingErrorHandler {
	private final RabbitTemplate rabbitTemplate;
	private static final Logger log = LoggerFactory.getLogger(DlxProcessingErrorHandler.class);

	// Dead Letter Exchange의 이름
	@Getter
	@Nonnull
	private String deadExchangeName;

	// 메시지 재처리 최대 횟수
	@Getter
	private int maxRetryCount = RabbitMQConst.MAX_RETRY_COUNT;

	public DlxProcessingErrorHandler(String deadExchangeName, RabbitTemplate rabbitTemplate) throws IllegalAccessError {
		super();

		if (StringUtils.isAnyEmpty(deadExchangeName)) {
			throw new IllegalArgumentException("Must define dlx exchange name");
		}

		this.rabbitTemplate = rabbitTemplate;
		this.deadExchangeName = deadExchangeName;
	}

	public DlxProcessingErrorHandler(String deadExchangeName, RabbitTemplate rabbitTemplate, int maxRetryCount) {
		this(deadExchangeName, rabbitTemplate);
		setMaxRetryCount(maxRetryCount);
	}

	public boolean handleErrorProcessingMessage(Message message, Channel channel) {
		var rabbitMqHeader = new RabbitmqHeader(message.getMessageProperties().getHeaders());

		try {
			log.warn("[DEAD] Error at " + new Date() + " on retry " + rabbitMqHeader.getFailedRetryCount()
				+ " for message " + message);

			// 3번이 넘으면 DLX로 메시지 전달
			if (rabbitMqHeader.getFailedRetryCount() >= maxRetryCount) {
				channel.basicPublish(
					getDeadExchangeName(), message.getMessageProperties().getReceivedRoutingKey(),
					null, message.getBody()
				);

				// Parking Lot로 메시지를 보내고 ACK
				rabbitTemplate.send(RabbitMQConst.RABBIT_MQ_PARKING_LOT_EXCHANGE, RabbitMQConst.RABBIT_MQ_PARKING_LOT_VIDEO_ROUTE_KEY, message);
				channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
			}
			else {
				log.debug("[REQUEUE] Error at " + new Date() + " on retry " + rabbitMqHeader.getFailedRetryCount()
					+ " for message " + message);

				// 메시지를 DLQ 큐로 반환 (재시도),
				// false: 메시지가 거부되면 더 이상 큐에 남지 않으며 삭제됩니다.
				// 이 경우, 메시지는 실패 처리되거나 DLQ(Dead Letter Queue)로 전송

				channel.basicReject(message.getMessageProperties().getDeliveryTag(), false);
			}
			return true;
		}
		catch (IOException e) {
			log.warn("[HANDLER-FAILED] Error at " + new Date() + " on retry " + rabbitMqHeader.getFailedRetryCount()
				+ " for message " + message);
		}
		catch (Exception e){
			log.error(e.getMessage());
		}

		return false;
	}

	public void setMaxRetryCount(int maxRetryCount) throws IllegalArgumentException{
		if (maxRetryCount > 1000) {
			throw new IllegalArgumentException("max retry must between 0 ~ 1000");
		}

		this.maxRetryCount = maxRetryCount;
	}
}
