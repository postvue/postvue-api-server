package com.postvue.feelogserver.app.maps.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Setter
@Getter
public class GetMapSearchRecommRsp {
	private Boolean isPlace;
	private String roadAddr;
	private String placeName;
	private Boolean hasLocation;
	private Float latitude;
	private Float longitude;

	private String searchQueryName;
}
