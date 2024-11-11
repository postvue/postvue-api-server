package com.postvue.feelogserver.app.maps.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Setter
@Getter
public class GetAddressGeocodeRsp {
	private String address;
	private Float latitude;
	private Float longitude;
}
