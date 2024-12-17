package com.postvue.feelogserver.domain.snsusers.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import com.postvue.feelogserver.domain.snsnotifications.SnsNotification;
import com.postvue.feelogserver.domain.snsuserfollows.dao.ProfileFollowDao;
import com.postvue.feelogserver.domain.snsusers.SnsUser;
import com.postvue.feelogserver.domain.snsusers.dao.ProfileInfoWithFollowDao;
import com.postvue.feelogserver.domain.snsusers.dao.ProfileUserByUsernameDao;
import com.postvue.feelogserver.domain.snsusers.dao.ProfileUserWithFollowByUsernameDao;
import com.postvue.feelogserver.domain.snsusers.vo.SignUpType;

import org.springframework.data.repository.query.Param;

public interface SnsUserRepository extends JpaRepository<SnsUser, Long>,JpaSpecificationExecutor<SnsUser> {

	Optional<SnsUser> findByUsername(String username);

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
			+ "(SELECT COUNT(*) FROM SnsUserFollow AS SNS_UF WHERE SNS_UF.followerUser.id = SNS_U.id) AS followingNum,"
			+ "(SELECT COUNT(*) FROM SnsUserFollow AS SNS_UF WHERE SNS_UF.followingUser.id = SNS_U.id) AS followerNum "
			+ "FROM SnsUser AS SNS_U "
			+ "LEFT OUTER JOIN SnsUserFollow AS SNS_UF "
			+ "ON SNS_U.id = SNS_UF.followingUser.id "
			+ "AND SNS_UF.followerUser.id = :myUserId "
			+ "LEFT OUTER JOIN SnsBlockUser AS SNS_BU "
			+ "ON SNS_BU.snsBlockerUser.id = :myUserId "
			+ "AND SNS_BU.snsBlockedUser.id = SNS_U.id "
			+ "WHERE SNS_U.username = :username")
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
			+ "AND (SNS_U.username LIKE CONCAT(:username, '%') OR SNS_U.nickname LIKE CONCAT(:username, '%') )"
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
			+ "ON SNS_BU.snsBlockerUser.id = :myUserId "
			+ "AND SNS_BU.snsBlockedUser.id = SNS_U.id "
			+ "LEFT JOIN SnsUserFollow SNS_UF "
			+ "ON SNS_UF.followingUser.id = SNS_U.id "
			+ "AND SNS_UF.followerUser.id = :myUserId "
			+ "WHERE SNS_U.id < :cursorId "
			+ "AND (SNS_U.username LIKE CONCAT(:username, '%') OR SNS_U.nickname LIKE CONCAT(:username, '%') )"
			+ "AND SNS_BU.snsBlockedUser IS NULL "
			+ "AND SNS_U.id != :myUserId "
			+ "ORDER BY SNS_U.id DESC")
	List<ProfileUserWithFollowByUsernameDao> findAllUserWithFollowByUsername(
		@Param("username") String username,
		@Param("myUserId") Long myUserId,
		@Param("cursorId") Long cursorId,
		Pageable pageable);

	@Query(value = "SELECT SNS_U FROM SnsUser SNS_U WHERE SNS_U.deletedAt < :daysAgo ORDER BY SNS_U.deletedAt DESC")
	List<SnsUser> findUserOlderThanDays(
		@Param("daysAgo") LocalDateTime daysAgo);
}
