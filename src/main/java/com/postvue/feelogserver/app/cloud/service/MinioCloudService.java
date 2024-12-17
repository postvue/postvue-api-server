package com.postvue.feelogserver.app.cloud.service;

import java.io.File;
import java.nio.file.Paths;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.postvue.feelogserver.global.constant.MediaConfigConst;
import com.postvue.feelogserver.global.exception.InternalServerErrorException;

import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.core.exception.SdkException;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

@Slf4j
@Service
public class MinioCloudService {
	private final S3Client minioClient;  // S3Client 대신 minioClient 사용

	private final String bucketName;

	private final String hlsDirPath;

	private final String posterDirPath;

	public final String bucketPublicUrl;

	public final String m3u8FileName;


	public MinioCloudService(
		@Qualifier("minioClient") S3Client minioClient,
		@Value("${cloud.minio.service.videos.bucket-name}") String bucketName,
		@Value("${cloud.minio.service.videos.hls-dir-path}") String hlsDirPath,
		@Value("${cloud.minio.service.videos.bucketPublicUrl}") String bucketPublicUrl,
		@Value("${cloud.minio.service.videos.m3u8-file-name}") String m3u8FileName,
		@Value("${cloud.minio.service.videos.poster-dir-path}") String posterDirPath
	) {
		this.minioClient = minioClient;
		this.bucketName = bucketName;
		this.hlsDirPath = hlsDirPath;
		this.posterDirPath = posterDirPath;
		this.bucketPublicUrl = bucketPublicUrl;
		this.m3u8FileName = m3u8FileName;
	}
	public String getBucketPath () {
		return bucketName + "/";
	}

	public String getVideoPosterContentUrlByMinio(String contentName){
		return getBucketPath() + posterDirPath + contentName;
	}

	public String getHlsContentUrlByMinio(String contentName){
		return getBucketPath() + hlsDirPath + contentName;
	}
	public String getPublicContentUrlByMinio(String getContentUrl){
		return bucketPublicUrl + getContentUrl;
	}

	public String getBucketKeyContentUrl(String contentUrlWithM3u8FileName) {
		return contentUrlWithM3u8FileName.replace(m3u8FileName,"").replace(getBucketPath(),"");
	}

	public void uploadFileToMinio(File file, String contentUrl, String mimeType) {
		try {
			minioClient.putObject(
				PutObjectRequest.builder()
					.bucket(bucketName)
					.key(contentUrl)
					.contentType(mimeType)
					.build(),
				Paths.get(file.getAbsolutePath())
			);
		} catch (SdkException e) {
			System.out.println("오류는 말수다");
			System.out.println(e.getMessage());
			throw new InternalServerErrorException("R2 이미지 업로드 실패 - 서버 에러", e);
		} catch (Exception e) {
			System.out.println("오류는 말이다.");
			System.out.println(e.getMessage());
			throw new InternalServerErrorException("서버 오류로 인한 업로드 실패", e);
		}
	}

	public void uploadImageJpegToMinio(File file, String contentUrl) {
		try {
			uploadFileToMinio(file, contentUrl, MediaConfigConst.IMAGE_JPEG_TYPE);
		} catch (SdkException e) {
			throw new InternalServerErrorException("R2 이미지 업로드 실패 - 서버 에러", e);
		} catch (Exception e) {
			System.out.println(e);
			throw new InternalServerErrorException("서버 오류로 인한 업로드 실패", e);
		}
	}

	/**
	 * Recursively uploads all files in the HLS directory to MinIO.
	 *
	 * @param directory   Directory containing HLS files
	 * @param bucketKey MinIO path prefix (e.g., "videos/hls/")
	 */
	public void uploadHLSToMinio(File directory, String bucketKey) {
		if (!directory.isDirectory()) {
			throw new IllegalArgumentException("Provided path is not a directory: " + directory.getAbsolutePath());
		}

		File[] files = directory.listFiles();
		if (files == null || files.length == 0) {
			throw new IllegalStateException("HLS directory is empty: " + directory.getAbsolutePath());
		}

		for (File file : files) {
			String targetPath = bucketKey + file.getName(); // MinIO 저장 경로 설정
			try {
				if (file.isFile()) {
					String mimeType = getMimeType(file); // MIME 타입 결정
					uploadFileToMinio(file, targetPath, mimeType);
					log.info("Uploaded file to MinIO: {}", targetPath);
				} else {
					log.warn("Skipping non-file entry in HLS directory: {}", file.getAbsolutePath());
				}
			} catch (Exception e) {
				log.error("Failed to upload file to MinIO: " + file.getAbsolutePath(), e);
				System.out.println("오류는 엄냥");
				System.out.println(e.getMessage());
				throw new InternalServerErrorException("Minio 업로드 실패했습니다.");
			}
		}
	}

	private String getMimeType(File file) {
		String fileName = file.getName().toLowerCase();
		if (fileName.endsWith(MediaConfigConst.VIDEO_M3U8_FORMAT)) {
			return MediaConfigConst.VIDEO_HLS_TYPE;
		} else if (fileName.endsWith(MediaConfigConst.VIDEO_TS_FORMAT)) {
			return MediaConfigConst.VIDEO_HLS_TS_TYPE;
		} else {
			return MediaConfigConst.BASE_FILE_TYPE; // 기본 MIME 타입
		}
	}
}
