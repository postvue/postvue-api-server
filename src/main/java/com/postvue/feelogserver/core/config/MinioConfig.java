package com.postvue.feelogserver.core.config;

import java.net.URI;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3Configuration;

@Configuration
public class MinioConfig {
	@Value("${cloud.minio.service.videos.endpoint}")
	private String endpoint;

	@Value("${cloud.minio.service.videos.access-key}")
	private String accessKey;

	@Value("${cloud.minio.service.videos.secret-key}")
	private String secretKey;

	@Bean(name = "minioClient")
	public S3Client minioClient() {
		return S3Client.builder()
			.endpointOverride(URI.create(endpoint))
			.credentialsProvider(StaticCredentialsProvider.create(AwsBasicCredentials.create(accessKey, secretKey)))
			.region(Region.AP_NORTHEAST_2) // MinIO는 리전 확인을 무시합니다. 아무 값이나 사용 가능.
			.serviceConfiguration(S3Configuration.builder().pathStyleAccessEnabled(true).build())
			.build();
	}
}
