package com.postvue.feelogserver.global.api.juso.addresssearch.dto;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class JusoAddressSearchTemplate {
	private Common common;
	private List<Juso> juso;
}
