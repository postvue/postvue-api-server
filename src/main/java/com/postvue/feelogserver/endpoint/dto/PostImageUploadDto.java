package com.postvue.feelogserver.endpoint.dto;

import java.time.LocalDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PostImageUploadDto {
        String username;
        List<String> imageFilePathList;
        String tittle;
        String bodyText;

        // 공개 대상
        Integer targetAudienceValue;

        LocalDateTime createdAt;

        List<String> tag;
        String address;
        Float latitude;
        Float longitude;
        String buildName;
}
