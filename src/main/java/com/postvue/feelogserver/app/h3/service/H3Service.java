package com.postvue.feelogserver.app.h3.service;

import com.postvue.feelogserver.global.constant.H3Const;
import com.uber.h3core.H3Core;

import org.springframework.stereotype.Service;
import java.io.IOException;
import java.util.List;

@Service
public class H3Service {
	private final H3Core h3;


	public H3Service() throws IOException {
		this.h3 = H3Core.newInstance();
	}

	// 위도/경도를 H3 인덱스로 변환
	public String getLatLngToH3Address(double latitude, double longitude) {
		return h3.latLngToCellAddress(latitude, longitude, H3Const.h3Resource);
	}

	public Long getLatLngToH3Cell(double latitude, double longitude) {
		return h3.latLngToCell(latitude, longitude, H3Const.h3Resource);
	}

	/**
	 * @param latitude latitude
	 * @param longitude longitude
	 * @param distance Distance (1: 1km, 2:2km)
	 */
	public List<String> getNearbyH3CellAddresses(double latitude, double longitude,
		int distance
	) {
		String h3Index = h3.latLngToCellAddress(latitude, longitude, H3Const.h3Resource); // 해상도 9 사용
		return h3.gridDisk(h3Index, distance);
	}

	/**
	 * @param latitude latitude
	 * @param longitude longitude
	 * @param distance Distance (1: 1km, 2:2km)
	 */
	public List<Long> getNearbyH3Cells(double latitude, double longitude,
		int distance
	) {
		long h3Index = h3.latLngToCell(latitude, longitude, H3Const.h3Resource); // 해상도 9 사용
		return h3.gridDisk(h3Index, distance);
	}
}
