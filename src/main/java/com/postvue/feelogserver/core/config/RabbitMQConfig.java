package com.postvue.feelogserver.core.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.postvue.feelogserver.global.constant.RabbitMQConst;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
@EnableRabbit
public class RabbitMQConfig {
	// Queue 설정
	@Bean
	public Queue videoConversionQueue() {
		return QueueBuilder.durable(RabbitMQConst.RABBIT_MQ_VIDEO_QUEUE)
			.withArgument("x-dead-letter-exchange", RabbitMQConst.RABBIT_MQ_VIDEO_DLX_EXCHANGE)
			.withArgument("x-message-ttl", 30000) // 30초 TTL 설정
			.build();
	}
	@Bean
	public Queue postImageUploadConversionQueue() {
		return QueueBuilder.durable(RabbitMQConst.RABBIT_MQ_POST_IMAGE_UPLOAD_QUEUE)
			.withArgument("x-dead-letter-exchange", RabbitMQConst.RABBIT_MQ_POST_IMAGE_UPLOAD_DLX_EXCHANGE)
			.withArgument("x-message-ttl", 30000) // 30초 TTL 설정
			.build();
	}

	// _ Dead Letter Queue
	@Bean
	public Queue videoConversionDLXQueue() {
		return new Queue(RabbitMQConst.RABBIT_MQ_VIDEO_DLX_QUEUE, true);
	}
	@Bean
	public Queue postImageUploadConversionDLXQueue() {
		return new Queue(RabbitMQConst.RABBIT_MQ_POST_IMAGE_UPLOAD_DLX_QUEUE, true);
	}

	// _ Parking Lot Queue
	@Bean
	public Queue parkingLotQueue() {
		return new Queue(RabbitMQConst.RABBIT_MQ_PARKING_LOT_QUEUE, true);
	}


	// Exchange 설정
	@Bean
	public DirectExchange videoConversionExchange() {
		return new DirectExchange(RabbitMQConst.RABBIT_MQ_VIDEO_EXCHANGE);
	}
	@Bean
	public DirectExchange postImageUploadConversionExchange() {
		return new DirectExchange(RabbitMQConst.RABBIT_MQ_POST_IMAGE_UPLOAD_EXCHANGE);
	}

	// _ Dead Letter Exchange 설정
	@Bean
	public TopicExchange videoConversionDeadLetterExchange() {
		return new TopicExchange(RabbitMQConst.RABBIT_MQ_VIDEO_DLX_EXCHANGE);
	}
	@Bean
	public TopicExchange postImageUploadConversionDeadLetterExchange() {
		return new TopicExchange(RabbitMQConst.RABBIT_MQ_POST_IMAGE_UPLOAD_DLX_EXCHANGE);
	}

	// _ Parking Lot Exchange 설정
	@Bean
	TopicExchange parkingLotExchange() {
		return new TopicExchange(RabbitMQConst.RABBIT_MQ_PARKING_LOT_EXCHANGE);
	}

	// Binding 설정
	@Bean
	public Binding videoConversionBinding(Queue videoConversionQueue, DirectExchange videoConversionExchange) {
		return BindingBuilder.bind(videoConversionQueue).to(videoConversionExchange)
			.with(RabbitMQConst.RABBIT_MQ_VIDEO_CONVERT_UPLOAD_DIRECT_ROUTE_KEY);
	}
	@Bean
	public Binding postImageUploadConversionBinding(Queue postImageUploadConversionQueue, DirectExchange postImageUploadConversionExchange) {
		return BindingBuilder.bind(postImageUploadConversionQueue).to(postImageUploadConversionExchange)
			.with(RabbitMQConst.RABBIT_MQ_POST_IMAGE_UPLOAD_DIRECT_ROUTE_KEY);
	}

	// Dead Letter Queue와 Exchange를 연결하는 Binding 설정
	@Bean
	public Binding videoConversionDLXBinding(Queue videoConversionDLXQueue, TopicExchange videoConversionDeadLetterExchange) {
		return BindingBuilder.bind(videoConversionDLXQueue).to(videoConversionDeadLetterExchange)
			.with(RabbitMQConst.RABBIT_MQ_DEAD_LETTER_VIDEO_ROUTE_KEY);
	}
	@Bean
	public Binding postImageUploadConversionDLXBinding(Queue postImageUploadConversionDLXQueue, TopicExchange postImageUploadConversionDeadLetterExchange) {
		return BindingBuilder.bind(postImageUploadConversionDLXQueue).to(postImageUploadConversionDeadLetterExchange)
			.with(RabbitMQConst.RABBIT_MQ_DEAD_LETTER_POST_IMAGE_UPLOAD_ROUTE_KEY);
	}

	// Parking: Queue -> Exchange
	@Bean
	Binding parkingLotBinding(Queue parkingLotQueue, TopicExchange parkingLotExchange) {
		return BindingBuilder.bind(parkingLotQueue).to(parkingLotExchange)
			.with(RabbitMQConst.RABBIT_MQ_PARKING_LOT_TOPIC_ROUTE_KEY);
	}

	// Template 설정
	@Bean
	RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory, MessageConverter jackson2JsonMessageConverter) {
		RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
		rabbitTemplate.setExchange(RabbitMQConst.RABBIT_MQ_VIDEO_QUEUE);
		rabbitTemplate.setMessageConverter(jackson2JsonMessageConverter);
		rabbitTemplate.setMandatory(true);
		// 메시지가 브로커에 도착했지만 지정된 큐로 라우팅되지 못한 경우
		rabbitTemplate.setReturnsCallback((returnedMessage) -> {
			log.info("routingKey: {}, replyText: {}", returnedMessage.getRoutingKey(), returnedMessage.getReplyText());
		});
		return rabbitTemplate;
	}

	@Bean
	public MessageConverter jackson2JsonMessageConverter() {
		return new Jackson2JsonMessageConverter();
	}

}
