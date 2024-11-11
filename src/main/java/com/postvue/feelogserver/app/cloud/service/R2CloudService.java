package com.postvue.feelogserver.app.cloud.service;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.postvue.feelogserver.global.exception.BaseException;
import com.postvue.feelogserver.global.exception.InternalServerErrorException;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import software.amazon.awssdk.core.exception.SdkException;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

@Service
@RequiredArgsConstructor
public class R2CloudService {
	@Value("${cloud.cloudflare.service.contentBucket.bucketName}")
	private String BUCKET_NAME;

	private final S3Client s3Client;


	@Transactional
	public void uploadImagesToR2(List<MultipartFile> files, List<String> s3ImageUrls) {
		try {
			for (int i = 0; i < files.size(); i++) {
				uploadImageToR2(files.get(i), s3ImageUrls.get(i));
			}
		} catch (BaseException e) {
			throw e;
		} catch (Exception e) {
			throw new InternalServerErrorException("서버 오류로 인한 리뷰 생성 및 수정 실패", e);
		}
	}

	public void uploadImageToR2(MultipartFile file, String s3ImageUrl) {
		try {
			InputStream inputStream = file.getInputStream();
			String contentType = file.getContentType();

			PutObjectRequest putObjectRequest = PutObjectRequest.builder()
				.bucket(BUCKET_NAME)
				.key(s3ImageUrl)
				.contentType(contentType)
				.build();

			s3Client.putObject(
				putObjectRequest,
				RequestBody.fromInputStream(
					inputStream,
					file.getSize()));

		} catch (SdkException | IOException e) {
			throw new InternalServerErrorException("S3 이미지 업로드 실패 - 서버 에러", e);
		} catch (Exception e) {
			throw new InternalServerErrorException("서버 오류로 인한 리뷰 생성 및 수정 실패", e);
		}
	}
}
