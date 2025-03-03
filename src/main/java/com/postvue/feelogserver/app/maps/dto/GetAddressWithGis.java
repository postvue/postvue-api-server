package com.postvue.feelogserver.app.maps.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Setter
@Getter
public class GetAddressWithGis {
	private String roadAddr;
	private String buildName;
	private Boolean hasAddress;
	private Float latitude;
	private Float longitude;
}
