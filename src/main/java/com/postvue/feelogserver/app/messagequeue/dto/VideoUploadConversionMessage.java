package com.postvue.feelogserver.app.messagequeue.dto;

import org.springframework.web.multipart.MultipartFile;

import com.postvue.feelogserver.domain.snsposts.vo.SnsPostContent;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class VideoUploadConversionMessage {
    private Long postId;
    private String videoAbsolutePath;
    private String videoContent;
    private String videoPreviewImg;
}