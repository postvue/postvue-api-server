package com.postvue.feelogserver.domain.sessionsnsactiveusers.repository;

import java.util.HashMap;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import com.postvue.feelogserver.domain.sessionsnsactiveusers.SessionSnsActiveUser;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class SessionSnsActiveUserNativeRepository {
	public final RedisTemplate<String, Object> redisTemplate;
	public final RedisTemplate<String, String> redisTemplateTest;

	// RedisTemplate의 multiGet 메서드를 사용하여 여러 값을 한 번에 조회
	public HashMap<Long, SessionSnsActiveUser> findAllByUserIdSet(Set<String> userIdSet) {
		return Objects.requireNonNull(redisTemplate.opsForValue().multiGet(userIdSet))
			.stream()
			.filter((Objects::nonNull))
			.map((o -> (SessionSnsActiveUser)o))
			.collect(Collectors.toMap(
				SessionSnsActiveUser::getUserId,
				sessionSnsActiveUser -> sessionSnsActiveUser,
				(existing, replacement) -> existing,
				HashMap::new));
	}
}
