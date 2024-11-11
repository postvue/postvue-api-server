package com.postvue.feelogserver.domain.snstagfollows.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.postvue.feelogserver.domain.snstagfollows.SnsTagFollow;
import com.postvue.feelogserver.domain.snsusers.SnsUser;

@Repository
public interface SnsTagFollowRepository extends JpaRepository<SnsTagFollow, Long>, JpaSpecificationExecutor<SnsTagFollow> {
	List<SnsTagFollow> findAllBySnsUser(SnsUser snsUser);
}
