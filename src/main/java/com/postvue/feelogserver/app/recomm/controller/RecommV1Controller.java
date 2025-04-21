package com.postvue.feelogserver.app.recomm.controller;

import java.util.List;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.postvue.feelogserver.app.posts.dto.rsp.get.SnsPostRsp;
import com.postvue.feelogserver.app.posts.service.PostsService;
import com.postvue.feelogserver.app.recomm.dto.rsp.GetRecommFollowRsp;
import com.postvue.feelogserver.app.recomm.dto.rsp.GetRecommTagRsp;
import com.postvue.feelogserver.app.recomm.service.RecommService;
import com.postvue.feelogserver.core.security.CustomUserDetails;
import com.postvue.feelogserver.global.http.response.serverresponse.ServerGetOkRsp;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/recomm/v1")
@RequiredArgsConstructor
public class RecommV1Controller {
	private final RecommService recommService;

	@GetMapping("/follows")
	public ServerGetOkRsp<List<GetRecommFollowRsp>> getRecommFollowList(
		@AuthenticationPrincipal CustomUserDetails userDetails,
		@RequestParam("page") Integer page
	) {
		Long snsUserId = (userDetails == null) ? null : Long.valueOf(userDetails.getUserId());
		return new ServerGetOkRsp<>(recommService.findRecommFollowListV1(snsUserId, page));
	}
}
