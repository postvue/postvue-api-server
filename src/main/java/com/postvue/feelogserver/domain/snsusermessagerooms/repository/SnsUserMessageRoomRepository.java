package com.postvue.feelogserver.domain.snsusermessagerooms.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.postvue.feelogserver.domain.snsusermessagerooms.SnsUserMessageRoom;

@Repository
public interface SnsUserMessageRoomRepository extends JpaRepository<SnsUserMessageRoom, Long>,
	JpaSpecificationExecutor<SnsUserMessageRoom> {

}
