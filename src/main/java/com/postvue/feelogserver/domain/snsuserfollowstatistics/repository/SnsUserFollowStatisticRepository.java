package com.postvue.feelogserver.domain.snsuserfollowstatistics.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.postvue.feelogserver.domain.snsuserfollowstatistics.SnsUserFollowStatistic;

@Repository
public interface SnsUserFollowStatisticRepository extends JpaRepository<SnsUserFollowStatistic, Long>,
	JpaSpecificationExecutor<SnsUserFollowStatistic> {


	Optional<SnsUserFollowStatistic> findBySnsUser_id(Long snsUserId);

	@Query(value = "SELECT SNS_UFS FROM SnsUserFollowStatistic AS SNS_UFS WHERE SNS_UFS.snsUser.id IN :snsUserIdList")
	List<SnsUserFollowStatistic> findBySnsUserInIdList(
		@Param("snsUserIdList") List<Long> snsUserIdList);
}
