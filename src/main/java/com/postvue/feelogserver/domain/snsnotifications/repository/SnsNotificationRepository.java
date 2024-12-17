package com.postvue.feelogserver.domain.snsnotifications.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.postvue.feelogserver.domain.snsnotifications.SnsNotification;
import com.postvue.feelogserver.domain.snsnotifications.vo.SnsNotificationType;

import org.springframework.data.repository.query.Param;

@Repository
public interface SnsNotificationRepository extends JpaRepository<SnsNotification, Long>, JpaSpecificationExecutor<SnsNotification> {

	@Query(value = "SELECT SNS_S FROM SnsNotification SNS_S WHERE SNS_S.snsUser.id = :snsUserId AND SNS_S.createdAt > :notifiedDateTime ORDER BY SNS_S.createdAt DESC")
	List<SnsNotification> findNotificationByIdAndAfterNotifiedAt(
		@Param("snsUserId") Long snsUserId,
		@Param("notifiedDateTime") LocalDateTime notifiedDateTime);

	@Query(value = "SELECT SNS_S FROM SnsNotification SNS_S WHERE SNS_S.createdAt < :daysAgo ORDER BY SNS_S.createdAt DESC")
	List<SnsNotification> findNotificationsOlderThanDays(
		@Param("daysAgo") LocalDateTime daysAgo);

	@Query("SELECT SNS_N FROM SnsNotification SNS_N WHERE "
		+ "SNS_N.snsUser.id = :userId "
		+ "AND SNS_N.snsPost.id = :snsPostId "
		+ "AND SNS_N.snsNotificationType = :snsNotificationType "
		+ "AND SNS_N.notificationCount = :notificationCount")
	Optional<SnsNotification> findNotificationByPost(
		@Param("userId") Long userId,
		@Param("snsPostId") Long snsPostId,
		@Param("snsNotificationType") SnsNotificationType snsNotificationType,
		@Param("notificationCount") Integer notificationCount);

	@Query("SELECT SNS_N FROM SnsNotification SNS_N WHERE "
		+ "SNS_N.snsUser.id = :userId "
		+ "AND SNS_N.followerUser.id = :followerId "
		+ "AND SNS_N.snsNotificationType = com.postvue.feelogserver.domain.snsnotifications.vo.SnsNotificationType.USER_FOLLOWER_NOTIFICATION")
	Optional<SnsNotification> findNotificationByFollower(
		@Param("userId") Long userId,
		@Param("followerId") Long followerId);
}
