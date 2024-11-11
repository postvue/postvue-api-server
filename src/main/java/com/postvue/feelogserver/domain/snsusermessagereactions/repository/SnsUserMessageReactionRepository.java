package com.postvue.feelogserver.domain.snsusermessagereactions.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.postvue.feelogserver.domain.snsusermessagereactions.SnsUserMessageReaction;

@Repository
public interface SnsUserMessageReactionRepository extends JpaRepository<SnsUserMessageReaction,Long>,
	JpaSpecificationExecutor<SnsUserMessageReaction> {
}
