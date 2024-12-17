package com.postvue.feelogserver.app.messagequeue.service.producer;

import java.time.LocalDateTime;

import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.postvue.feelogserver.app.messagequeue.dto.VideoUploadConversionMessage;
import com.postvue.feelogserver.domain.snsposts.vo.SnsPostContent;
import com.postvue.feelogserver.global.constant.LogTemplateConst;
import com.postvue.feelogserver.global.constant.RabbitMQConst;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class VideoConversationProducer {
	private final AmqpTemplate amqpTemplate;
	public void sendVideoConversionUploadToQueue(
		Long postId,
		String videoAbsolutePath,
		SnsPostContent snsPostContent) throws JsonProcessingException {
		VideoUploadConversionMessage videoUploadConversionMessage = new VideoUploadConversionMessage(postId, videoAbsolutePath, snsPostContent.getContent(), snsPostContent.getPreviewImg());

		// ObjectMapper 생성
		ObjectMapper objectMapper = new ObjectMapper();

		// 객체를 JSON으로 변환
		String videoUploadConversionMessageString = objectMapper.writeValueAsString(videoUploadConversionMessage);
		amqpTemplate.convertAndSend(RabbitMQConst.RABBIT_MQ_VIDEO_EXCHANGE, RabbitMQConst.RABBIT_MQ_VIDEO_CONVERT_UPLOAD_DIRECT_ROUTE_KEY, videoUploadConversionMessageString);
		log.info(LogTemplateConst.getLogInfoTemplate(String.format("Video conversion request sent: %s", videoUploadConversionMessageString),
			LocalDateTime.now().toString()));
	}
}
