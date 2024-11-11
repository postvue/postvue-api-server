package com.postvue.feelogserver.domain.snsblockusers.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.postvue.feelogserver.domain.snsblockusers.SnsBlockUser;
import com.postvue.feelogserver.domain.snsusers.SnsUser;

import org.springframework.data.repository.query.Param;

@Repository
public interface SnsBlockUserRepository extends JpaRepository<SnsBlockUser, Long>, JpaSpecificationExecutor<SnsBlockUser> {

	void deleteBySnsBlockerUser_IdAndSnsBlockedUser_Id(Long blockerUserId,
		Long blockedUserId);

	Optional<SnsBlockUser> findBySnsBlockerUser_IdAndSnsBlockedUser_Id(
		Long myUserId, Long otherUserId);

	@Query("SELECT SNS_U FROM SnsBlockUser AS SNS_BU INNER JOIN SnsUser AS SNS_U ON SNS_BU.snsBlockedUser = SNS_U WHERE SNS_BU.snsBlockerUser.id = :snsUserId")
	List<SnsUser> findBlockedUserListByPageable(
		@Param("snsUserId") Long snsUserId,
		Pageable pageable);
}
