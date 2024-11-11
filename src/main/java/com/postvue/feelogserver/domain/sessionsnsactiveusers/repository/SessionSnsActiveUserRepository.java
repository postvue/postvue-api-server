package com.postvue.feelogserver.domain.sessionsnsactiveusers.repository;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.postvue.feelogserver.domain.sessionsnsactiveusers.SessionSnsActiveUser;

@Repository
public interface SessionSnsActiveUserRepository extends CrudRepository<SessionSnsActiveUser, Long> {
	Optional<SessionSnsActiveUser> findByUserId(String sessionId);

	Optional<SessionSnsActiveUser> findBySessionId(String sessionId);

}
