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
public class R2Config {
    @Value("${cloud.cloudflare.service.contentBucket.accessKey}")
    private String R2_ACCESS_KEY;

    @Value("${cloud.cloudflare.service.contentBucket.secretKey}")
    private String R2_SECRET_KEY;

    @Value("${cloud.cloudflare.service.contentBucket.endpoint}")
    private String R2_ENDPOINT;


    
    @Bean
    public S3Client s3Client() {
        AwsBasicCredentials credentials = AwsBasicCredentials.create(R2_ACCESS_KEY, R2_SECRET_KEY);
        
        return S3Client.builder()
                .endpointOverride(URI.create(R2_ENDPOINT))
                .credentialsProvider(StaticCredentialsProvider.create(credentials))
                .region(Region.of("auto")) // R2는 글로벌 서비스이므로 임의의 Region 사용 가능
                .serviceConfiguration(S3Configuration.builder()
                        .pathStyleAccessEnabled(true) // R2는 path-style URL을 사용
                        .build())
                .build();
    }
}