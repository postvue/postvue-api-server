package com.postvue.feelogserver.app.maps.dto;

import java.util.List;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Setter
@Getter
public class GetPlaceByCategoryRsp {
	private String roadAddr;
	private String buildName;
	private Integer distance;
	private List<String> category;
	private String placeUrl;
	private Float latitude;
	private Float longitude;
}
