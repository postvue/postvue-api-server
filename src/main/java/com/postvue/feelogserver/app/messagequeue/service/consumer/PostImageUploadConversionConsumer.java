package com.postvue.feelogserver.app.messagequeue.service.consumer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Semaphore;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.postvue.feelogserver.app.posts.dto.req.create.SnsPostComposeCreateReq;
import com.postvue.feelogserver.app.posts.dto.req.create.admin.PostImageUploadConversationMessageQDto;
import com.postvue.feelogserver.app.posts.service.PostsService;
import com.postvue.feelogserver.domain.snsposts.SnsPost;
import com.postvue.feelogserver.domain.snsusers.SnsUser;
import com.postvue.feelogserver.domain.snsusers.repository.SnsUserRepository;
import com.postvue.feelogserver.global.constant.LogTemplateConst;
import com.postvue.feelogserver.global.constant.MediaConfigConst;
import com.postvue.feelogserver.global.constant.RabbitMQConst;
import com.postvue.feelogserver.global.exception.BadRequestErrorException;
import com.postvue.feelogserver.global.util.generator.FileUtils;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.LongString;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class PostImageUploadConversionConsumer {

	private final SnsUserRepository snsUserRepository;
	private final PostsService postsService;
	private final RabbitTemplate rabbitTemplate;
	private final ObjectMapper objectMapper;

	public PostImageUploadConversionConsumer(
		SnsUserRepository snsUserRepository,
		PostsService postsService,
		ObjectMapper objectMapper,
		RabbitTemplate rabbitTemplate
	){
		this.snsUserRepository = snsUserRepository;
		this.postsService = postsService;
		this.objectMapper = objectMapper;
		this.rabbitTemplate = rabbitTemplate;
	}

	private final Semaphore semaphore = new Semaphore(MediaConfigConst.MAX_ASYNC_POST_IMAGE_UPLOAD_NUM);

	// 비디오 전처리, 업로드 및 db 반영 을 pub-sub 방식으로 처리
	// 최소 3개에서 15개의 멀티 스레드로 처리
	@RabbitListener(queues = RabbitMQConst.RABBIT_MQ_POST_IMAGE_UPLOAD_QUEUE, concurrency = "3-15")
	@Transactional
	public void handlePostImageUploadConversion(Message message, Channel channel) throws IOException {
		try {
			// 세마포어 습득
			semaphore.acquire();

			// 객체를 JSON으로 변환
			PostImageUploadConversationMessageQDto postImageUploadDto = objectMapper.readValue(message.getBody(), PostImageUploadConversationMessageQDto.class);

			SnsUser snsUser = snsUserRepository.findByUsername(postImageUploadDto.getUsername()).orElseThrow(
				() -> new BadRequestErrorException("해당 계정은 없습니다.")
			);

			List<File> imageFileList = postImageUploadDto.getPostImageAbsolutePathList().stream().map(File::new).toList();

			if (imageFileList.isEmpty()) {
				throw new FileNotFoundException("업로드된 이미지 파일을 찾을 수 없습니다.");
			}

			List<MultipartFile> multipartFileList = imageFileList.stream().map((imageFile -> {
				try {
					return FileUtils.convertFileToMultipartFile(imageFile);
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
			})).toList();

			SnsPostComposeCreateReq snsPostComposeCreateReq = new SnsPostComposeCreateReq(
				postImageUploadDto.getTagList(),
				postImageUploadDto.getAddress(),
				postImageUploadDto.getBuildName(),
				postImageUploadDto.getLatitude(),
				postImageUploadDto.getLongitude(),
				List.of(),
				postImageUploadDto.getTittle(),
				postImageUploadDto.getBodyText(),
				postImageUploadDto.getTargetAudienceValue(),
				List.of()
			);
			CompletableFuture<Void> future = postsService.composePostProcess(
				new SnsPost(),
				snsPostComposeCreateReq.getTitle(),
				snsPostComposeCreateReq.getBodyText(),
				snsPostComposeCreateReq.getAddress(),
				snsPostComposeCreateReq.getBuildName(),
				snsPostComposeCreateReq.getLatitude(),
				snsPostComposeCreateReq.getLongitude(),
				snsPostComposeCreateReq.getTargetAudienceValue(),
				List.of(),
				List.of(),
				snsPostComposeCreateReq.getTagList(),
				multipartFileList,
				snsUser.getId(),
				false,
				List.of(),
				postImageUploadDto.getCreatedAt(),
				true
			);
			future.get();

			imageFileList.forEach(File::delete);

			// 메시지 처리 성공 시 수동 확인
			channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);

		} catch (Exception e) {

			String errorMsg = "Error processing message : " + new String(message.getBody()) + " : " + e.getMessage();

			Object consumerErrorInfoObj = message.getMessageProperties().getHeaders().get(RabbitMQConst.CONSUMER_ERROR_INFO);
			String consumerErrorInfo = null;

			if (consumerErrorInfoObj instanceof String) {
				consumerErrorInfo = (String) consumerErrorInfoObj;
			} else if (consumerErrorInfoObj instanceof LongString) {
				consumerErrorInfo = ((LongString) consumerErrorInfoObj).toString(); // ✅ LongString을 String으로 변환
			} else if (consumerErrorInfoObj != null) {
				consumerErrorInfo = consumerErrorInfoObj.toString(); // ✅ 기타 객체도 String으로 변환
			}

			StackTraceElement[] errorStacks = e.getStackTrace();
			if (consumerErrorInfo == null) {
				String errorTemplate = LogTemplateConst.getErrorLogTemplate(RabbitMQConst.RABBIT_MQ_ERROR_TYPE, errorMsg, e.getMessage() + "\n" + e.getCause().getMessage() + "\n" + errorStacks[0].toString(), PostImageUploadConversionConsumer.class.toString(),"handlePostImageUploadConversion",
					null, HttpStatus.INTERNAL_SERVER_ERROR.value());
				log.warn(errorTemplate);
				consumerErrorInfo = errorTemplate;
				message.getMessageProperties().getHeaders().put(RabbitMQConst.CONSUMER_ERROR_INFO, consumerErrorInfo);
			}

			rabbitTemplate.send(RabbitMQConst.RABBIT_MQ_POST_IMAGE_UPLOAD_DLX_EXCHANGE,
				message.getMessageProperties().getReceivedRoutingKey(), message);
			channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
		} finally {
			// 세마포어 반환
			semaphore.release();
		}
	}

	@RabbitListener(queues = RabbitMQConst.RABBIT_MQ_POST_IMAGE_UPLOAD_DLX_QUEUE)
	public void dlqConsume(
		Channel channel, Message message,
		@Header(AmqpHeaders.DELIVERY_TAG) long tag) throws
		IOException {
		log.info("consume from dead letter queue");
		log.info("message: {}", message);

		String HEADER_X_RETRIES_COUNT = RabbitMQConst.X_RETRIES_COUNT;
		Integer retriesCnt = (Integer) message.getMessageProperties().getHeaders().get(HEADER_X_RETRIES_COUNT);

		if (retriesCnt == null) retriesCnt = 1;
		else retriesCnt++;
		if (retriesCnt > RabbitMQConst.RETRIES_CNT) {
			log.info("Discarding message");
			rabbitTemplate.send(RabbitMQConst.RABBIT_MQ_PARKING_LOT_EXCHANGE,
				RabbitMQConst.RABBIT_MQ_PARKING_LOT_POST_IMAGE_UPLOAD_ROUTE_KEY, message);
			channel.basicAck(tag, false);
			return;
		}
		log.info("Retrying message for the {} time", retriesCnt);
		message.getMessageProperties().getHeaders().put(HEADER_X_RETRIES_COUNT, retriesCnt);

		// Delay 설정 (30초 후 재처리)
		Message delayedMessage = MessageBuilder
			.fromMessage(message)
			.setHeader("x-delay", 30000) // 딜레이 설정 (밀리초 단위)
			.build();

		rabbitTemplate.send(RabbitMQConst.RABBIT_MQ_POST_IMAGE_UPLOAD_EXCHANGE,
			delayedMessage.getMessageProperties().getReceivedRoutingKey(), delayedMessage);
		channel.basicAck(tag, false);
	}
}
