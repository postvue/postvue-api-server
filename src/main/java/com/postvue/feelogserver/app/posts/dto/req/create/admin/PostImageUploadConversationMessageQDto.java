package com.postvue.feelogserver.app.posts.dto.req.create.admin;

import java.time.LocalDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PostImageUploadConversationMessageQDto {
        String username;
        String tittle;
        String bodyText;

        // 공개 대상
        Integer targetAudienceValue;

        LocalDateTime createdAt;

        List<String> tagList;
        String address;
        Float latitude;
        Float longitude;
        String buildName;

        List<String> postImageAbsolutePathList;
}
