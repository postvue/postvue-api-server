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
import com.postvue.feelogserver.app.maps.service.AppleMapsService;
import com.postvue.feelogserver.app.maps.service.MapService;
import com.postvue.feelogserver.app.posts.dto.rsp.get.SnsPostRsp;
import com.postvue.feelogserver.app.posts.service.PostsService;
import com.postvue.feelogserver.core.security.CustomUserDetails;
import com.postvue.feelogserver.core.security.exception.JwtTokenExpiredException;
import com.postvue.feelogserver.global.constant.PageConfigConst;
import com.postvue.feelogserver.global.http.response.serverresponse.ServerGetOkRsp;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/maps")
@RequiredArgsConstructor
public class MapController {
	private final MapService mapService;
	private final PostsService postsService;
	private final AppleMapsService appleMapsService;

	@GetMapping("/addresses/uniqueness")
	public ServerGetOkRsp<GetAddressReverseGeocodeRsp> getAddressByGeo(
		@AuthenticationPrincipal CustomUserDetails userDetails,
		@RequestParam(name = "latitude", required = true) String latitude,
		@RequestParam(name = "longitude", required = true) String longitude
	) {
		// Long snsUserId = (userDetails == null) ? null : Long.valueOf(userDetails.getUserId());
		//
		// if (snsUserId == null) {
		// 	throw new JwtTokenExpiredException(new Exception());
		// }
		return new ServerGetOkRsp<>(mapService.getAddressReverseGeocode(latitude, longitude));
	}

	@GetMapping("/addresses")
	public ServerGetOkRsp<List<GetAddressReverseGeocodeRsp>> getAddressListByGeo(
		@AuthenticationPrincipal CustomUserDetails userDetails,
		@RequestParam(name = "latitude", required = true) String latitude,
		@RequestParam(name = "longitude", required = true) String longitude
	) {
		Long snsUserId = (userDetails == null) ? null : Long.valueOf(userDetails.getUserId());

		if (snsUserId == null) {
			throw new JwtTokenExpiredException(new Exception());
		}
		return new ServerGetOkRsp<>(mapService.getAddressListReverseGeocode(latitude, longitude));
	}

	@GetMapping("/location")
	public ServerGetOkRsp<GetAddressGeocodeRsp> getAddressByGeocode(
		@AuthenticationPrincipal CustomUserDetails userDetails,
		@RequestParam(name = "address", required = true) String address) {
		Long snsUserId = (userDetails == null) ? null : Long.valueOf(userDetails.getUserId());

		if (snsUserId == null) {
			throw new JwtTokenExpiredException(new Exception());
		}
		return new ServerGetOkRsp<>(mapService.getAddressGeocode(address));
	}

	@GetMapping("/search/local")
	public ServerGetOkRsp<List<GetLocalSearchRsp>> getSearchLocal(
		@AuthenticationPrincipal CustomUserDetails userDetails,
		@RequestParam(name = "srch_qry") String srchQry,
		@RequestParam(name = "latitude") Float latitude,
		@RequestParam(name = "longitude") Float longitude
	) {
		Long snsUserId = (userDetails == null) ? null : Long.valueOf(userDetails.getUserId());

		if (snsUserId == null) {
			throw new JwtTokenExpiredException(new Exception());
		}
		// return new ServerGetOkRsp<>(mapService.getSearchLocal(srchQry));
		return new ServerGetOkRsp<>(mapService.getSearchLocalWithGis(srchQry,latitude, longitude));

	}

	@GetMapping("/addresses/relations")
	public ServerGetOkRsp<List<GetAddress>> getAddressRelations(
		@AuthenticationPrincipal CustomUserDetails userDetails,
		@RequestParam(name = "srch_qry", required = true) String srchQry,
		@RequestParam(name = "page", defaultValue = PageConfigConst.PAGE_INIT_NUM_STRING) Integer page,
		@RequestParam(name = "latitude", required = false) Float latitude,
		@RequestParam(name = "longitude", required = false) Float longitude
	) {
		Long snsUserId = (userDetails == null) ? null : Long.valueOf(userDetails.getUserId());

		if (snsUserId == null) {
			throw new JwtTokenExpiredException(new Exception());
		}

		if (latitude != null && longitude != null){
			return new ServerGetOkRsp<>(
				mapService.getAddressRelationBySrchQryByGis(srchQry,
						true, PageConfigConst.MAP_ADDRESS_RELATION_KAKAO_MAX_NUM, page, latitude, longitude, null)
					.stream().map(addressWithGis ->
						GetAddress.builder()
							.roadAddr(addressWithGis.getRoadAddr())
							.buildName(addressWithGis.getBuildName())
							.latitude(addressWithGis.getLatitude())
							.longitude(addressWithGis.getLongitude())
							.build())
					.toList()
			);
		}
		else{
			return new ServerGetOkRsp<>(mapService.getAddressRelationBySrchQry(srchQry,
				true, PageConfigConst.MAP_ADDRESS_RELATION_NAVER_MAX_NUM, page, null));
		}
	}

	@GetMapping("/search/recomm")
	public ServerGetOkRsp<List<GetMapSearchRecommRsp>> getMapSearchRecomm(
		@AuthenticationPrincipal CustomUserDetails userDetails,
		@RequestParam(value = "srch_qry", required = true) String srchQry) {

		Long snsUserId = (userDetails == null) ? null : Long.valueOf(userDetails.getUserId());
		return new ServerGetOkRsp<>(mapService.getMapRecommSearch(srchQry, snsUserId));
	}

	@GetMapping("/search/post")
	public ServerGetOkRsp<List<GetMapSearchPostRsp>> getAllMapPostBySrchQry(
		@AuthenticationPrincipal CustomUserDetails userDetails,
		@RequestParam(value = "srch_qry", required = true) String srchQry,
		@RequestParam(name = "page", defaultValue = PageConfigConst.PAGE_INIT_NUM_STRING) Integer page) {
		Long snsUserId = (userDetails == null) ? null : Long.valueOf(userDetails.getUserId());
		return new ServerGetOkRsp<>(
			mapService.getAllMapPostBySrchQry(srchQry, page, PageConfigConst.MAP_POST_PAGE_SIZE, snsUserId));
	}

	// 내 사진만 보기
	@GetMapping("/posts/me")
	public ServerGetOkRsp<List<SnsPostRsp>> getMapPostByMe(
		@AuthenticationPrincipal CustomUserDetails userDetails,
		@RequestParam(name = "page", defaultValue = PageConfigConst.PAGE_INIT_NUM_STRING) Integer page) {

		Long snsUserId = (userDetails == null) ? null : Long.valueOf(userDetails.getUserId());
		return new ServerGetOkRsp<>(postsService.findMapPostsByMe(snsUserId, page));
	}
}
