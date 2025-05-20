package com.postvue.feelogserver.app.subevent.controller;

import java.util.List;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.postvue.feelogserver.app.recomm.dto.rsp.GetRecommFollowRsp;
import com.postvue.feelogserver.app.subevent.dto.GetShortArticleRsp;
import com.postvue.feelogserver.app.subevent.service.SubEventService;
import com.postvue.feelogserver.core.security.CustomUserDetails;
import com.postvue.feelogserver.global.http.response.serverresponse.ServerGetOkRsp;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/sub-events/v1")
@RequiredArgsConstructor
public class SubEventV1Controller {
	private final SubEventService subEventService;

	@GetMapping("/short-articles")
	public ServerGetOkRsp<List<GetShortArticleRsp>> getRecommFollowList(
		@RequestParam("page") Integer page
	) {
		return new ServerGetOkRsp<>(subEventService.findShortArticleListV1(page));
	}
}
