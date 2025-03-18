package com.postvue.feelogserver.app.posts.controller;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.postvue.feelogserver.app.posts.dto.req.create.admin.AdminSnsPostComposeCreateListReq;
import com.postvue.feelogserver.app.posts.dto.req.create.admin.AdminSnsPostComposeCreateReq;
import com.postvue.feelogserver.app.posts.service.PostsAdminService;
import com.postvue.feelogserver.core.security.CustomUserDetails;
import com.postvue.feelogserver.domain.snsusers.SnsUser;
import com.postvue.feelogserver.domain.snsusers.repository.SnsUserRepository;
import com.postvue.feelogserver.domain.snsusers.vo.SnsAppRole;
import com.postvue.feelogserver.global.constant.MediaConfigConst;
import com.postvue.feelogserver.global.exception.BadRequestErrorException;
import com.postvue.feelogserver.global.exception.UnauthorizedErrorException;
import com.postvue.feelogserver.global.http.response.serverresponse.ServerPostCreatedRsp;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/admin-api/posts")
@RequiredArgsConstructor
public class PostsAdminController {
	private final PostsAdminService postsAdminService;
	private final SnsUserRepository snsUserRepository;

	@PostMapping("/compose")
	public ServerPostCreatedRsp<Boolean> composePost(
		@RequestPart("snsPostComposeList") List<AdminSnsPostComposeCreateReq> snsPostComposeList,
		@RequestPart(value = "files", required = false) List<MultipartFile> files,
		@AuthenticationPrincipal CustomUserDetails userDetails
	) {
		Long snsUserId = (userDetails == null) ? null : Long.valueOf(userDetails.getUserId());
		// 유저 불러오기
		if (snsUserId == null) {
			throw new UnauthorizedErrorException("인증되지 않았습니다.");
		}

		SnsUser snsUser = snsUserRepository.findById(snsUserId).orElseThrow(
			() -> new BadRequestErrorException("해당 계정은 없습니다.")
		);
		if (snsUser.getSnsAppRole() == SnsAppRole.ROLE_USER){
			throw new UnauthorizedErrorException("접근 권한이 없습니다.");
		}

		snsPostComposeList.forEach(
			adminSnsPostComposeCreateReq -> {
				if (snsUserRepository.findByUsername(adminSnsPostComposeCreateReq.getUsername()).isEmpty()){
					throw new BadRequestErrorException("해당 계정은 없습니다.");
				}
			}
		);

		return new ServerPostCreatedRsp<>(postsAdminService.postImageUploadList(snsPostComposeList, files));
	}
}
