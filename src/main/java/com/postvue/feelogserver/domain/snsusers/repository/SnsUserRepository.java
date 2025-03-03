package com.postvue.feelogserver.domain.snsusers.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import com.postvue.feelogserver.domain.snsusers.SnsUser;
import com.postvue.feelogserver.domain.snsusers.dao.ProfileInfoWithFollowDao;
import com.postvue.feelogserver.domain.snsusers.dao.ProfileUserByUsernameDao;
import com.postvue.feelogserver.domain.snsusers.dao.ProfileUserWithFollowByUsernameDao;
import com.postvue.feelogserver.domain.snsusers.vo.SignUpType;

import org.springframework.data.repository.query.Param;

public interface SnsUserRepository extends JpaRepository<SnsUser, Long>,JpaSpecificationExecutor<SnsUser> {


	@Query("SELECT SNS_S FROM SnsUser AS SNS_S WHERE LOWER(SNS_S.username) = LOWER(:username)")
	Optional<SnsUser> findByUsername(@Param("username") String username);


	@Query("SELECT SNS_S FROM SnsUser  AS SNS_S WHERE SNS_S.signupEmail = :signupEmail AND SNS_S.signUpType = com.postvue.feelogserver.domain.snsusers.vo.SignUpType.EMAIL")
	Optional<SnsUser> findBySignupEmail(@Param("signupEmail") String signupEmail);

	// @Query("SELECT SNS_S FROM SnsUser  AS SNS_S WHERE SNS_S.signupEmail = :signupEmail AND SNS_S.signUpType = com.postvue.feelogserver.domain.snsusers.vo.SignUpType.APPLE")
	// Optional<SnsUser> findBySignupAppleEmail(@Param("signupAppleEmail") String signupAppleEmail);


	@Query(value =
		"SELECT "
			+ "SNS_U.id AS snsUserId,"
			+ "SNS_U.username AS username,"
			+ "SNS_U.nickname AS nickname,"
			+ "SNS_U.userLink AS userLink,"
			+ "SNS_U.userDescription AS userDescription,"
			+ "SNS_U.profilePath AS profilePath,"
			+ "(CASE WHEN SNS_UF.followingUser IS NOT NULL THEN TRUE ELSE FALSE END) AS isFollowed, "
			+ "(CASE WHEN SNS_BU.snsBlockedUser IS NOT NULL THEN TRUE ELSE FALSE END) AS isBlocked, "
			+ "EXISTS ( SELECT 1 FROM SnsBlockUser AS SNS_B WHERE SNS_B.snsBlockedUser.id = :myUserId AND SNS_B.snsBlockerUser.id = SNS_U.id ) AS isBlockerUser, "
			+ "(CASE "
			+ "    WHEN (SNS_U.id != COALESCE(:myUserId, -1) AND SNS_U.isPrivateProfile IS TRUE) THEN TRUE "
			+ "    ELSE FALSE "
			+ "END) AS isPrivate, "
			+ "(SELECT COUNT(*) FROM SnsUserFollow AS SNS_UF WHERE SNS_UF.followerUser.id = SNS_U.id) AS followingNum,"
			+ "(SELECT COUNT(*) FROM SnsUserFollow AS SNS_UF WHERE SNS_UF.followingUser.id = SNS_U.id) AS followerNum "
			+ "FROM SnsUser AS SNS_U "
			+ "LEFT OUTER JOIN SnsUserFollow AS SNS_UF "
			+ "ON SNS_U.id = SNS_UF.followingUser.id "
			+ "AND SNS_UF.followerUser.id = :myUserId "
			+ "LEFT OUTER JOIN SnsBlockUser AS SNS_BU "
			+ "ON SNS_BU.snsBlockerUser.id = :myUserId "
			+ "AND SNS_BU.snsBlockedUser.id = SNS_U.id "
			+ "WHERE LOWER(SNS_U.username) = LOWER(:username) "
			+ "AND SNS_U.snsUserState != com.postvue.feelogserver.domain.snsusers.vo.SnsUserState.FULL_DELETED ")
	Optional<ProfileInfoWithFollowDao> findByUsernameWithFollowInfo(
		@Param("username") String username,
		@Param("myUserId") Long myUserId);

	@Query("SELECT SU FROM SnsUser SU "
		+ "WHERE SU.snsUserState != com.postvue.feelogserver.domain.snsusers.vo.SnsUserState.FULL_DELETED "
		+ "AND SU.signUpType = :signUpType "
		+ "AND SU.socialId = :socialId ")
	Optional<SnsUser> findNotFullDeletedUser(
		@Param("socialId") String socialId,
		@Param("signUpType") SignUpType signUpType);

	Optional<SnsUser> findByRefreshToken(String refreshToken);

	@Query("SELECT SNS_S FROM SnsUser AS SNS_S "
		+ "WHERE SNS_S.id = :id AND"
		+ " SNS_S.snsUserState != com.postvue.feelogserver.domain.snsusers.vo.SnsUserState.FULL_DELETED")
	Optional<SnsUser> findByNotFullDeleted(@Param("id") Long id);
	Optional<SnsUser> findByIdAndDeletedAtIsNull(Long id);

	@Query(value =
		"SELECT "
			+ "SNS_U.id AS snsUserId,"
			+ "SNS_U.username AS username,"
			+ "SNS_U.nickname AS nickname,"
			+ "SNS_U.profilePath AS profilePath "
			+ "FROM SnsUser AS SNS_U "
			+ "LEFT JOIN SnsBlockUser SNS_BU "
			+ "ON SNS_BU.snsBlockerUser.id = :myUserId "
			+ "AND SNS_BU.snsBlockedUser.id = SNS_U.id "
			+ "WHERE SNS_U.id < :cursorId "
			+ "AND (LOWER(SNS_U.username) LIKE CONCAT(LOWER(:username), '%') OR LOWER(SNS_U.nickname) LIKE CONCAT(LOWER(:username), '%') )"
			+ "AND SNS_BU.snsBlockedUser IS NULL "
			+ "ORDER BY SNS_U.id DESC")
	List<ProfileUserByUsernameDao> findAllUserByUsername(@Param("username") String username,
		@Param("myUserId") Long myUserId,
		@Param("cursorId") Long cursorId,
		Pageable pageable);

	@Query(value =
		"SELECT "
			+ "SNS_U.id AS snsUserId,"
			+ "SNS_U.username AS username,"
			+ "SNS_U.nickname AS nickname,"
			+ "SNS_U.profilePath AS profilePath, "
			+ "CASE WHEN SNS_UF.followingUser.id IS NULL THEN FALSE ELSE TRUE END AS isFollowed "
			+ "FROM SnsUser AS SNS_U "
			+ "LEFT JOIN SnsBlockUser SNS_BU "
			+ "ON (SNS_BU.snsBlockerUser.id = :myUserId AND SNS_BU.snsBlockedUser.id = SNS_U.id) "
			+ "OR (SNS_BU.snsBlockerUser.id = SNS_U.id AND SNS_BU.snsBlockedUser.id = :myUserId) "
			+ "LEFT JOIN SnsUserFollow SNS_UF "
			+ "ON SNS_UF.followingUser.id = SNS_U.id "
			+ "AND SNS_UF.followerUser.id = :myUserId "
			+ "WHERE SNS_U.id < :cursorId "
			+ "AND SNS_U.deletedAt IS NULL "
			+ "AND (LOWER(SNS_U.username) ILIKE CONCAT(LOWER(:username), '%') OR LOWER(SNS_U.nickname) ILIKE CONCAT(LOWER(:username), '%') )"
			+ "AND SNS_BU.snsBlockedUser IS NULL "
			+ "AND SNS_U.id != :myUserId "
			+ "ORDER BY SNS_U.id DESC")
	List<ProfileUserWithFollowByUsernameDao> findAllUserWithFollowByUsername(
		@Param("username") String username,
		@Param("myUserId") Long myUserId,
		@Param("cursorId") Long cursorId,
		Pageable pageable);

	@Query(value = "SELECT SNS_U FROM SnsUser SNS_U WHERE SNS_U.snsUserState = com.postvue.feelogserver.domain.snsusers.vo.SnsUserState.DELETED AND SNS_U.deletedAt < :daysAgo ORDER BY SNS_U.deletedAt DESC")
	List<SnsUser> findDeletedUserOlderThanDays(
		@Param("daysAgo") LocalDateTime daysAgo);
}
