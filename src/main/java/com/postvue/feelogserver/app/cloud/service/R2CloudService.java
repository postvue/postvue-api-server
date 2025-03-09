package com.postvue.feelogserver.app.cloud.service;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.UUID;

import javax.imageio.ImageIO;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.postvue.feelogserver.app.externallib.ffmpeg.FfmpegProcessingService;
import com.postvue.feelogserver.app.openapis.req.DiscordWebhookRequest;
import com.postvue.feelogserver.app.openapis.service.DiscordService;
import com.postvue.feelogserver.global.constant.LogTemplateConst;
import com.postvue.feelogserver.global.constant.MediaConfigConst;
import com.postvue.feelogserver.global.exception.InternalServerErrorException;
import com.postvue.feelogserver.global.util.generator.FileUtils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.core.exception.SdkException;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.CopyObjectRequest;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

@Slf4j
@Service
@RequiredArgsConstructor
public class R2CloudService {
	@Value("${cloud.cloudflare.service.contentBucket.bucketName}")
	private String BUCKET_NAME;

	@Value("${cloud.cloudflare.service.contentBucket.bucketPublicUrl}")
	public String bucketPublicUrl;

	@Value("${cloud.cloudflare.service.contentBucket.imageContentStoragePath}")
	private String imageContentStoragePath;

	@Value("${cloud.cloudflare.service.contentBucket.imageCommentContentPath}")
	private String imageCommentContentPath;

	@Value("${cloud.cloudflare.service.contentBucket.imagePostContentPath}")
	private String imagePostContentPath;

	private final S3Client s3Client;

	private final FfmpegProcessingService ffmpegProcessingService;

	private final DiscordService discordService;

	public String getPostImageContentUrlByR2(String name ) {
		return imageContentStoragePath + imagePostContentPath + name;
	}

	public String getPostCommentImageContentUrlByR2(String name ) {
		return imageContentStoragePath + imageCommentContentPath + name;
	}

	public String getPublicContentUrlByR2(String contentUrl) {
		return bucketPublicUrl + contentUrl;
	}

	// @Transactional
	// public void uploadFileToR2(List<MultipartFile> files, List<String> contentUrls) {
	// 	try {
	// 		for (int i = 0; i < files.size(); i++) {
	// 			if(UploadFileValidationUtils.isImage(files.get(i).getContentType())){
	// 				uploadImageToR2(files.get(i), contentUrls.get(i));
	// 			}
	// 			else{
	// 				File tempFile;
	// 				tempFile = File.createTempFile("upload-", files.get(i).getOriginalFilename());
	// 				files.get(i).transferTo(tempFile);
	// 				String outputPath = "output-video.mp4";
	// 				ffmpegProcessingService.convertVideo(tempFile, outputPath);
	// 				File outputFile = new File(outputPath);
	//
	// 				uploadVideoToR2(outputFile, contentUrls.get(i));
	// 				tempFile.delete();
	// 				outputFile.delete();
	// 			}
	// 		}
	// 	} catch (BaseException e) {
	// 		throw e;
	// 	} catch (Exception e) {
	// 		throw new InternalServerErrorException("서버 오류로 업로드 실패", e);
	// 	}
	// }

	public void uploadVideoToR2(File file, String contentUrl) {

		try {
			PutObjectRequest putObjectRequest = PutObjectRequest.builder()
				.bucket(BUCKET_NAME)
				.key(contentUrl)
				.contentType(MediaConfigConst.VIDEO_MP4_TYPE)
				.build();
			s3Client.putObject(
				putObjectRequest,
				Paths.get(file.getAbsolutePath()
				));
		} catch (SdkException e) {
			log.error(e.getMessage());
			throw new InternalServerErrorException("R2 이미지 업로드 실패 - 서버 에러", e);
		} catch (Exception e) {
			log.error(e.getMessage());
			throw new InternalServerErrorException("서버 오류로 인한 업로드 실패", e);
		}
	}

	public void uploadImageToR2(MultipartFile file, String contentUrl) {
		try {
			// 업로드된 파일을 임시 파일로 저장
			Path tempFile = Files.createTempFile(MediaConfigConst.UPLOAD_TEMP_FILE_PREFIX_NAME, MediaConfigConst.UPLOAD_TEMP_FILE_SUFFIX_FORMAT);
			file.transferTo(tempFile.toFile());

			String formatName = Objects.requireNonNull(FileUtils.getExtension(contentUrl)).toLowerCase();
			String contentType = MediaConfigConst.CONTENT_TYPE_MAP.getOrDefault(formatName, MediaConfigConst.BASE_FILE_TYPE);

			// 포맷 변환을 위한 출력 파일 경로 생성
			String outputFileName = UUID.randomUUID() + "." + formatName;
			Path outputFile = tempFile.resolveSibling(outputFileName);

			// 변환된 WebP 파일을 R2에 업로드
			byte[] webpBytes = ffmpegProcessingService.convertImgFormat(tempFile, outputFile);

			// R2에 업로드
			PutObjectRequest putObjectRequest = PutObjectRequest.builder()
				.bucket(BUCKET_NAME)
				.key(contentUrl)
				.contentType(contentType)
				.build();

			s3Client.putObject(
				putObjectRequest,
				RequestBody.fromBytes(webpBytes)
			);

			// 임시 파일 삭제
			Files.deleteIfExists(tempFile);
			Files.deleteIfExists(outputFile);

		} catch (SdkException | IOException e) {
			log.error(e.getMessage());
			throw new InternalServerErrorException("R2 이미지 업로드 실패 - 서버 에러", e);
		} catch (Exception e) {
			log.error(e.getMessage());
			DiscordWebhookRequest discordWebhookRequest = new DiscordWebhookRequest(
				LogTemplateConst.getErrorLogTemplate(e.getClass().getName(),
				"서버 오류로 인해, 이미지 업로드 실패",
				e.getMessage(),
				this.getClass().getName(), Thread.currentThread().getStackTrace()[1].getMethodName(),
				new Object[] {},
				HttpStatus.INTERNAL_SERVER_ERROR.value()
			));
			discordService.sendMessageToPostReportChannel(discordWebhookRequest);
			throw new InternalServerErrorException("서버 오류로 인한 업로드 실패", e);
		}
	}

	public void deleteImageFromR2(String s3ImageUrl) {
		try {
			DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
				.bucket(BUCKET_NAME)
				.key(s3ImageUrl)
				.build();

			s3Client.deleteObject(deleteObjectRequest);
		} catch (SdkException e) {
			log.error(e.getMessage());
			throw new InternalServerErrorException("R2 이미지 삭제 실패 - 서버 에러", e);
		} catch (Exception e) {
			log.error(e.getMessage());
			throw new InternalServerErrorException("서버 오류로 인한 삭제 실패", e);
		}
	}

	public void renameImageInR2(String oldFileUrl, String newFileName) {
		try {
			// 1. 복사 요청 생성
			CopyObjectRequest copyObjectRequest = CopyObjectRequest.builder()
				.sourceBucket(BUCKET_NAME)
				.sourceKey(oldFileUrl) // 기존 파일의 Key(URL에서 파일 경로 추출)
				.destinationBucket(BUCKET_NAME)
				.destinationKey(newFileName) // 새 파일 이름
				.build();

			s3Client.copyObject(copyObjectRequest);

			// 2. 기존 파일 삭제 요청 생성
			deleteImageFromR2(oldFileUrl);
		} catch (SdkException e) {
			log.error(e.getMessage());
			throw new InternalServerErrorException("R2 이미지 이름 변경 실패 - 서버 에러", e);
		} catch (Exception e) {
			log.error(e.getMessage());
			throw new InternalServerErrorException("서버 오류로 인한 이름 변경 실패", e);
		}
	}
}
