package com.postvue.feelogserver.domain.snsscrapboard.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.postvue.feelogserver.domain.snsscrapboard.SnsScrapBoard;
import com.postvue.feelogserver.domain.snsscrapboard.dao.ScrapBoardInfoDao;

import org.springframework.data.repository.query.Param;

@Repository
public interface SnsScrapBoardRepository extends JpaRepository<SnsScrapBoard, Long>, JpaSpecificationExecutor<SnsScrapBoard> {
	Optional<SnsScrapBoard> findBySnsUser_IdAndId(Long snsUserId, Long scrapId);

	List<SnsScrapBoard> findBySnsUser_IdOrderByIdDesc(Long snsUserId, Pageable pageable);

	@Query(value = "SELECT "
		+ "SNS_SB.scrapName AS scrapName, SNS_SB.id AS scrapBoardId, "
		+ "SNS_SB.targetAudience as targetAudience, "
		+ "(SELECT COUNT(SNS_S) FROM SnsScrap SNS_S WHERE SNS_S.snsScrapBoard = SNS_SB) AS scrapNum, "
		+ "CASE WHEN SNS_SB.snsUser.id = :myUserId THEN TRUE ELSE FALSE END AS isMe,"
		+ "SNS_SB.snsUser.id AS userId, SNS_SB.snsUser.username AS username, "
		+ "SNS_SB.snsUser.nickname AS nickname, SNS_SB.snsUser.profilePath AS profilePath,"
		+ "(SELECT MAX(SNS_S.createdAt) FROM SnsScrap SNS_S "
		+ "WHERE SNS_S.snsScrapBoard = SNS_SB GROUP BY SNS_S.snsScrapBoard) AS recentlyPostedAt "
		+ "FROM SnsScrapBoard AS SNS_SB WHERE SNS_SB.id = :scrapBoardId "
		+ "AND ( "
		+ "SNS_SB.snsUser.id = :myUserId "
		+ "OR "
		+ "(SNS_SB.targetAudience = "
		+ "com.postvue.feelogserver.domain.snsscrapboard.vo.ScrapTargetAudience.PUBLIC_AUDIENCE) "
		+ "OR "
		+ "(SNS_SB.targetAudience = "
		+ "com.postvue.feelogserver.domain.snsscrapboard.vo.ScrapTargetAudience.PROTECTED_AUDIENCE "
		+ "AND :myUserId IN "
		+ "(SELECT SNS_UF.followerUser.id FROM SnsUserFollow AS SNS_UF WHERE SNS_SB.snsUser = SNS_UF.followingUser)) "
		+ ")")
	Optional<ScrapBoardInfoDao> findScrapInfoByMyUserId(
		@Param("scrapBoardId") Long scrapBoardId,
		@Param("myUserId") Long myUserId);
}
