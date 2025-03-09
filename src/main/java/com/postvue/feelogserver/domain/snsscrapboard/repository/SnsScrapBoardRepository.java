package com.postvue.feelogserver.domain.snsscrapboard.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.postvue.feelogserver.domain.snsscrap.SnsScrap;
import com.postvue.feelogserver.domain.snsscrapboard.SnsScrapBoard;
import com.postvue.feelogserver.domain.snsscrapboard.dao.ScrapBoardInfoDao;
import com.postvue.feelogserver.domain.snsscrapboard.dao.ScrapPreviewDao;

import org.springframework.data.repository.query.Param;

@Repository
public interface SnsScrapBoardRepository extends JpaRepository<SnsScrapBoard, Long>, JpaSpecificationExecutor<SnsScrapBoard> {
	Optional<SnsScrapBoard> findBySnsUser_IdAndId(Long snsUserId, Long scrapId);

	List<SnsScrapBoard> findBySnsUser_IdOrderByIdDesc(Long snsUserId, Pageable pageable);

	@Query(value = "SELECT "
		+ "SNS_SB.id AS scrapBoardId,"
		+ "SNS_SB.scrapName AS scrapBoardName, "
		+ "CASE "
		+ "WHEN EXISTS ( "
		+ "SELECT 1 "
		+ "FROM SnsScrap SNS_S "
		+ "WHERE  "
		+ "SNS_S.snsScrapBoard.id = SNS_SB.id AND SNS_SB.snsUser.id = :snsUserId AND SNS_S.snsPost.id = :snsPostId "
		+ ") THEN TRUE "
		+ "ELSE FALSE "
		+ "END AS isScraped, "
		+ "COALESCE((SELECT MAX(SNS_S.createdAt) FROM SnsScrap SNS_S WHERE SNS_S.snsScrapBoard = SNS_SB GROUP BY SNS_S.snsScrapBoard), SNS_SB.createdAt) AS recentlyPostedAt "
		+ "FROM SnsScrapBoard AS SNS_SB "
		+ "WHERE SNS_SB.snsUser.id = :snsUserId "
		+ "ORDER BY recentlyPostedAt DESC "
		)
	List<ScrapPreviewDao> findPreviewScrapBoardList(Long snsUserId, Long snsPostId, Pageable pageable);

	@Query(value =
		" SELECT "
		+ "SNS_SB.id AS scrapBoardId, "
		+ "SNS_SB.scrapName AS scrapBoardName, "
		+ "TRUE AS isScraped "
		+ "FROM SnsScrap SNS_S "
		+ "JOIN SnsScrapBoard SNS_SB ON SNS_S.snsScrapBoard.id = SNS_SB.id "
		+ "WHERE SNS_SB.snsUser.id = :snsUserId "
		+ "AND SNS_S.snsPost.id = :snsPostId "
		+ "ORDER BY SNS_S.createdAt DESC "
		+ "LIMIT 1 "
	)
	List<ScrapPreviewDao> findPreviewScrapBoardListByHasPost(Long snsUserId, Long snsPostId);




	@Query(value = "SELECT "
		+ "SNS_SB.scrapName AS scrapName, SNS_SB.id AS scrapBoardId, "
		+ "SNS_SB.targetAudience as targetAudience, "
		+ "(SELECT COUNT(SNS_S) FROM SnsScrap SNS_S WHERE SNS_S.snsScrapBoard = SNS_SB) AS scrapNum, "
		+ "CASE WHEN SNS_SB.snsUser.id = :myUserId THEN TRUE ELSE FALSE END AS isMe,"
		+ "SNS_SB.snsUser.id AS userId, SNS_SB.snsUser.username AS username, "
		+ "SNS_SB.snsUser.nickname AS nickname, SNS_SB.snsUser.profilePath AS profilePath,"
		+ "COALESCE((SELECT MAX(SNS_S.createdAt) FROM SnsScrap SNS_S WHERE SNS_S.snsScrapBoard = SNS_SB GROUP BY SNS_S.snsScrapBoard),SNS_SB.createdAt) AS recentlyPostedAt "
		+ "FROM SnsScrapBoard AS SNS_SB "
		+ "LEFT OUTER JOIN FETCH SnsBlockUser AS SNS_BU "
		+ "ON (SNS_BU.snsBlockerUser.id = :myUserId AND SNS_BU.snsBlockedUser.id = SNS_SB.snsUser.id) "
		+ "OR (SNS_BU.snsBlockerUser.id = SNS_SB.snsUser.id AND SNS_BU.snsBlockedUser.id = :myUserId) "
		+ "WHERE SNS_SB.id = :scrapBoardId "
		+ "AND SNS_BU.snsBlockerUser IS NULL "
		+ "AND ( "
		+ "SNS_SB.snsUser.id = :myUserId "
		+ "OR "
		+ "(SNS_SB.targetAudience = com.postvue.feelogserver.domain.snsscrapboard.vo.ScrapTargetAudience.PUBLIC_AUDIENCE) "
		+ "OR "
		+ "(SNS_SB.targetAudience = "
		+ "com.postvue.feelogserver.domain.snsscrapboard.vo.ScrapTargetAudience.PROTECTED_AUDIENCE "
		+ "AND :myUserId IN "
		+ "(SELECT SNS_UF.followerUser.id FROM SnsUserFollow AS SNS_UF WHERE SNS_SB.snsUser = SNS_UF.followingUser)) "
		+ ")")
	Optional<ScrapBoardInfoDao> findScrapInfoByMyUserId(
		@Param("scrapBoardId") Long scrapBoardId,
		@Param("myUserId") Long myUserId);



	@Query(value = "SELECT SNS_SB FROM SnsScrapBoard AS SNS_SB "
		+ "WHERE SNS_SB.snsUser.id = :userId "
		+ "AND SNS_SB.id IN (:snsScrapBoardId)")
	List<SnsScrapBoard> findBySnsUserAndSnsScrapBoardIn(
		@Param("userId") Long userId,
		@Param("snsScrapBoardId") List<Long> snsScrapBoardId);
}
