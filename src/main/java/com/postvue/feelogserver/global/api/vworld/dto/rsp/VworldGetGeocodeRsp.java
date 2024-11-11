package com.postvue.feelogserver.global.api.vworld.dto.rsp;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VworldGetGeocodeRsp {

	private Response response;

	@Getter
	@Setter
	public static class Response {
		private Service service;
		private String status;
		private Input input;
		private Refined refined;
		private Result result;
	}

	@Getter
	@Setter
	public static class Service {
		private String name;
		private String version;
		private String operation;
		private String time;
	}

	@Getter
	@Setter
	public static class Input {
		private String type;
		private String address;
	}

	@Getter
	@Setter
	public static class Refined {
		private String text;
		private Structure structure;
	}

	@Getter
	@Setter
	public static class Structure {
		private String level0;
		private String level1;
		private String level2;
		private String level3;
		private String level4L;
		private String level4LC;
		private String level4A;
		private String level4AC;
		private String level5;
		private String detail;
	}

	@Getter
	@Setter
	public static class Result {
		private String crs;
		private GeocodePoint point;
	}

	@Getter
	@Setter
	public static class GeocodePoint {
		private String x;
		private String y;
	}
}
