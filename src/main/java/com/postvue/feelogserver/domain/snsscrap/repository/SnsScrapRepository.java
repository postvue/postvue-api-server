package com.postvue.feelogserver.domain.snsscrap.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.postvue.feelogserver.domain.snspostuserreactions.dao.ProfilePostScrapDao;
import com.postvue.feelogserver.domain.snsscrap.SnsScrap;
import com.postvue.feelogserver.domain.snsscrap.dao.ScrapBoardByPostInfoDao;
import com.postvue.feelogserver.domain.snsscrap.repository.dao.ScrapThumbNailDao;
import com.postvue.feelogserver.domain.snsscrapboard.vo.ScrapTargetAudienceValue;

import org.springframework.data.repository.query.Param;

@Repository
public interface SnsScrapRepository extends JpaRepository<SnsScrap, Long>, JpaSpecificationExecutor<SnsScrap> {

	String SHOW_BY_NOT_BLOCKED_USER_NATIVE_QUERY = "LEFT OUTER JOIN sns_block_users_tb AS SNS_BU "
		+ "ON SNS_BU.sns_blocker_user_id = :snsUserId AND SNS_BU.sns_blocked_user_id = sns_post.sns_user_id ";
	String SCRAP_BOARD_NATIVE_QUERY = "WITH "
		+ "SCRAP_BOARD_OFFSET AS (  "
		+ "SELECT SNS_SB.sns_scrap_board_id, "
		+ "CASE WHEN recently_posted_at IS NOT NULL THEN recently_posted_at ELSE SNS_SB.created_at END as recently_posted_at, "
		+ "CASE WHEN scrap_num IS NOT NULL THEN scrap_num ELSE 0 END as scrap_num "
		+ "FROM sns_scrap_boards_tb AS SNS_SB  "
		+ "LEFT JOIN  "
		+ "(SELECT sns_scrap_board_id, MAX(created_at) as recently_posted_at  "
		+ "FROM sns_scraps_tb AS SNS_S GROUP BY sns_scrap_board_id) AS RECENTLY_SCRAP  "
		+ "ON RECENTLY_SCRAP.sns_scrap_board_id = SNS_SB.sns_scrap_board_id  "
		+ "LEFT JOIN  "
		+ "(SELECT SNS_S.sns_scrap_board_id, COUNT(SNS_S.sns_scrap_board_id) AS scrap_num FROM sns_scraps_tb AS SNS_S WHERE SNS_S.sns_user_id = :snsUserId GROUP BY SNS_S.sns_scrap_board_id) AS SCRAP_NUM_TB  "
		+ "ON SCRAP_NUM_TB.sns_scrap_board_id = SNS_SB.sns_scrap_board_id "
		+ "WHERE SNS_SB.sns_user_id = :snsUserId AND SNS_SB.deleted_at IS NULL "
		+ "ORDER BY RECENTLY_SCRAP.recently_posted_at DESC LIMIT :pageNum OFFSET :page) "
		+ "SELECT sns_scrap_board_id, scrap_name, recently_posted_at, scrap_num, sns_post_contents, "
		+ "TRUE AS is_me "
		+ "FROM ( "
		+ "SELECT SNS_SPL.sns_scrap_board_id, SNS_SPL.scrap_name, SCRAP_BOARD_OFFSET.recently_posted_at AS recently_posted_at, SCRAP_BOARD_OFFSET.scrap_num AS scrap_num, COALESCE(SNS_P.sns_post_contents,'[]') AS sns_post_contents, "
		+ "ROW_NUMBER() OVER (PARTITION BY SNS_SPL.sns_scrap_board_id ORDER BY SNS_SP.created_at ASC) AS asc_row_num, "
		+ "ROW_NUMBER() OVER (PARTITION BY SNS_SPL.sns_scrap_board_id ORDER BY SNS_SP.created_at DESC) AS desc_row_num "
		+ "FROM sns_scrap_boards_tb AS SNS_SPL "
		+ "INNER JOIN SCRAP_BOARD_OFFSET ON SNS_SPL.sns_scrap_board_id = SCRAP_BOARD_OFFSET.sns_scrap_board_id "
		+ "LEFT JOIN sns_scraps_tb AS SNS_SP ON SNS_SPL.sns_scrap_board_id = SNS_SP.sns_scrap_board_id "
		+ "LEFT JOIN sns_posts_tb AS SNS_P ON SNS_SP.sns_post_id = SNS_P.sns_post_id AND SNS_P.deleted_at IS NULL "
		+ "LEFT OUTER JOIN sns_users_tb AS SNS_U ON SNS_U.sns_user_id = SNS_P.sns_user_id "
		+ "LEFT OUTER JOIN sns_block_users_tb AS SNS_BU "
		+ "ON SNS_BU.sns_blocker_user_id = :snsUserId AND SNS_BU.sns_blocked_user_id = SNS_P.sns_user_id "
		+ "WHERE SNS_SPL.sns_user_id = :snsUserId "
		+ "AND SNS_BU.sns_blocker_user_id IS NULL "
		+ "AND (SNS_U IS NULL OR SNS_U.deleted_at IS NULL) "
		+ ")AS SUB "
		+ "WHERE (asc_row_num = 1 OR desc_row_num <= 5) ";

	String SEARCH_SCRAP_BOARD_NATIVE_QUERY = "WITH  "
		+ "SCRAP_BOARD_OFFSET AS ( "
		+ "SELECT SNS_SB.sns_scrap_board_id, recently_posted_at, scrap_num "
		+ "FROM sns_scrap_boards_tb AS SNS_SB  "
		+ "INNER JOIN "
		+ "(SELECT sns_scrap_board_id, MAX(created_at) as recently_posted_at  "
		+ "FROM sns_scraps_tb AS SNS_S GROUP BY sns_scrap_board_id) AS RECENTLY_SCRAP  "
		+ "ON RECENTLY_SCRAP.sns_scrap_board_id = SNS_SB.sns_scrap_board_id  "
		+ "INNER JOIN "
		+ "(SELECT SNS_S.sns_scrap_board_id, "
		+ "COUNT(SNS_S.sns_scrap_board_id) AS scrap_num FROM sns_scraps_tb AS SNS_S "
		+ "GROUP BY SNS_S.sns_scrap_board_id) AS SCRAP_NUM_TB "
		+ "ON SCRAP_NUM_TB.sns_scrap_board_id = SNS_SB.sns_scrap_board_id "
		+ "LEFT OUTER JOIN sns_block_users_tb AS SNS_BU "
		+ "ON SNS_BU.sns_blocker_user_id = :snsUserId AND SNS_BU.sns_blocked_user_id = SNS_SB.sns_user_id  "
		+ "OR SNS_BU.sns_blocker_user_id = SNS_SB.sns_user_id AND SNS_BU.sns_blocked_user_id = :snsUserId  "
		+ "WHERE "
		+ "SNS_SB.deleted_at IS NULL "
		+ "AND SNS_BU.sns_blocker_user_id IS  NULL "
		+ "AND"
		+ "(SNS_SB.sns_user_id = :snsUserId "
		+ "OR (SNS_SB.target_audience = " + "'" + ScrapTargetAudienceValue.PUBLIC_AUDIENCE_VALUE + "'" + " ) "
		+ "OR (SNS_SB.target_audience = " + "'" + ScrapTargetAudienceValue.PROTECTED_AUDIENCE_VALUE + "'" + " AND :snsUserId IN (SELECT SNS_UF.follower_id FROM sns_user_follows_tb AS SNS_UF WHERE SNS_SB.sns_user_id = SNS_UF.following_id) )"
		+ ")"
		+ "ORDER BY RECENTLY_SCRAP.recently_posted_at DESC LIMIT :pageNum OFFSET :page) "
		+ "SELECT sns_scrap_board_id, scrap_name, recently_posted_at, scrap_num, sns_post_contents, is_me "
		+ "FROM ( "
		+ "SELECT SNS_SPL.sns_scrap_board_id, "
		+ "CASE WHEN SNS_SPL.sns_user_id = :snsUserId THEN TRUE ELSE FALSE END as is_me, "
		+ "SNS_SPL.scrap_name, SCRAP_BOARD_OFFSET.recently_posted_at AS recently_posted_at, SCRAP_BOARD_OFFSET.scrap_num AS scrap_num, COALESCE(SNS_P.sns_post_contents,'[]') AS sns_post_contents,  "
		+ "ROW_NUMBER() OVER (PARTITION BY SNS_SPL.sns_scrap_board_id ORDER BY SNS_SP.created_at ASC) AS asc_row_num,  "
		+ "ROW_NUMBER() OVER (PARTITION BY SNS_SPL.sns_scrap_board_id ORDER BY SNS_SP.created_at DESC) AS desc_row_num  "
		+ "FROM sns_scrap_boards_tb AS SNS_SPL  "
		+ "INNER JOIN SCRAP_BOARD_OFFSET ON SNS_SPL.sns_scrap_board_id = SCRAP_BOARD_OFFSET.sns_scrap_board_id  "
		+ "LEFT JOIN sns_scraps_tb AS SNS_SP ON SNS_SPL.sns_scrap_board_id = SNS_SP.sns_scrap_board_id  "
		+ "LEFT JOIN sns_posts_tb AS SNS_P ON SNS_SP.sns_post_id = SNS_P.sns_post_id "
		+ "INNER JOIN sns_users_tb AS SNS_U ON SNS_P.sns_user_id = SNS_U.sns_user_id "
		+ "LEFT OUTER JOIN sns_block_users_tb AS SNS_BU "
		+ "ON SNS_BU.sns_blocker_user_id = :snsUserId AND SNS_BU.sns_blocked_user_id = SNS_P.sns_user_id  "
		+ "OR SNS_BU.sns_blocker_user_id = SNS_P.sns_user_id AND SNS_BU.sns_blocked_user_id = :snsUserId  "
		+ "WHERE SNS_SPL.scrap_name @@ :searchQuery  "
		+ "AND SNS_BU.sns_blocker_user_id IS  NULL  "
		+ "AND SNS_P.deleted_at IS NULL "
		+ "AND SNS_U.deleted_at IS NULL "
		+ ")AS SUB  "
		+ "WHERE "
		+ "(asc_row_num = 1 OR desc_row_num <= 5)";

	@Query(value = SCRAP_BOARD_NATIVE_QUERY, nativeQuery = true)
	List<ScrapThumbNailDao> selectScrapBoard(
		@Param("snsUserId") Long snsUserId,
		@Param("page") Integer page,
		@Param("pageNum") Integer pageNum);

	@Query(value = SEARCH_SCRAP_BOARD_NATIVE_QUERY, nativeQuery = true)
	List<ScrapThumbNailDao> selectScrapBoardBySearchQuery(
		@Param("snsUserId") Long snsUserId,
		@Param("searchQuery") String searchQuery,
		@Param("page") Integer page,
		@Param("pageNum") Integer pageNum);


	@Query(value = "SELECT "
		+ "SNS_SP.id AS cursorId, "
		+ "SNS_P.id AS postId, "
		+ "SNS_P.snsUser.profilePath as profilePath, "
		+ "(CASE WHEN SNS_P.snsUser.id = :snsUserId THEN FALSE ELSE TRUE END) as followable, "
		+ "COALESCE(SPUR.isLiked,false) AS isLiked, COALESCE(SPUR.isReposted,false) AS isReposted, "
		+ "COALESCE(SPUR.isClipped,false) AS isClipped, "
		+ "SNS_P.latitude AS latitude, SNS_P.longitude AS longitude, SNS_P.address AS address, SNS_P.buildName AS buildName, "
		+ "SNS_P.snsUser.id AS userId, SNS_P.snsUser.username AS username, "
		+ "SNS_P.postTitle AS postTitle, SNS_P.postBodyText AS postBodyText, "
		+ "SNS_P.snsPostContents AS snsPostContents, "
		+ "SNS_P.tags as tags, SNS_P.tgtAudType as tgtAudType, "
		+ "SNS_P.snsUser.id as snsUserId, SNS_UF.followingUser.id as followingId, "
		+ "(CASE WHEN SNS_BU.snsBlockerUser IS NOT NULL THEN TRUE ELSE FALSE END ) AS isBlocked, "
		+ "SNS_P.createdAt AS postedAt "
		+ "FROM SnsScrapBoard AS SNS_SB "
		+ "INNER JOIN fetch SnsScrap AS SNS_SP ON SNS_SB = SNS_SP.snsScrapBoard "
		+ "INNER JOIN fetch SnsPost AS SNS_P ON SNS_SP.snsPost = SNS_P "
		+ "LEFT OUTER JOIN SnsPostUserReaction AS SPUR ON SPUR.snsUser.id = :snsUserId AND SNS_P.id = SPUR.snsPost.id "
		+ "LEFT OUTER JOIN SnsUserFollow AS SNS_UF ON SNS_UF.followerUser.id = :snsUserId AND SNS_UF.followingUser = SNS_P.snsUser "
		+ "LEFT OUTER JOIN FETCH SnsBlockUser AS SNS_BU "
		+ "ON (SNS_BU.snsBlockerUser.id = :snsUserId AND SNS_BU.snsBlockedUser.id = SNS_P.snsUser.id) "
		+ "OR (SNS_BU.snsBlockerUser.id = SNS_P.snsUser.id AND SNS_BU.snsBlockedUser.id = :snsUserId) "
		+ "WHERE "
		+ "SNS_BU.snsBlockerUser IS NULL "
		+ "AND SNS_P.deletedAt IS NULL "
		+ "AND SNS_P.snsUser.deletedAt IS NULL "
		+ "AND SNS_SB.id = :scrapId "
		+ "AND SNS_SP.id < :cursorId "
		+ "AND ( "
		+ "SNS_SB.snsUser.id = :snsUserId "
		+ "OR "
		+ "(SNS_SB.targetAudience = "
		+ "com.postvue.feelogserver.domain.snsscrapboard.vo.ScrapTargetAudience.PUBLIC_AUDIENCE) "
		+ "OR "
		+ "(SNS_SB.targetAudience = "
		+ "com.postvue.feelogserver.domain.snsscrapboard.vo.ScrapTargetAudience.PROTECTED_AUDIENCE "
		+ "AND :snsUserId IN "
		+ "(SELECT SNS_UF.followerUser.id FROM SnsUserFollow AS SNS_UF WHERE SNS_SB.snsUser = SNS_UF.followingUser)) "
		+ ") "
		+ "ORDER BY SNS_SP.createdAt DESC")
	List<ProfilePostScrapDao> findScrapPostList(
		@Param("snsUserId") Long snsUserId,
		@Param("scrapId") Long scrapId,
		@Param("cursorId") Long cursorId,
		@Param("pageable") Pageable pageable);

	List<SnsScrap> findBySnsUser_IdAndSnsPost_Id(Long userId, Long postId);

	List<SnsScrap> findBySnsUser_IdAndSnsPost_IdOrderByCreatedAtDesc(Long userId, Long postId);

	Long countBySnsUser_idAndSnsScrapBoard_id(Long userId, Long scrapBoardId);

	Optional<SnsScrap> findBySnsUser_IdAndSnsPost_IdAndSnsScrapBoard_Id(Long userId,
		Long postId,
		Long snsScrapBoardId);

	@Query(value = "SELECT SNS_S FROM SnsScrap AS SNS_S "
		+ "WHERE SNS_S.snsUser.id = :userId "
		+ "AND SNS_S.snsPost.id = :postId AND SNS_S.snsScrapBoard.id IN (:snsScrapBoardId)")
	List<SnsScrap> findBySnsUserAndSnsPostAndSnsScrapBoardIn(
		@Param("userId") Long userId,
		@Param("postId") Long postId,
		@Param("snsScrapBoardId") List<Long> snsScrapBoardId);

	@Query(value = "SELECT SNS_S FROM SnsScrap AS SNS_S "
		+ "WHERE SNS_S.snsUser.id = :userId "
		+ "AND SNS_S.snsPost.id = :postId")
	List<SnsScrap> findBySnsUserAndSnsPost(
		@Param("userId") Long userId,
		@Param("postId") Long postId);


	@Query(value = "SELECT SNS_S.snsScrapBoard.id as scrapId, SNS_S.snsScrapBoard.scrapName as scrapName FROM SnsScrap AS SNS_S "
		+ "WHERE SNS_S.snsPost.id = :snsPostId AND SNS_S.snsUser.id = :snsUserId")
	List<ScrapBoardByPostInfoDao> findScrapBoardIdByMyUserIdAndPostId(
		@Param("snsUserId") Long snsUserId,
		@Param("snsPostId") Long snsPostId
	);

	@Modifying
	@Query(value = "DELETE FROM SnsScrap WHERE id IN (:snsScrapIdList)")
	void deleteAllByScrapId(@Param("snsScrapIdList") List<Long> snsScrapIdList);

	@Modifying
	@Query("DELETE FROM SnsScrap s WHERE s.snsPost.id = :snsPostId")
	void deleteAllByPostId(@Param("snsPostId") Long snsPostId);
}
