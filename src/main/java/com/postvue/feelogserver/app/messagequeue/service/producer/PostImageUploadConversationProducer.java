package com.postvue.feelogserver.app.messagequeue.service.producer;

import java.time.LocalDateTime;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.postvue.feelogserver.app.posts.dto.req.create.admin.PostImageUploadConversationMessageQDto;
import com.postvue.feelogserver.endpoint.dto.PostImageUploadDto;
import com.postvue.feelogserver.global.constant.LogTemplateConst;
import com.postvue.feelogserver.global.constant.RabbitMQConst;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class PostImageUploadConversationProducer {
	private final RabbitTemplate rabbitTemplate;
	private final ObjectMapper objectMapper;
	public void sendPostImageUploadToQueue(
		PostImageUploadConversationMessageQDto postImageUploadConversationMessageQDto
	) throws JsonProcessingException {
		// ObjectMapper 생성

		// 객체를 JSON으로 변환
		// 메시지 객체 생성
		String postImageUploadDtoString = objectMapper.writeValueAsString(postImageUploadConversationMessageQDto);

		Message message = MessageBuilder.withBody(postImageUploadDtoString.getBytes())
			.setContentType("application/json")
			.build();

		rabbitTemplate.send(RabbitMQConst.RABBIT_MQ_POST_IMAGE_UPLOAD_EXCHANGE,
			RabbitMQConst.RABBIT_MQ_POST_IMAGE_UPLOAD_DIRECT_ROUTE_KEY, message);
		log.info(LogTemplateConst.getLogInfoTemplate(String.format("Post Image conversion request sent: %s", postImageUploadDtoString),
			LocalDateTime.now().toString()));
	}
}
