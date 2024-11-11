package com.postvue.feelogserver.app.maps.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.postvue.feelogserver.app.maps.dto.GetAddress;
import com.postvue.feelogserver.app.maps.dto.GetAddressGeocodeRsp;
import com.postvue.feelogserver.app.maps.dto.GetAddressReverseGeocodeRsp;
import com.postvue.feelogserver.app.maps.dto.GetLocalSearchRsp;
import com.postvue.feelogserver.app.maps.dto.GetMapSearchPostRsp;
import com.postvue.feelogserver.app.maps.dto.GetMapSearchRecommRsp;
import com.postvue.feelogserver.domain.snsposts.repository.SnsPostRepository;
import com.postvue.feelogserver.global.api.juso.addresssearch.JusoAddressSearchApiClient;
import com.postvue.feelogserver.global.api.juso.addresssearch.dto.Juso;
import com.postvue.feelogserver.global.api.juso.addresssearch.dto.JusoAddressSearchApiRsp;
import com.postvue.feelogserver.global.api.naver.NaverApiClient;
import com.postvue.feelogserver.global.api.naver.dto.rsp.NaverLocalSearchResponseDto;
import com.postvue.feelogserver.global.api.vworld.VworldApiClient;
import com.postvue.feelogserver.global.api.vworld.VworldRspConst;
import com.postvue.feelogserver.global.api.vworld.dto.rsp.VworldGetAddressResultRsp;
import com.postvue.feelogserver.global.api.vworld.dto.rsp.VworldGetGeocodeRsp;
import com.postvue.feelogserver.global.api.vworld.dto.rsp.VworldGetReverseGeocodeRsp;
import com.postvue.feelogserver.global.constant.MapConst;
import com.postvue.feelogserver.global.constant.PageConfigConst;
import com.postvue.feelogserver.global.exception.BadRequestErrorException;
import com.postvue.feelogserver.global.exception.InternalServerErrorException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MapService {
	private final SnsPostRepository snsPostRepository;
	private final VworldApiClient vworldApiClient;

	@Value("${openapi.vworld.clientId}")
	private String vworldClientId;

	@Value("${openapi.vworld.version}")
	private String vworldVersion;

	@Value("${openapi.vworld.addressServiceParam}")
	private String addressServiceParam;

	@Value("${openapi.vworld.reverseGeocodeParam}")
	private String reverseGeocodeParam;

	@Value("${openapi.vworld.geocodeParam}")
	private String gecodeParam;

	@Value("${openapi.vworld.apiFormat}")
	private String apiFormat;

	@Value("${openapi.vworld.crs}")
	private String crs;

	@Value("${openapi.vworld.rspType}")
	private String rspType;

	@Value("${openapi.vworld.rspRoadType}")
	private String rspRoadType;

	private final JusoAddressSearchApiClient jusoAddressSearchApiClient;

	@Value("${openapi.businessjuso.secretKey}")
	private String jusoSecretKey;

	@Value("${openapi.businessjuso.apiFormat}")
	private String jusoApiFormat;

	@Value("${openapi.businessjuso.countPerPage}")
	private Integer jusoCountPerPage;

	@Value("${openapi.businessjuso.firstSort}")
	private String jusoFirstSort;

	private final NaverApiClient naverApiClient;

	@Value("${openapi.naverdevelopers.xNaverClientId}")
	private String xNaverClientId;

	@Value("${openapi.naverdevelopers.xNaverClientSecret}")
	private String xNaverClientSecret;

	@Value("${openapi.naverdevelopers.mapLocalMaxSrchNum}")
	private Integer mapLocalMaxSrchNum;

	public GetAddressReverseGeocodeRsp getAddressReverseGeocode(String latitude, String longitude) {

		VworldGetReverseGeocodeRsp vworldGetReverseGeocodeRsp = vworldApiClient.getAddressReverseGeocode(
			addressServiceParam, reverseGeocodeParam, vworldVersion, apiFormat,
			crs, rspRoadType,
			String.format("%s,%s", longitude, latitude), vworldClientId);

		if (Objects.equals(vworldGetReverseGeocodeRsp.getResponse().getStatus(), VworldRspConst.VWORLD_OK_RSP_STATUS)) {
			List<VworldGetAddressResultRsp> vworldGetAddressResultRsps = vworldGetReverseGeocodeRsp.getResponse()
				.getResult();
			if (!vworldGetAddressResultRsps.isEmpty()) {
				VworldGetAddressResultRsp vworldGetAddressResultRsp = vworldGetAddressResultRsps.get(0);
				return GetAddressReverseGeocodeRsp.builder()
					.address(vworldGetAddressResultRsp.getText())
					.buildName(vworldGetAddressResultRsp.getStructure().getDetail())
					.zipcode(vworldGetAddressResultRsp.getZipcode())
					.latitude(vworldGetReverseGeocodeRsp.getResponse().getInput().getPoint().getY())
					.longitude(vworldGetReverseGeocodeRsp.getResponse().getInput().getPoint().getX())
					.build();
			} else {
				throw new InternalServerErrorException("주소 정보가 없습니다.");
			}
		} else {
			throw new BadRequestErrorException("해당 gps 정보가 DB 상에 존재하지 않습니다.");
		}
	}

	public List<GetAddressReverseGeocodeRsp> getAddressListReverseGeocode(String latitude, String longitude) {

		VworldGetReverseGeocodeRsp vworldGetReverseGeocodeRsp = vworldApiClient.getAddressReverseGeocode(
			addressServiceParam, reverseGeocodeParam, vworldVersion, apiFormat,
			crs, rspRoadType,
			String.format("%s,%s", longitude, latitude), vworldClientId);

		if (Objects.equals(vworldGetReverseGeocodeRsp.getResponse().getStatus(), VworldRspConst.VWORLD_OK_RSP_STATUS)) {
			List<VworldGetAddressResultRsp> vworldGetAddressResultRsps = vworldGetReverseGeocodeRsp.getResponse()
				.getResult();
			if (!vworldGetAddressResultRsps.isEmpty()) {

				return vworldGetAddressResultRsps.stream()
					.map((vworldGetAddressResultRsp -> GetAddressReverseGeocodeRsp.builder()
						.address(vworldGetAddressResultRsp.getStructure().getLevel1() + " "
							+ vworldGetAddressResultRsp.getStructure().getLevel2() + " "
							+ vworldGetAddressResultRsp.getStructure().getLevel4L() + " "
							+ vworldGetAddressResultRsp.getStructure().getLevel5())
						.buildName(vworldGetAddressResultRsp.getStructure().getDetail())
						.zipcode(vworldGetAddressResultRsp.getText())
						.latitude(vworldGetReverseGeocodeRsp.getResponse().getInput().getPoint().getY())
						.longitude(vworldGetReverseGeocodeRsp.getResponse().getInput().getPoint().getX())
						.build()))
					.toList();

			} else {
				throw new InternalServerErrorException("주소 정보가 없습니다.");
			}
		} else {
			throw new BadRequestErrorException("해당 gps 정보가 DB 상에 존재하지 않습니다.");
		}
	}

	public GetAddressGeocodeRsp getAddressGeocode(String address) {

		VworldGetGeocodeRsp vworldGetGeocodeRsp = vworldApiClient.getAddressGeocode(
			addressServiceParam, gecodeParam, vworldVersion, address, apiFormat,
			crs, rspRoadType, vworldClientId);

		if (Objects.equals(vworldGetGeocodeRsp.getResponse().getStatus(), VworldRspConst.VWORLD_OK_RSP_STATUS)) {
			VworldGetGeocodeRsp.GeocodePoint geocodePoint = vworldGetGeocodeRsp.getResponse()
				.getResult().getPoint();

			return GetAddressGeocodeRsp.builder()
				.address(address)
				.latitude(Float.valueOf(geocodePoint.getY()))
				.longitude(Float.valueOf(geocodePoint.getX()))
				.build();

		} else {
			throw new BadRequestErrorException("해당 gps 정보가 DB 상에 존재하지 않습니다.");
		}
	}

	public List<GetLocalSearchRsp> getSearchLocal(String srchQry) {

		NaverLocalSearchResponseDto naverLocalSearchResponseDto = naverApiClient.getLocalSearch(xNaverClientId,
			xNaverClientSecret,
			srchQry, mapLocalMaxSrchNum);

		List<GetAddress> getAddressList = getAddressRelationBySrchQry(srchQry, false, 0, 0f, 0f,
			MapConst.MAG_LOCAL_SEARCH_REVERSE_GEOCODE_MAX_NUM);

		return Stream.concat(naverLocalSearchResponseDto.getItems().stream().map((item ->
			GetLocalSearchRsp.builder()
				.roadAddr(NaverLocalSearchResponseDto.convertHtmlTitleToText(item.getRoadAddress()))
				.placeName(NaverLocalSearchResponseDto.convertHtmlTitleToText(item.getTitle()))
				.hasLocation(true)
				.latitude(NaverLocalSearchResponseDto.convertLatTextToFloat(item.getMapy()))
				.longitude(NaverLocalSearchResponseDto.convertLngTextToFloat(item.getMapx()))
				.build()
		)), getAddressList.stream().map((getAddress ->
			GetLocalSearchRsp.builder()
				.roadAddr(getAddress.getRoadAddr())
				.placeName(getAddress.getBuildName())
				.hasLocation(false)
				.latitude(0f)
				.longitude(0f)
				.build()
		))).collect(Collectors.toList());
	}

	public List<GetAddress> getAddressRelationBySrchQry(String srchQry,
		Boolean hasLocalAddress,
		Integer page, Float latitude,
		Float longitude, Integer srchPageNum) {

		List<GetAddress> getAddressList = new ArrayList<>();
		if (Objects.equals(page, PageConfigConst.PAGE_INIT_NUM) && hasLocalAddress) {
			NaverLocalSearchResponseDto naverLocalSearchResponseDto = naverApiClient.getLocalSearch(xNaverClientId,
				xNaverClientSecret,
				srchQry, mapLocalMaxSrchNum);
			getAddressList.addAll(
				naverLocalSearchResponseDto.getItems()
					.stream()
					.filter((item -> !NaverLocalSearchResponseDto.convertHtmlTitleToText(item.getRoadAddress())
						.trim()
						.isEmpty()))
					.map((item ->
						GetAddress.builder()
							.roadAddr(NaverLocalSearchResponseDto.convertHtmlTitleToText(item.getRoadAddress()))
							.buildName(NaverLocalSearchResponseDto.convertHtmlTitleToText(item.getTitle()))
							.build()
					))
					.toList()
			);
		}

		JusoAddressSearchApiRsp jusoAddressSearchTemplate = jusoAddressSearchApiClient.getAddress(
			jusoSecretKey,
			page + PageConfigConst.PAGE_INIT_ONE_NUM,
			srchPageNum != null ? srchPageNum : jusoCountPerPage,
			srchQry,
			jusoApiFormat,
			jusoFirstSort
		);

		List<Juso> jusoList = jusoAddressSearchTemplate.getResults().getJuso();
		getAddressList.addAll(
			jusoList.stream().filter((juso -> !juso.getRoadAddrPart1().trim().isEmpty())).map((juso ->
				GetAddress.builder()
					.roadAddr(juso.getRoadAddrPart1())
					.buildName(juso.getBdNm())
					.build())
			).toList());

		return getAddressList;
	}

	public List<GetMapSearchPostRsp> getAllMapPostBySrchQry(String srchQry, Integer page, Integer pageSize) {
		return snsPostRepository.findAllMapPostBySearchQuery(srchQry, page * pageSize,
				pageSize)
			.stream()
			.map((v) -> GetMapSearchPostRsp.builder().searchQueryName(v.getSearchQuery()).build())
			.toList();
	}

	public List<GetMapSearchRecommRsp> getMapRecommSearch(String srchQry) {
		NaverLocalSearchResponseDto naverLocalSearchResponseDto = naverApiClient.getLocalSearch(xNaverClientId,
			xNaverClientSecret,
			srchQry, PageConfigConst.MAP_SEARCH_RECOMM_PLAcE_PAGE_SIZE);

		List<GetMapSearchRecommRsp> getMapSearchRecommRsps = new ArrayList<>();

		getMapSearchRecommRsps.addAll(naverLocalSearchResponseDto.getItems().stream().map((item ->
			GetMapSearchRecommRsp.builder()
				.isPlace(true)
				.roadAddr(NaverLocalSearchResponseDto.convertHtmlTitleToText(item.getRoadAddress()))
				.placeName(NaverLocalSearchResponseDto.convertHtmlTitleToText(item.getTitle()))
				.latitude(NaverLocalSearchResponseDto.convertLatTextToFloat(item.getMapy()))
				.longitude(NaverLocalSearchResponseDto.convertLngTextToFloat(item.getMapx()))
				.build()
		)).toList());

		getMapSearchRecommRsps.addAll(
			getAllMapPostBySrchQry(srchQry, PageConfigConst.PAGE_INIT_NUM,
				PageConfigConst.MAP_SEARCH_RECOMM_POST_PAGE_SIZE).stream()
				.map((getMapSearchPostRsp ->
					GetMapSearchRecommRsp.builder()
						.isPlace(false)
						.searchQueryName(getMapSearchPostRsp.getSearchQueryName())
						.build())).toList()
		);

		return getMapSearchRecommRsps;

	}

}
