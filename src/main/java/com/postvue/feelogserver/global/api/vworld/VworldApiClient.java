package com.postvue.feelogserver.global.api.vworld;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.postvue.feelogserver.global.api.vworld.dto.rsp.VworldGetGeocodeRsp;
import com.postvue.feelogserver.global.api.vworld.dto.rsp.VworldGetReverseGeocodeRsp;

@FeignClient(
	name = "vworldApiClient",
	url = VworldRspConst.VWORLD_API_PATH,
	configuration = VworldFeignClientConfiguration.class // 필요한 경우 Feign 관련 설정을 추가합니다.
)
public interface VworldApiClient {
	@GetMapping("/req/address")
	VworldGetReverseGeocodeRsp getAddressReverseGeocode(
		@RequestParam("service") String service,
		@RequestParam("request") String requestType,
		@RequestParam("version") String version,
		@RequestParam("format") String format,
		@RequestParam("crs") String crs,
		@RequestParam("type") String type,
		@RequestParam("point") String point,
		@RequestParam("key") String apikey
	);

	@GetMapping("/req/address")
	VworldGetGeocodeRsp getAddressGeocode(
		@RequestParam("service") String service,
		@RequestParam("request") String requestType,
		@RequestParam("version") String version,
		@RequestParam("address") String address,
		@RequestParam("format") String format,
		@RequestParam("crs") String crs,
		@RequestParam("type") String type,
		@RequestParam("key") String apiKey
	);
}
