package com.postvue.feelogserver.global.api.vworld.dto.rsp;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class InputInfo {
	private InputPoint point;
	private String crs;
	private String type;
}
