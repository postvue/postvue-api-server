package com.postvue.feelogserver.core.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.postvue.feelogserver.global.constant.RabbitMQConst;


@Configuration
@EnableRabbit
public class RabbitMQConfig {
	@Bean
	public MessageConverter jsonMessageConverter() {
		return new Jackson2JsonMessageConverter();
	}

	@Bean
	public Queue videoConversionQueue() {
		return new Queue(RabbitMQConst.RABBIT_MQ_VIDEO_QUEUE, true);
	}

	@Bean
	public DirectExchange videoConversionExchange() {
		return new DirectExchange(RabbitMQConst.RABBIT_MQ_VIDEO_EXCHANGE);
	}

	@Bean
	public Binding videoConversionBinding(Queue videoConversionQueue, DirectExchange videoConversionExchange) {
		return BindingBuilder.bind(videoConversionQueue).to(videoConversionExchange).with(RabbitMQConst.RABBIT_MQ_VIDEO_CONVERT_UPLOAD_DIRECT_ROUTE_KEY);
	}
}
