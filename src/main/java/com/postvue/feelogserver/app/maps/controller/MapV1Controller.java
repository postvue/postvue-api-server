package com.postvue.feelogserver.app.maps.controller;

import java.util.List;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.postvue.feelogserver.app.maps.dto.GetAddress;
import com.postvue.feelogserver.app.maps.dto.GetAddressGeocodeRsp;
import com.postvue.feelogserver.app.maps.dto.GetAddressReverseGeocodeRsp;
import com.postvue.feelogserver.app.maps.dto.GetLocalSearchRsp;
import com.postvue.feelogserver.app.maps.dto.GetMapSearchPostRsp;
import com.postvue.feelogserver.app.maps.dto.GetMapSearchRecommRsp;
import com.postvue.feelogserver.app.maps.dto.GetPlaceByCategoryRsp;
import com.postvue.feelogserver.app.maps.service.AppleMapsService;
import com.postvue.feelogserver.app.maps.service.MapService;
import com.postvue.feelogserver.app.posts.dto.rsp.get.SnsPostRsp;
import com.postvue.feelogserver.app.posts.service.PostsService;
import com.postvue.feelogserver.core.security.CustomUserDetails;
import com.postvue.feelogserver.core.security.exception.JwtTokenExpiredException;
import com.postvue.feelogserver.global.constant.PageConfigConst;
import com.postvue.feelogserver.global.exception.BadRequestErrorException;
import com.postvue.feelogserver.global.http.response.serverresponse.ServerGetOkRsp;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/maps/v1")
@RequiredArgsConstructor
public class MapV1Controller {
	private final MapService mapService;

	@GetMapping("/menu/random")
	public ServerGetOkRsp<List<GetPlaceByCategoryRsp>> getMenuRandomList(
		@RequestParam(name = "latitude") Float latitude,
		@RequestParam(name = "longitude") Float longitude,
		@RequestParam(name = "page", required = false) Integer page
	) {
		if (page == null){
			return new ServerGetOkRsp<>(
				mapService.getPlaceByCategory(latitude, longitude)
			);
		}

		return new ServerGetOkRsp<>(
			mapService.getPlaceByCategory(latitude, longitude, page)
		);
	}
}
