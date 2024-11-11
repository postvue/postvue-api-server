package com.postvue.feelogserver.app.maps.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Setter
@Getter
public class GetLocalSearchRsp {
	private String roadAddr;
	private String placeName;
	private Boolean hasLocation;
	private Float latitude;
	private Float longitude;
}
