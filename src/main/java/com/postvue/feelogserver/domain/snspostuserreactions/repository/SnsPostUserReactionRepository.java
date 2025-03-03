package com.postvue.feelogserver.domain.snspostuserreactions.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.postvue.feelogserver.domain.snspostuserreactions.SnsPostUserReaction;
import com.postvue.feelogserver.domain.snspostuserreactions.dao.PostClipNumDao;
import com.postvue.feelogserver.domain.snspostuserreactions.dao.PostIsRepostedDao;
import com.postvue.feelogserver.domain.snspostuserreactions.dao.PostLikeDao;
import com.postvue.feelogserver.domain.snspostuserreactions.dao.PostLikeNumDao;

import org.springframework.data.repository.query.Param;

@Repository
public interface SnsPostUserReactionRepository extends JpaRepository<SnsPostUserReaction, Long>,
	JpaSpecificationExecutor<SnsPostUserReaction> {
	@Query(value = "SELECT "
		+ "SNS_U.id AS userId , SNS_U.username AS username, SNS_U.nickname AS nickname, "
		+ "SNS_U.profilePath AS profilePath, SPUR.id AS cursorId, "
		+ "(CASE WHEN SNS_U_F.followingUser IS NOT NULL THEN TRUE ELSE FALSE END) AS isFollowed, "
		+ "(CASE WHEN SNS_U.id = :snsUserId THEN TRUE ELSE FALSE END) AS isMe "
		+ "FROM SnsPostUserReaction SPUR "
		+ "INNER JOIN SnsUser AS SNS_U "
		+ "ON SPUR.snsUser.id = SNS_U.id "
		+ "LEFT OUTER JOIN SnsUserFollow AS SNS_U_F ON SNS_U_F.followingUser = SPUR.snsUser AND SNS_U_F.followerUser.id = :snsUserId "
		+ "LEFT OUTER JOIN SnsBlockUser AS SNS_BU "
		+ "ON (SNS_BU.snsBlockerUser.id = :snsUserId AND SNS_BU.snsBlockedUser.id = SPUR.snsUser.id) "
		+ "OR (SNS_BU.snsBlockerUser.id = SPUR.snsUser.id AND SNS_BU.snsBlockedUser.id = :snsUserId) "
		+ "WHERE "
		+ "SNS_BU.snsBlockedUser IS NULL "
		+ "AND SPUR.snsPost.id = :snsPostId "
		+ "AND SPUR.isLiked = TRUE AND SPUR.id < :cursorId ")
	List<PostLikeDao> selectLikeListByPost(
		@Param("snsPostId") Long snsPostId,
		@Param("cursorId") Long cursorId,
		@Param("snsUserId") Long snsUserId,
		Pageable pageable);

	@Query(value = "SELECT "
		+ "COUNT (SPUR) AS likeCount "
		+ "FROM SnsPostUserReaction SPUR WHERE SPUR.snsPost.id = :snsPostId "
		+ "AND SPUR.isLiked = TRUE "
		+ "AND SPUR.snsUser.id != :myUserId")
	Optional<PostLikeNumDao> findLikeNumWithoutMe(
		@Param("snsPostId") Long snsPostId,
		@Param("myUserId") Long myUserId);

	@Query(value = "SELECT "
		+ "COUNT (SPUR) AS clipCount "
		+ "FROM SnsPostUserReaction SPUR WHERE SPUR.snsPost.id = :snsPostId AND SPUR.isClipped = TRUE "
		+ "AND SPUR.snsUser.id != :myUserId")
	Optional<PostClipNumDao> findClipNumWithoutMe(
		@Param("snsPostId") Long snsPostId,
		@Param("myUserId") Long myUserId);

	@Query(value = "SELECT "
		+ "SNS_U.id AS userId , SNS_U.username AS username, SNS_U.nickname AS nickname, "
		+ "SNS_U.profilePath AS profilePath,  "
		+ "(CASE WHEN SNS_U_F.followingUser IS NOT NULL THEN TRUE ELSE FALSE END) AS isFollowed, (CASE WHEN SNS_U.id = :snsUserId THEN TRUE ELSE FALSE END) AS isMe "
		+ "FROM SnsPostUserReaction SPUR "
		+ "INNER JOIN SnsUser AS SNS_U "
		+ "ON SPUR.snsUser.id = SNS_U.id "
		+ "LEFT OUTER JOIN SnsUserFollow AS SNS_U_F ON SNS_U_F.followingUser = SPUR.snsUser AND SNS_U_F.followerUser.id = :snsUserId "
		+ "WHERE SPUR.snsPost.id = :snsPostId AND SPUR.isReposted = TRUE AND SPUR.id < :cursorId")
	List<PostIsRepostedDao> selectRepostListByPost(
		@Param("snsPostId") Long snsPostId,
		@Param("cursorId") Long cursorId,
		@Param("snsUserId") Long snsUserId,
		Pageable pageable);

	@Query("SELECT SPUR FROM SnsPostUserReaction SPUR WHERE SPUR.snsPost.id = :snsPostId AND SPUR.snsUser.id = :snsUserId ")
	Optional<SnsPostUserReaction> findBySnsPostAndSnsUser(
		@Param("snsPostId") Long snsPostId,
		@Param("snsUserId") Long snsUserId);

	@Query("SELECT SPUR FROM SnsPostUserReaction SPUR "
		+ "LEFT OUTER JOIN SnsBlockUser AS SNS_BU "
		+ "ON (SNS_BU.snsBlockerUser.id = :snsUserId AND SNS_BU.snsBlockedUser.id = SPUR.snsUser.id) "
		+ "OR (SNS_BU.snsBlockerUser.id = SPUR.snsUser.id AND SNS_BU.snsBlockedUser.id = :snsUserId) "
		+ "WHERE SPUR.snsPost.id = :snsPostId "
		+ "AND SPUR.snsUser.id = :snsUserId "
		+ "AND SNS_BU IS NULL")
	Optional<SnsPostUserReaction> findBySnsPostAndSnsUserByNotBlocked(
		@Param("snsPostId") Long snsPostId,
		@Param("snsUserId") Long snsUserId);



	@Query(value = "SELECT "
		+ "SNS_PUR.* "
		+ "FROM "
		+ "sns_post_user_reactions_tb AS SNS_PUR "
		+ "INNER JOIN sns_scraps_tb AS SNS_SCRAP ON SNS_SCRAP.sns_post_id = SNS_PUR.sns_post_id "
		+ "AND SNS_SCRAP.sns_user_id = :snsUserId "
		+ "INNER JOIN "
		+ "(SELECT "
		+ "SNS_S.sns_post_id AS postId, SNS_S.sns_user_id AS userId "
		+ "FROM "
		+ "sns_scraps_tb AS SNS_S "
		+ "WHERE "
		+ "SNS_S.sns_user_id = :snsUserId "
		+ "GROUP BY "
		+ "SNS_S.sns_post_id, SNS_S.sns_user_id "
		+ "HAVING "
		+ "COUNT(SNS_S.sns_post_id) = 1 ) GROUPED_S "
		+ "ON GROUPED_S.postId = SNS_PUR.sns_post_id "
		+ "AND GROUPED_S.userId = SNS_PUR.sns_user_id "
		+ "WHERE "
		+ "SNS_PUR.is_clipped = true "
		+ "AND SNS_SCRAP.sns_scrap_board_id = :snsScrapBoardId", nativeQuery = true)
	List<SnsPostUserReaction> findAllByDistinctScrapAndClipTrue(
		@Param("snsUserId") Long snsUserId,
		@Param("snsScrapBoardId") Long snsScrapBoardId
	);
}
