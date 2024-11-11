package com.postvue.feelogserver.app.posts.dto.common;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class Location {
	private Float latitude;
	private Float longitude;
	private String address;
}
