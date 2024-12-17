package com.postvue.feelogserver.app.messagequeue.service.consumer;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.Semaphore;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.postvue.feelogserver.app.cloud.service.MinioCloudService;
import com.postvue.feelogserver.app.externallib.ffmpeg.FfmpegProcessingService;
import com.postvue.feelogserver.app.messagequeue.dto.VideoUploadConversionMessage;
import com.postvue.feelogserver.domain.snsposts.SnsPost;
import com.postvue.feelogserver.domain.snsposts.repository.SnsPostRepository;
import com.postvue.feelogserver.domain.snsposts.vo.SnsPostContent;
import com.postvue.feelogserver.global.constant.MediaConfigConst;
import com.postvue.feelogserver.global.constant.RabbitMQConst;
import com.postvue.feelogserver.global.exception.BadRequestErrorException;
import com.postvue.feelogserver.global.exception.BaseException;
import com.postvue.feelogserver.global.exception.InternalServerErrorException;
import com.postvue.feelogserver.global.util.converter.ByteConvertor;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class VideoConversionConsumer {
	private final SnsPostRepository snsPostRepository;
	private final FfmpegProcessingService ffmpegProcessingService;
	private final MinioCloudService minioCloudService;

	private final Semaphore semaphore = new Semaphore(MediaConfigConst.MAX_ASYNC_VIDEO_UPLOAD_NUM);

	// 비디오 전처리, 업로드 및 db 반영 을 pub-sub 방식으로 처리
	// 최소 3개에서 10개의 멀티 스레드로 처리
	@RabbitListener(queues = RabbitMQConst.RABBIT_MQ_VIDEO_QUEUE, concurrency = "3-12")
	@Transactional
	public void handleUploadVideoConversion(String videoUploadConversionMessageString){
		try {
			// 세마포어 습득
			semaphore.acquire();

			// 비디오 파일
			// ObjectMapper 생성
			ObjectMapper objectMapper = new ObjectMapper();

			// 객체를 JSON으로 변환
			VideoUploadConversionMessage videoUploadConversionMessage = objectMapper.readValue(videoUploadConversionMessageString, VideoUploadConversionMessage.class);
			File videoFile = new File(videoUploadConversionMessage.getVideoAbsolutePath());

			SnsPost snsPost = snsPostRepository.findById(videoUploadConversionMessage.getPostId()).orElseThrow(
				() -> new BadRequestErrorException("해당 포스트 id는 없습니다.")
			);

			String tempFolderName = UUID.randomUUID().toString();
			Path tempDir = Files.createTempDirectory(tempFolderName);
			Path outputTempAbsoluteDirPath = tempDir.toAbsolutePath();
			File outputDirFile = outputTempAbsoluteDirPath.toFile();
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
					postContent_ -> postContent_.setIsUploaded(true)
			);

			snsPost.setSnsPostContents(snsPostContentList);
			snsPostRepository.save(snsPost);


			// 즉시 제거
			videoFile.delete();
			outputDirFile.delete();
			tempDir.toFile().delete();


		} catch (BaseException e) {
			throw e;
		} catch (Exception e) {
			throw new InternalServerErrorException("서버 오류로 업로드 실패", e);
		} finally {
			// 세마포어 반환
			semaphore.release();
		}
	}
}
