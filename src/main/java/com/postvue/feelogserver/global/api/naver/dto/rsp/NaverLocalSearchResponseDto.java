package com.postvue.feelogserver.global.api.naver.dto.rsp;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NaverLocalSearchResponseDto {

	private String lastBuildDate;
	private int total;
	private int start;
	private int display;
	private List<Item> items;

	@Getter
	@Setter
	public static class Item {
		private String title;
		private String link;
		private String category;
		private String description;
		private String telephone;
		private String address;
		private String roadAddress;
		private String mapx;
		private String mapy;
	}

	public static String convertHtmlTitleToText(String htmlTitle) {
		return htmlTitle.replace("<b>", "").replace("</b>", " ");
	}

	public static Float convertLatTextToFloat(String latitude) {
		return Float.valueOf(latitude.subSequence(0, 2) + "." + latitude.substring(2));
	}

	public static Float convertLngTextToFloat(String longitude) {
		return Float.valueOf(longitude.subSequence(0, 3) + "." + longitude.substring(3));
	}
}
