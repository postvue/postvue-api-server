package com.postvue.feelogserver.domain.snsposts.repository;

import java.util.List;

import com.postvue.feelogserver.domain.snsposts.dto.PostDao;

public interface SnsPostCustomRepository {
	List<PostDao> findNearbyPosts(Long snsUserId, Float userLatitude, Float userLongitude,
		String postContentBusinessType, int pageSize, int offset);

}
