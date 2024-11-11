package com.postvue.feelogserver.app.recomm.controller;

import java.util.List;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.postvue.feelogserver.app.posts.dto.rsp.get.GetPostRelationRsp;
import com.postvue.feelogserver.app.posts.service.PostsService;
import com.postvue.feelogserver.app.recomm.dto.rsp.GetRecommFollowRsp;
import com.postvue.feelogserver.app.recomm.dto.rsp.GetRecommTagRsp;
import com.postvue.feelogserver.app.recomm.service.RecommService;
import com.postvue.feelogserver.core.security.CustomUserDetails;
import com.postvue.feelogserver.global.constant.PageConfigConst;
import com.postvue.feelogserver.global.http.response.serverresponse.ServerGetOkRsp;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/recomm")
@RequiredArgsConstructor
public class RecommController {
	private final RecommService recommService;
	private final PostsService postsService;

	@GetMapping("/follows")
	public ServerGetOkRsp<List<GetRecommFollowRsp>> getRecommFollowList(
		@AuthenticationPrincipal CustomUserDetails userDetails) {
		Long snsUserId = (userDetails == null) ? null : Long.valueOf(userDetails.getUserId());
		return new ServerGetOkRsp<>(recommService.findRecommFollowList(snsUserId));
	}

	//@REFER: 1711682965002 아닌 다른 아이디로 하면 관련 포스트 잘 안 보여줌
	@GetMapping("/posts/{postId}/relation")
	public ServerGetOkRsp<
		GetPostRelationRsp> getRecommPostRelated(
		@AuthenticationPrincipal CustomUserDetails userDetails,
		@PathVariable("postId") Long postId,
		@RequestParam(value = "cursor", defaultValue = PageConfigConst.LAST_POST_ID) Long cursorId) {

		Long snsUserId = (userDetails == null) ? null : Long.valueOf(userDetails.getUserId());
		return new ServerGetOkRsp<>(postsService.findPostRelation(postId, snsUserId, cursorId));
	}

	//@REFER: 로그인 하지 않을 시, 다른 값으로 보여줄 지 고려
	@GetMapping("/tags")
	public ServerGetOkRsp<
		List<GetRecommTagRsp>> getRecommTagList(
		@AuthenticationPrincipal CustomUserDetails userDetails) {
		Long snsUserId = (userDetails == null) ? null : Long.valueOf(userDetails.getUserId());
		return new ServerGetOkRsp<>(recommService.findRecommTagList(snsUserId));
	}

	@GetMapping("/favorite/tags")
	public ServerGetOkRsp<
		List<GetRecommTagRsp>> getRecommFavoriteTagList(@RequestParam("page") Integer page) {
		return new ServerGetOkRsp<>(recommService.findRecommFavoriteTagList(page));
	}

	@GetMapping("/posts/relation/popularity")
	public ServerGetOkRsp<String> getRecommPostRelatedByPopular() {
		return null;
	}

	@GetMapping("/posts/relation/follows")
	public ServerGetOkRsp<String> getRecommPostRelatedByFollow() {
		return null;
	}

	@GetMapping("/posts/relation/recency")
	public ServerGetOkRsp<String> getRecommPostRelatedByRecently() {
		return null;
	}

	@GetMapping("/posts/relation/distance")
	public ServerGetOkRsp<String> getRecommPostRelatedByDistance() {
		return null;
	}
}
