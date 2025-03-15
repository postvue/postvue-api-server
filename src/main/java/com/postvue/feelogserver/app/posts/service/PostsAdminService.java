package com.postvue.feelogserver.app.posts.service;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.postvue.feelogserver.app.messagequeue.service.producer.PostImageUploadConversationProducer;
import com.postvue.feelogserver.app.posts.dto.req.create.admin.AdminSnsPostComposeCreateReq;
import com.postvue.feelogserver.app.posts.dto.req.create.admin.PostImageUploadConversationMessageQDto;
import com.postvue.feelogserver.global.constant.LogTemplateConst;
import com.postvue.feelogserver.global.constant.MediaConfigConst;
import com.postvue.feelogserver.global.util.generator.DateUtils;
import com.vaadin.hilla.exception.EndpointException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostsAdminService {
	private final PostImageUploadConversationProducer postImageUploadConversationProducer;

	public Boolean postImageUploadList(
		List<AdminSnsPostComposeCreateReq> snsPostComposeList,
		List<MultipartFile> imageFiles
	) {

		LocalDateTime start = LocalDateTime.of(2024, 3, 1, 0, 0);
		LocalDateTime end = LocalDateTime.of(2025, 3, 1, 0, 0);

		snsPostComposeList.forEach((snsPostComposeCreateReq -> {
			List<MultipartFile> multipartFileList = imageFiles.stream().filter(imageFile ->
				snsPostComposeCreateReq.getImageFilePathList().contains(imageFile.getOriginalFilename())
			).toList();

			List<String> postImageAbsolutePathList =  multipartFileList.stream().map(multipartFile -> {
				try {
					File postImageTempFile = File.createTempFile(
						MediaConfigConst.UPLOAD_TEMP_FILE_PREFIX_NAME + UUID.randomUUID() + "-",
						multipartFile.getOriginalFilename());
					multipartFile.transferTo(postImageTempFile);

					return postImageTempFile.getAbsolutePath();
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
			}).toList();

			try {
				PostImageUploadConversationMessageQDto data = new PostImageUploadConversationMessageQDto(
					snsPostComposeCreateReq.getUsername(),
					snsPostComposeCreateReq.getTitle(),
					snsPostComposeCreateReq.getBodyText(),
					snsPostComposeCreateReq.getTargetAudienceValue(),
					DateUtils.getRandomDateTime(start, end),
					snsPostComposeCreateReq.getTagList(),
					snsPostComposeCreateReq.getAddress(),
					snsPostComposeCreateReq.getLatitude(),
					snsPostComposeCreateReq.getLongitude(),
					snsPostComposeCreateReq.getBuildName(),
					postImageAbsolutePathList
				);

				postImageUploadConversationProducer.sendPostImageUploadToQueue(data);

			} catch (Exception e) {
				throw new EndpointException("서버 오류 발생",
					LogTemplateConst.getLogInfoTemplate(e.getMessage() + "_" + e.toString(),
						LocalDateTime.now().toString()));
			}
		}));

		return true;
	}
}
