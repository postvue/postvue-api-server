package com.postvue.feelogserver.app.messagequeue.service.consumer;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.Semaphore;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.core.NestedExceptionUtils;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.postvue.feelogserver.app.cloud.service.MinioCloudService;
import com.postvue.feelogserver.app.externallib.ffmpeg.FfmpegProcessingService;
import com.postvue.feelogserver.app.messagequeue.dto.VideoUploadConversionMessage;
import com.postvue.feelogserver.domain.snsposts.SnsPost;
import com.postvue.feelogserver.domain.snsposts.repository.SnsPostRepository;
import com.postvue.feelogserver.domain.snsposts.vo.SnsPostContent;
import com.postvue.feelogserver.global.constant.LogTemplateConst;
import com.postvue.feelogserver.global.constant.MediaConfigConst;
import com.postvue.feelogserver.global.constant.RabbitMQConst;
import com.postvue.feelogserver.global.exception.BadRequestErrorException;
import com.rabbitmq.client.Channel;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class VideoConversionConsumer {

	private final SnsPostRepository snsPostRepository;
	private final FfmpegProcessingService ffmpegProcessingService;
	private final MinioCloudService minioCloudService;
	private final RabbitTemplate rabbitTemplate;
	private final ObjectMapper objectMapper;

	public VideoConversionConsumer(
		SnsPostRepository snsPostRepository,
		FfmpegProcessingService ffmpegProcessingService,
		MinioCloudService minioCloudService,
		ObjectMapper objectMapper,
		RabbitTemplate rabbitTemplate
	){
		this.snsPostRepository = snsPostRepository;
		this.ffmpegProcessingService = ffmpegProcessingService;
		this.minioCloudService = minioCloudService;
		this.objectMapper = objectMapper;
		this.rabbitTemplate = rabbitTemplate;
	}


	private final Semaphore semaphore = new Semaphore(MediaConfigConst.MAX_ASYNC_VIDEO_UPLOAD_NUM);

	// 비디오 전처리, 업로드 및 db 반영 을 pub-sub 방식으로 처리
	// 최소 3개에서 10개의 멀티 스레드로 처리
	@RabbitListener(queues = RabbitMQConst.RABBIT_MQ_VIDEO_QUEUE, concurrency = "3-12")
	@Transactional
	public void handleUploadVideoConversion(Message message, Channel channel) throws IOException {
		try {
			// 세마포어 습득
			semaphore.acquire();

			// 비디오 파일

			// 객체를 JSON으로 변환
			VideoUploadConversionMessage videoUploadConversionMessage = objectMapper.readValue(message.getBody(), VideoUploadConversionMessage.class);
			File videoFile = new File(videoUploadConversionMessage.getVideoAbsolutePath());

			SnsPost snsPost = snsPostRepository.findById(videoUploadConversionMessage.getPostId()).orElseThrow(
				() -> new BadRequestErrorException("해당 포스트 id는 없습니다.")
			);

			String tempFolderName = UUID.randomUUID().toString();
			Path tempDir = Files.createTempDirectory(tempFolderName);
			Path outputTempAbsoluteDirPath = tempDir.toAbsolutePath();
			File outputDirFile = outputTempAbsoluteDirPath.toFile();

			if (ffmpegProcessingService.getVideoDuration(videoFile) <= MediaConfigConst.MAX_VIDEO_DURATION) {

				ffmpegProcessingService.convertToHLS(videoFile, outputDirFile, minioCloudService.m3u8FileName);
				minioCloudService.uploadHLSToMinio(outputDirFile, minioCloudService.getBucketKeyContentUrl(
					videoUploadConversionMessage.getVideoContent()));

				// queue에서 처리하지 않고 바로 처리
				// poster 이미지
				// File posterImgFile = ffmpegProcessingService.generateVideoPoster(videoFile, MediaConfigConst.TEMP_IMAGE_NAME);
				// minioCloudService.uploadImageJpegToMinio(posterImgFile, minioCloudService.getBucketKeyContentUrl(
				// 	videoUploadConversionMessage.getVideoPreviewImg()));
				// posterImgFile.delete();

				List<SnsPostContent> snsPostContentList = snsPost.getSnsPostContents();
				snsPostContentList.stream().filter(postContent -> Objects.equals(
					postContent.getContent(), videoUploadConversionMessage.getVideoContent())).forEach(
					postContent_ -> {
						postContent_.setIsUploaded(true);
						postContent_.setIsVideoDuration(
							ffmpegProcessingService.getVideoDuration(videoFile)
						);
					}
				);

				snsPost.setSnsPostContents(snsPostContentList);
				snsPostRepository.save(snsPost);

				// 파일 즉시 제거
				videoFile.delete();
				outputDirFile.delete();
				tempDir.toFile().delete();

				// 메시지 처리 성공 시 수동 확인
				channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
			}
			else{
				// 파일 즉시 제거
				videoFile.delete();
				outputDirFile.delete();
				tempDir.toFile().delete();

				throw new BadRequestErrorException(MediaConfigConst.MAX_VIDEO_DURATION+" 이하의 영상만 올릴 수 있습니다.");
			}
		} catch (Exception e) {

			String errorMsg = "Error processing message : " + new String(message.getBody()) + " : " + e.getMessage();

			String consumerErrorInfo = (String) message.getMessageProperties().getHeaders().get(RabbitMQConst.CONSUMER_ERROR_INFO);

			StackTraceElement[] errorStacks = e.getStackTrace();
			if (consumerErrorInfo == null) {
				String errorTemplate = LogTemplateConst.getErrorLogTemplate(RabbitMQConst.RABBIT_MQ_ERROR_TYPE, errorMsg, e.getMessage() + "\n" + e.getCause().getMessage() + "\n" + errorStacks[0].toString(), VideoConversionConsumer.class.toString(),"handleUploadVideoConversion",
					null, HttpStatus.INTERNAL_SERVER_ERROR.value());
				log.warn(errorTemplate);
				consumerErrorInfo = errorTemplate;
				message.getMessageProperties().getHeaders().put(RabbitMQConst.CONSUMER_ERROR_INFO, consumerErrorInfo);
			}

			// channel.basicReject(message.getMessageProperties().getDeliveryTag(), false);
			rabbitTemplate.send(RabbitMQConst.RABBIT_MQ_VIDEO_DLX_EXCHANGE,
				message.getMessageProperties().getReceivedRoutingKey(), message);
			channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
		} finally {
			// 세마포어 반환
			semaphore.release();
		}
	}

	@RabbitListener(queues = RabbitMQConst.RABBIT_MQ_VIDEO_DLX_QUEUE)
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
				RabbitMQConst.RABBIT_MQ_PARKING_LOT_VIDEO_ROUTE_KEY, message);
			channel.basicAck(tag, false);
			return;
		}
		log.info("Retrying message for the {} time", retriesCnt);
		message.getMessageProperties().getHeaders().put(HEADER_X_RETRIES_COUNT, retriesCnt);

		// Delay 설정 (10초 후 재처리)
		Message delayedMessage = MessageBuilder
			.fromMessage(message)
			.setHeader("x-delay", 30000) // 딜레이 설정 (밀리초 단위)
			.build();

		rabbitTemplate.send(RabbitMQConst.RABBIT_MQ_VIDEO_EXCHANGE,
			delayedMessage.getMessageProperties().getReceivedRoutingKey(), delayedMessage);
		channel.basicAck(tag, false);
	}

	// @RabbitListener(queues = RabbitMQConst.RABBIT_MQ_VIDEO_QUEUE, concurrency = "3-12")
	// @Transactional
	// public void handleUploadVideoConversion(String videoUploadConversionMessageString){
	// 	try {
	// 		// 세마포어 습득
	// 		semaphore.acquire();
	//
	// 		// 비디오 파일
	// 		// ObjectMapper 생성
	// 		ObjectMapper objectMapper = new ObjectMapper();
	//
	// 		// 객체를 JSON으로 변환
	// 		VideoUploadConversionMessage videoUploadConversionMessage = objectMapper.readValue(videoUploadConversionMessageString, VideoUploadConversionMessage.class);
	// 		File videoFile = new File(videoUploadConversionMessage.getVideoAbsolutePath());
	//
	// 		SnsPost snsPost = snsPostRepository.findById(videoUploadConversionMessage.getPostId()).orElseThrow(
	// 			() -> new BadRequestErrorException("해당 포스트 id는 없습니다.")
	// 		);
	//
	// 		String tempFolderName = UUID.randomUUID().toString();
	// 		Path tempDir = Files.createTempDirectory(tempFolderName);
	// 		Path outputTempAbsoluteDirPath = tempDir.toAbsolutePath();
	// 		File outputDirFile = outputTempAbsoluteDirPath.toFile();
	//
	// 		ffmpegProcessingService.convertToHLS(videoFile, outputDirFile, minioCloudService.m3u8FileName);
	// 		minioCloudService.uploadHLSToMinio(outputDirFile, minioCloudService.getBucketKeyContentUrl(
	// 			videoUploadConversionMessage.getVideoContent()));
	//
	//
	// 		// queue에서 처리하지 않고 바로 처리
	// 		// poster 이미지
	// 		// File posterImgFile = ffmpegProcessingService.generateVideoPoster(videoFile, MediaConfigConst.TEMP_IMAGE_NAME);
	// 		// minioCloudService.uploadImageJpegToMinio(posterImgFile, minioCloudService.getBucketKeyContentUrl(
	// 		// 	videoUploadConversionMessage.getVideoPreviewImg()));
	// 		// posterImgFile.delete();
	//
	// 		List<SnsPostContent> snsPostContentList = snsPost.getSnsPostContents();
	// 		snsPostContentList.stream().filter(postContent -> Objects.equals(
	// 			postContent.getContent(), videoUploadConversionMessage.getVideoContent())).forEach(
	// 			postContent_ -> {
	// 				postContent_.setIsUploaded(true);
	// 				postContent_.setIsVideoDuration(
	// 					ffmpegProcessingService.getVideoDuration(videoFile)
	// 				);
	// 			}
	// 		);
	//
	// 		snsPost.setSnsPostContents(snsPostContentList);
	// 		snsPostRepository.save(snsPost);
	//
	// 		// 즉시 제거
	// 		videoFile.delete();
	// 		outputDirFile.delete();
	// 		tempDir.toFile().delete();
	// 	} catch (BaseException e) {
	// 		throw e;
	// 	} catch (Exception e) {
	// 		throw new InternalServerErrorException("서버 오류로 업로드 실패", e);
	// 	} finally {
	// 		// 세마포어 반환
	// 		semaphore.release();
	// 	}
	// }
}
