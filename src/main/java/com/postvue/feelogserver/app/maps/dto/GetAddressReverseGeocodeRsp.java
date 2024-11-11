package com.postvue.feelogserver.app.maps.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Setter
@Getter
public class GetAddressReverseGeocodeRsp {
	private String address;
	private String zipcode;
	private String latitude;
	private String longitude;
	private String buildName;
}
