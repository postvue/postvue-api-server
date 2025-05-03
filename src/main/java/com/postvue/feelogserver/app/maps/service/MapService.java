package com.postvue.feelogserver.app.maps.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.geolatte.geom.crs.CoordinateReferenceSystem;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.postvue.feelogserver.app.maps.dto.GetAddress;
import com.postvue.feelogserver.app.maps.dto.GetAddressGeocodeRsp;
import com.postvue.feelogserver.app.maps.dto.GetAddressReverseGeocodeRsp;
import com.postvue.feelogserver.app.maps.dto.GetAddressWithGis;
import com.postvue.feelogserver.app.maps.dto.GetLocalSearchRsp;
import com.postvue.feelogserver.app.maps.dto.GetMapSearchPostRsp;
import com.postvue.feelogserver.app.maps.dto.GetMapSearchRecommRsp;
import com.postvue.feelogserver.app.maps.dto.GetPlaceByCategoryRsp;
import com.postvue.feelogserver.domain.snsposts.repository.SnsPostRepository;
import com.postvue.feelogserver.global.api.juso.addresssearch.JusoAddressSearchApiClient;
import com.postvue.feelogserver.global.api.juso.addresssearch.dto.Juso;
import com.postvue.feelogserver.global.api.juso.addresssearch.dto.JusoAddressSearchApiRsp;
import com.postvue.feelogserver.global.api.kakao.client.localapi.KakaoLocalApiClient;
import com.postvue.feelogserver.global.api.kakao.dto.rsp.KakaoPlaceDto;
import com.postvue.feelogserver.global.api.kakao.dto.rsp.KakaoSearchResponseDto;
import com.postvue.feelogserver.global.api.naver.NaverApiClient;
import com.postvue.feelogserver.global.api.naver.dto.rsp.NaverLocalSearchResponseDto;
import com.postvue.feelogserver.global.api.vworld.VworldApiClient;
import com.postvue.feelogserver.global.api.vworld.VworldRspConst;
import com.postvue.feelogserver.global.api.vworld.dto.rsp.VworldGetAddressResultRsp;
import com.postvue.feelogserver.global.api.vworld.dto.rsp.VworldGetGeocodeRsp;
import com.postvue.feelogserver.global.api.vworld.dto.rsp.VworldGetReverseGeocodeRsp;
import com.postvue.feelogserver.global.constant.KakaoApiConst;
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
	private final KakaoLocalApiClient kakaoLocalApiClient;

	@Value("${openapi.naverdevelopers.xNaverClientId}")
	private String xNaverClientId;

	@Value("${openapi.naverdevelopers.xNaverClientSecret}")
	private String xNaverClientSecret;

	@Value("${openapi.kakaodevelopers.restApi}")
	private String kakaoRestApi;

	@Value("${openapi.naverdevelopers.mapLocalMaxSrchNum}")
	private Integer mapLocalMaxSrchNum;

	public GetAddressReverseGeocodeRsp getAddressReverseGeocode(String latitude, String longitude) {

		VworldGetReverseGeocodeRsp vworldGetReverseGeocodeRsp = vworldApiClient.getAddressReverseGeocode(
			addressServiceParam, reverseGeocodeParam, vworldVersion, apiFormat,
			crs, rspType,
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
			if (Objects.equals(vworldGetReverseGeocodeRsp.getResponse().getStatus(), VworldRspConst.VWORLD_NOT_FOUND_RSP_STATUS)){
				return null;
			}
			else{
				throw new BadRequestErrorException("해당 gps 정보가 DB 상에 존재하지 않습니다.");
			}
		}
	}

	public List<GetAddressReverseGeocodeRsp> getAddressListReverseGeocode(String latitude, String longitude) {

		VworldGetReverseGeocodeRsp vworldGetReverseGeocodeRsp = vworldApiClient.getAddressReverseGeocode(
			addressServiceParam, reverseGeocodeParam, vworldVersion, apiFormat,
			crs, rspType,
			String.format("%s,%s", longitude, latitude), vworldClientId);

		if (Objects.equals(vworldGetReverseGeocodeRsp.getResponse().getStatus(), VworldRspConst.VWORLD_OK_RSP_STATUS)) {
			List<VworldGetAddressResultRsp> vworldGetAddressResultRsps = vworldGetReverseGeocodeRsp.getResponse()
				.getResult();
			if (!vworldGetAddressResultRsps.isEmpty()) {
				return vworldGetAddressResultRsps.stream()
					.map((vworldGetAddressResultRsp -> {
						return GetAddressReverseGeocodeRsp.builder()
							.address(vworldGetAddressResultRsp.getStructure().getLevel1() + " "
								+ vworldGetAddressResultRsp.getStructure().getLevel2() + " "
								+ vworldGetAddressResultRsp.getStructure().getLevel4L() + " "
								+ vworldGetAddressResultRsp.getStructure().getLevel5())
							.buildName(vworldGetAddressResultRsp.getStructure().getDetail())
							.zipcode(vworldGetAddressResultRsp.getText())
							.latitude(vworldGetReverseGeocodeRsp.getResponse().getInput().getPoint().getY())
							.longitude(vworldGetReverseGeocodeRsp.getResponse().getInput().getPoint().getX())
							.build();
					}))
					.toList();

			} else {
				throw new InternalServerErrorException("주소 정보가 없습니다.");
			}
		} else {
			if (Objects.equals(vworldGetReverseGeocodeRsp.getResponse().getStatus(), VworldRspConst.VWORLD_NOT_FOUND_RSP_STATUS)){
				return List.of();
			}
			else{
				throw new BadRequestErrorException("해당 gps 정보가 DB 상에 존재하지 않습니다.");
			}
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

	// maxKakaoRequestPageNum: 카카오 최대 호출 페이지 수
	public List<GetAddressWithGis> getAddressRelationBySrchQryByGis(String srchQry,
		Boolean hasLocalAddress,
		Integer maxKakaoRequestPageNum,
		Integer page, Float latitude,
		Float longitude, Integer srchPageNum) {

		if (page > MapConst.MAX_KAKAO_LOCAL_SEARCH_PAGE_NUM){
			return new ArrayList<>();
		}

		List<GetAddressWithGis> getAddressList = new ArrayList<>();

		// 맨 앞 부분만 요청 되게
		if (page <= maxKakaoRequestPageNum && hasLocalAddress) {
			KakaoSearchResponseDto kakaoSearchResponseDto = kakaoLocalApiClient.getLocalSearch(
				KakaoApiConst.kakaoAKHeader + kakaoRestApi,
				srchQry,
				longitude,
				latitude,
				page * PageConfigConst.KAKAO_MAP_LOCAL_SEARCH_QUERY_PAGE_NUM + PageConfigConst.PAGE_INIT_ONE_NUM,
				page + PageConfigConst.KAKAO_MAP_LOCAL_SEARCH_QUERY_PAGE_NUM
			);

			getAddressList.addAll(
				kakaoSearchResponseDto.getDocuments().stream()
					.map(kakaoPlaceDto ->
						GetAddressWithGis.builder()
							.roadAddr(kakaoPlaceDto.getRoadAddressName())
							.buildName(kakaoPlaceDto.getPlaceName())
							.hasAddress(true)
							.latitude(kakaoPlaceDto.getY().floatValue())
							.longitude(kakaoPlaceDto.getX().floatValue())
							.build()).toList()
			);
		}


		List<Juso> jusoList = returnAddressByJuso(page, srchPageNum, srchQry);
		getAddressList.addAll(
			jusoList.stream().filter((juso -> !juso.getRoadAddrPart1().trim().isEmpty())).map((juso ->
				GetAddressWithGis.builder()
					.roadAddr(juso.getRoadAddrPart1())
					.buildName(juso.getBdNm())
					.hasAddress(false)
					.longitude(null)
					.latitude(null)
					.build())
			).toList());

		return getAddressList;
	}

	public List<GetPlaceByCategoryRsp> getPlaceByCategory(
		Float latitude,
		Float longitude) {

		String foodCategoryGroupCode = "FD6"; // @REFER: 나중에 상수로 관리, 음식점
		Integer foodCategoryRadius = 420; // @REFER: 나중에 상수로 관리
		Integer foodCategoryPage = 0;
		String sortStr = "distance"; // @REFER: 나중에 상수로 관리,거리순

		// 맨 앞 부분만 요청 되게
		return getGetPlaceByCategoryRsps(latitude, longitude, foodCategoryPage, foodCategoryGroupCode,
			foodCategoryRadius,sortStr);
	}

	public List<GetPlaceByCategoryRsp> getPlaceByCategory(
		Float latitude,
		Float longitude,
		Integer page
	) {

		String foodCategoryGroupCode = "FD6"; // @REFER: 나중에 상수로 관리, 음식점
		Integer foodCategoryRadius = 420; // @REFER: 나중에 상수로 관리
		String sortStr = "distance"; // @REFER: 나중에 상수로 관리,거리순

		// 맨 앞 부분만 요청 되게
		return getGetPlaceByCategoryRsps(latitude, longitude, page, foodCategoryGroupCode, foodCategoryRadius, sortStr);
	}

	private List<GetPlaceByCategoryRsp> getGetPlaceByCategoryRsps(Float latitude, Float longitude, Integer page,
		String foodCategoryGroupCode, Integer foodCategoryRadius,String sortStr) {

		List<KakaoPlaceDto> kakaoPlaceDtoList = new ArrayList<>(List.of());
		kakaoPlaceDtoList.addAll(kakaoLocalApiClient.getPlaceByCategory(
			KakaoApiConst.kakaoAKHeader + kakaoRestApi,
			foodCategoryGroupCode,
			longitude,
			latitude,
			foodCategoryRadius,
			PageConfigConst.PAGE_INIT_ONE_NUM + page * 2,
			PageConfigConst.KAKAO_MAP_LOCAL_SEARCH_QUERY_PAGE_NUM,sortStr
		).getDocuments());

		kakaoPlaceDtoList.addAll(kakaoLocalApiClient.getPlaceByCategory(
			KakaoApiConst.kakaoAKHeader + kakaoRestApi,
			foodCategoryGroupCode,
			longitude,
			latitude,
			foodCategoryRadius,
			PageConfigConst.PAGE_INIT_ONE_NUM + 1 + page * 2,
			PageConfigConst.KAKAO_MAP_LOCAL_SEARCH_QUERY_PAGE_NUM,sortStr
		).getDocuments());

		kakaoPlaceDtoList = kakaoPlaceDtoList.stream()
			.filter(kakaoPlaceDto ->
				!kakaoPlaceDto.getCategoryName().contains("치킨") &&
					!kakaoPlaceDto.getCategoryName().contains("실내포장마차") &&
					!kakaoPlaceDto.getCategoryName().contains("패밀리레스토랑") &&
					!kakaoPlaceDto.getCategoryName().contains("간식") &&
					!kakaoPlaceDto.getCategoryName().contains("양꼬치") &&
					!kakaoPlaceDto.getCategoryName().contains("호프") &&
					!kakaoPlaceDto.getCategoryName().contains("고기") &&
					!kakaoPlaceDto.getCategoryName().contains("스테이크,립") &&
					!kakaoPlaceDto.getCategoryName().contains("참치회")
			)
			.toList();

		int toIndex = Math.min(kakaoPlaceDtoList.size(), 15);
		kakaoPlaceDtoList = kakaoPlaceDtoList.subList(0,toIndex);

		return kakaoPlaceDtoList.stream()
			.map(kakaoPlaceDto ->
				GetPlaceByCategoryRsp.builder()
					.roadAddr(kakaoPlaceDto.getRoadAddressName())
					.buildName(kakaoPlaceDto.getPlaceName())
					.latitude(kakaoPlaceDto.getY().floatValue())
					.longitude(kakaoPlaceDto.getX().floatValue())
					.distance(Integer.parseInt(kakaoPlaceDto.getDistance()))
					.category(parseCategory(kakaoPlaceDto.getCategoryName()))
					.placeUrl(kakaoPlaceDto.getPlaceUrl())
					.build()).toList();
	}

	private List<String> parseCategory(String category) {
		String[] parts = category.split(">");
		List<String> result = new ArrayList<>();

		for (String part : parts) {
			String trimmed = part.trim();


			// 특정 키워드 치환
			if (trimmed.equals("육류,고기")) {
				trimmed = "고기";
			}


			result.add(trimmed);
		}

		// 5단계로 맞추기
		while (result.size() < 5) {
			result.add("");
		}

		return result;
	}

	public List<GetLocalSearchRsp> getSearchLocalWithGis(String srchQry, Float lat, Float lng) {
		List<GetAddressWithGis> addressWithGisList = getAddressRelationBySrchQryByGis(srchQry,
			true, PageConfigConst.PAGE_INIT_NUM,0, lat, lng,
			MapConst.MAG_LOCAL_SEARCH_REVERSE_GEOCODE_MAX_NUM);

		return addressWithGisList.stream().map((addressWithGis ->
			GetLocalSearchRsp.builder()
				.roadAddr(addressWithGis.getRoadAddr())
				.placeName(addressWithGis.getBuildName())
				.hasLocation(addressWithGis.getHasAddress())
				.latitude(addressWithGis.getLatitude())
				.longitude(addressWithGis.getLongitude())
				.build()
		)).toList();
	}

	public List<GetLocalSearchRsp> getSearchLocal(String srchQry) {

		// 네이버 검색
		NaverLocalSearchResponseDto naverLocalSearchResponseDto = naverApiClient.getLocalSearch(xNaverClientId,
			xNaverClientSecret,
			srchQry, mapLocalMaxSrchNum);

		List<GetAddress> getAddressList = getAddressRelationBySrchQry(srchQry, false, PageConfigConst.PAGE_INIT_NUM, 0,
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
		Integer maxNaverRequestPageNum,
		Integer page, Integer srchPageNum) {

		List<GetAddress> getAddressList = new ArrayList<>();
		if (page <= maxNaverRequestPageNum && hasLocalAddress) {
			NaverLocalSearchResponseDto naverLocalSearchResponseDto = naverApiClient.getLocalSearch(xNaverClientId,
				xNaverClientSecret,
				srchQry, mapLocalMaxSrchNum);
			getAddressList.addAll(
				naverLocalSearchResponseDto.getItems()
					.stream()
					.filter((item -> !NaverLocalSearchResponseDto.convertHtmlTitleToText(item.getRoadAddress())
						.trim()
						.isEmpty()))
					.map((item -> {
						return GetAddress.builder()
							.roadAddr(NaverLocalSearchResponseDto.convertHtmlTitleToText(item.getRoadAddress()))
							.buildName(NaverLocalSearchResponseDto.convertHtmlTitleToText(item.getTitle()))
							.latitude((float)convertNaverCoordToGPS(Integer.parseInt(item.getMapy())))
							.longitude((float)convertNaverCoordToGPS(Integer.parseInt(item.getMapx())))
							.build();
						}
					))
					.toList()
			);
		}

		List<Juso> jusoList = returnAddressByJuso(page, srchPageNum, srchQry);
		getAddressList.addAll(
			jusoList.stream().filter((juso -> !juso.getRoadAddrPart1().trim().isEmpty())).map((juso ->
				GetAddress.builder()
					.roadAddr(juso.getRoadAddrPart1())
					.buildName(juso.getBdNm())
					.build())
			).toList());

		return getAddressList;
	}

	@Transactional
	public List<GetMapSearchPostRsp> getAllMapPostBySrchQry(String srchQry, Integer page, Integer pageSize, Long snsUserId) {
		return snsPostRepository.findAllMapPostBySearchQuery(srchQry, page * pageSize,
				pageSize, snsUserId)
			.stream()
			.map((v) -> GetMapSearchPostRsp.builder().searchQueryName(v.getSearchQuery()).build())
			.toList();
	}

	@Transactional
	public List<GetMapSearchRecommRsp> getMapRecommSearch(String srchQry, Long snsUserId) {
		NaverLocalSearchResponseDto naverLocalSearchResponseDto = naverApiClient.getLocalSearch(xNaverClientId,
			xNaverClientSecret,
			srchQry, PageConfigConst.MAP_SEARCH_RECOMM_PLAcE_PAGE_SIZE);

		KakaoSearchResponseDto kakaoSearchResponseDto = kakaoLocalApiClient.getLocalSearchNotGis(
			KakaoApiConst.kakaoAKHeader + kakaoRestApi,
			srchQry,
			PageConfigConst.PAGE_INIT_ONE_NUM,
			PageConfigConst.KAKAO_MAP_LOCAL_SEARCH_QUERY_PAGE_NUM
		);



		List<GetMapSearchRecommRsp> getMapSearchRecommRsps = new ArrayList<>();

		getMapSearchRecommRsps.addAll(
			getAllMapPostBySrchQry(srchQry, PageConfigConst.PAGE_INIT_NUM,
				PageConfigConst.MAP_SEARCH_RECOMM_POST_PAGE_SIZE, snsUserId).stream()
				.map((getMapSearchPostRsp ->
					GetMapSearchRecommRsp.builder()
						.hasLocation(false)
						.isPlace(false)
						.searchQueryName(getMapSearchPostRsp.getSearchQueryName())
						.build())).toList()
		);

		getMapSearchRecommRsps.addAll(naverLocalSearchResponseDto.getItems().stream().map((item ->
			GetMapSearchRecommRsp.builder()
				.hasLocation(true)
				.isPlace(true)
				.roadAddr(NaverLocalSearchResponseDto.convertHtmlTitleToText(item.getRoadAddress()))
				.placeName(NaverLocalSearchResponseDto.convertHtmlTitleToText(item.getTitle()))
				.latitude(NaverLocalSearchResponseDto.convertLatTextToFloat(item.getMapy()))
				.longitude(NaverLocalSearchResponseDto.convertLngTextToFloat(item.getMapx()))
				.build()
		)).toList());

		getMapSearchRecommRsps.addAll(
			kakaoSearchResponseDto.getDocuments().stream()
				.map(kakaoPlaceDto ->
					GetMapSearchRecommRsp.builder()
						.hasLocation(true)
						.isPlace(true)
						.roadAddr(kakaoPlaceDto.getRoadAddressName())
						.placeName(kakaoPlaceDto.getPlaceName())
						.latitude(kakaoPlaceDto.getY().floatValue())
						.longitude(kakaoPlaceDto.getX().floatValue())
						.build()).toList()
		);

		return getMapSearchRecommRsps;

	}

	private List<Juso> returnAddressByJuso (Integer page, Integer srchPageNum, String srchQry) {
		JusoAddressSearchApiRsp jusoAddressSearchTemplate = jusoAddressSearchApiClient.getAddress(
			jusoSecretKey,
			page + PageConfigConst.PAGE_INIT_ONE_NUM,
			srchPageNum != null ? srchPageNum : jusoCountPerPage,
			srchQry,
			jusoApiFormat,
			jusoFirstSort
		);

		return jusoAddressSearchTemplate.getResults().getJuso();
	}

	double convertNaverCoordToGPS(int coord) {
		return coord / 1e7;
	}

	public boolean isInSouthKorea(double latitude, double longitude) {
		return (latitude >= 33.0 && latitude <= 43.0) &&
			(longitude >= 124.0 && longitude <= 132.0);
	}
}
