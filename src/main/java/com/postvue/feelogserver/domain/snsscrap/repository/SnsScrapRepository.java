package com.postvue.feelogserver.domain.snsscrap.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.postvue.feelogserver.domain.snspostuserreactions.dao.ProfilePostListDao;
import com.postvue.feelogserver.domain.snsscrap.SnsScrap;
import com.postvue.feelogserver.domain.snsscrap.repository.dao.MyScrapListDao;

import org.springframework.data.repository.query.Param;

@Repository
public interface SnsScrapRepository extends JpaRepository<SnsScrap, Long>, JpaSpecificationExecutor<SnsScrap> {

	String SHOW_BY_NOT_BLOCKED_USER_NATIVE_QUERY = "LEFT OUTER JOIN sns_block_users_tb AS SNS_BU "
		+ "ON SNS_BU.sns_blocker_user_id = :snsUserId AND SNS_BU.sns_blocked_user_id = sns_post.sns_user_id ";
	String MY_SCRAP_BOARD_NATIVE_QUERY = "WITH "
		+ "SCRAP_BOARD_OFFSET AS ( "
		+ "SELECT SNS_SB.sns_scrap_board_id, recently_posted_at, scrap_num "
		+ "FROM sns_scrap_boards_tb AS SNS_SB "
		+ "INNER JOIN "
		+ "(SELECT sns_scrap_board_id, MAX(created_at) as recently_posted_at "
		+ "FROM sns_scraps_tb AS SNS_S WHERE SNS_S.sns_user_id = :snsUserId GROUP BY sns_scrap_board_id) AS RECENTLY_SCRAP "
		+ "ON RECENTLY_SCRAP.sns_scrap_board_id = SNS_SB.sns_scrap_board_id "
		+ "INNER JOIN "
		+ "(SELECT SNS_S.sns_scrap_board_id, COUNT(SNS_S.sns_scrap_board_id) AS scrap_num FROM sns_scraps_tb AS SNS_S WHERE SNS_S.sns_user_id = :snsUserId GROUP BY SNS_S.sns_scrap_board_id) AS SCRAP_NUM_TB "
		+ "ON SCRAP_NUM_TB.sns_scrap_board_id = SNS_SB.sns_scrap_board_id "
		+ "ORDER BY RECENTLY_SCRAP.recently_posted_at DESC LIMIT :pageNum OFFSET :page) "
		+ "SELECT sns_scrap_board_id, scrap_name, recently_posted_at, scrap_num, sns_post_contents "
		+ "FROM ( "
		+ "SELECT SNS_SPL.sns_scrap_board_id, SNS_SPL.scrap_name, SCRAP_BOARD_OFFSET.recently_posted_at AS recently_posted_at, SCRAP_BOARD_OFFSET.scrap_num AS scrap_num, COALESCE(SNS_P.sns_post_contents,'[]') AS sns_post_contents, "
		+ "ROW_NUMBER() OVER (PARTITION BY SNS_SPL.sns_scrap_board_id ORDER BY SNS_SP.created_at ASC) AS asc_row_num, "
		+ "ROW_NUMBER() OVER (PARTITION BY SNS_SPL.sns_scrap_board_id ORDER BY SNS_SP.created_at DESC) AS desc_row_num "
		+ "FROM sns_scrap_boards_tb AS SNS_SPL "
		+ "INNER JOIN SCRAP_BOARD_OFFSET ON SNS_SPL.sns_scrap_board_id = SCRAP_BOARD_OFFSET.sns_scrap_board_id "
		+ "LEFT JOIN sns_scraps_tb AS SNS_SP ON SNS_SPL.sns_scrap_board_id = SNS_SP.sns_scrap_board_id "
		+ "LEFT JOIN sns_posts_tb AS SNS_P ON SNS_SP.sns_post_id = SNS_P.sns_post_id "
		+ "LEFT OUTER JOIN sns_block_users_tb AS SNS_BU "
		+ "ON SNS_BU.sns_blocker_user_id = :snsUserId AND SNS_BU.sns_blocked_user_id = SNS_P.sns_user_id "
		+ "WHERE SNS_SPL.sns_user_id = :snsUserId "
		+ "AND SNS_BU.sns_blocker_user_id IS  NULL "
		+ ")AS SUB "
		+ "WHERE (asc_row_num = 1 OR desc_row_num <= 5) ";

	@Query(value = MY_SCRAP_BOARD_NATIVE_QUERY, nativeQuery = true)
	List<MyScrapListDao> selectScrapBoard(Long snsUserId, Integer page, Integer pageNum);

	@Query(value = "SELECT SNS_SB.id AS cursorId, SNS_P.id AS postId, "
		+ "SNS_P.latitude AS latitude,SNS_P.longitude AS longitude, SNS_P.address AS address, "
		+ "SNS_P.snsUser.id AS userId, SNS_P.snsUser.username AS username, "
		+ "SNS_P.snsPostContents AS postContents, SNS_P.createdAt AS postedAt  "
		+ "FROM SnsScrapBoard AS SNS_SB "
		+ "INNER JOIN fetch SnsScrap AS SNS_SP ON SNS_SB = SNS_SP.snsScrapBoard "
		+ "INNER JOIN fetch SnsPost AS SNS_P ON SNS_SP.snsPost = SNS_P "
		+ "LEFT OUTER JOIN FETCH SnsBlockUser AS SNS_BU "
		+ "ON SNS_BU.snsBlockerUser.id = :snsUserId AND SNS_P.snsUser = SNS_BU.snsBlockedUser "
		+ "WHERE SNS_SB.snsUser.id = :snsUserId "
		+ "AND SNS_BU.snsBlockerUser IS NULL "
		+ "AND SNS_SB.id = :scrapId "
		+ "AND SNS_SB.id <:cursorId "
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
		+ "ORDER BY SNS_P.createdAt DESC")
	List<ProfilePostListDao> findScrapPostList(
		@Param("snsUserId") Long snsUserId,
		@Param("scrapId") Long scrapId,
		@Param("cursorId") Long cursorId,
		@Param("pageable") Pageable pageable);

	List<SnsScrap> findBySnsUser_IdAndSnsPost_Id(Long userId, Long postId);

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

}
