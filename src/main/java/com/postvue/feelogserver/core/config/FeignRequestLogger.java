package com.postvue.feelogserver.core.config;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class FeignRequestLogger implements RequestInterceptor {
    private static final Logger logger = LoggerFactory.getLogger(FeignRequestLogger.class);

    @Override
    public void apply(RequestTemplate template) {
        logger.info("Feign Request URL: {}", template.url());
        logger.info("Feign Request Method: {}", template.method());
        logger.info("Feign Request Headers: {}", template.headers());
        logger.info("Feign Request Body: {}", 
            template.body() != null ? new String(template.body()) : "No Body");
    }
}
