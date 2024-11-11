package com.postvue.feelogserver.global.util.response;

import java.util.ArrayList;
import java.util.List;

import com.postvue.feelogserver.global.constant.PageConfigConst;

public final class ObjectConvertRspUtil<T, S> {
	public static <T, S> T GenericObjectListRsp(List<S> objectList, ObjectConverter<T, S> converter, String cursorId) {
		if (objectList.isEmpty()) {
			return converter.create(PageConfigConst.ZERO_ID, new ArrayList<>());
		} else {
			return converter.create(
				cursorId,
				objectList
			);
		}
	}

	public interface ObjectConverter<T, S> {
		T create(String cursorId, List<S> list);
	}
}
