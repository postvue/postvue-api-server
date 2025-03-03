package com.postvue.feelogserver.domain.snsblockusers.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
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

	@Query("SELECT CASE WHEN COUNT(SNS_U) > 0 THEN TRUE ELSE FALSE END " +
		"FROM SnsUser AS SNS_U " +
		"LEFT OUTER JOIN SnsBlockUser AS SNS_BU " +
		"ON (SNS_BU.snsBlockedUser.id = :myUserId AND SNS_BU.snsBlockerUser.id = :targetUserId) " +
		"OR SNS_BU.snsBlockedUser.id = :targetUserId AND SNS_BU.snsBlockerUser.id = :myUserId " +
		"WHERE (SNS_BU.snsBlockerUser IS NOT NULL) " )
	boolean findIsBlockUser(
		@Param("myUserId") Long myUserId,
		@Param("targetUserId") Long targetUserId
	);
	Optional<SnsBlockUser> findBySnsBlockerUser_IdAndSnsBlockedUser_Id(
		Long myUserId, Long otherUserId);

	@Query("SELECT CASE WHEN COUNT(SNS_BU) > 0 THEN TRUE ELSE FALSE END FROM SnsBlockUser SNS_BU "
		+ "WHERE (SNS_BU.snsBlockerUser.id = :myUserId and SNS_BU.snsBlockedUser.id =:followingId) "
		+ "OR (SNS_BU.snsBlockerUser.id = :followingId and SNS_BU.snsBlockedUser.id =:myUserId)")
	boolean findIsBlock(
		@Param("myUserId") Long myUserId,
		@Param("followingId") Long followingId);

	@Query("SELECT SNS_U FROM SnsBlockUser AS SNS_BU INNER JOIN SnsUser AS SNS_U ON SNS_BU.snsBlockedUser = SNS_U WHERE SNS_BU.snsBlockerUser.id = :snsUserId")
	List<SnsUser> findBlockedUserListByPageable(
		@Param("snsUserId") Long snsUserId,
		Pageable pageable);
}
