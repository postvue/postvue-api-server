package com.postvue.feelogserver.domain.snspostuserreactions.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.postvue.feelogserver.domain.snsposts.SnsPost;
import com.postvue.feelogserver.domain.snspostuserreactions.SnsPostUserReaction;
import com.postvue.feelogserver.domain.snspostuserreactions.dao.PostClipNumDao;
import com.postvue.feelogserver.domain.snspostuserreactions.dao.PostIsRepostedDao;
import com.postvue.feelogserver.domain.snspostuserreactions.dao.PostLikeDao;
import com.postvue.feelogserver.domain.snspostuserreactions.dao.PostLikeNumDao;
import com.postvue.feelogserver.domain.snspostuserreactions.dao.ProfilePostListDao;

import org.springframework.data.repository.query.Param;

@Repository
public interface SnsPostUserReactionRepository extends JpaRepository<SnsPostUserReaction, Long>,
	JpaSpecificationExecutor<SnsPostUserReaction> {
	@Query(value = "SELECT "
		+ "SNS_U.id AS userId , SNS_U.username AS username, SNS_U.nickname AS nickname, "
		+ "SNS_U.profilePath AS profilePath, SPUR.id AS cursorId, "
		+ "(CASE WHEN SNS_U_F.followingUser IS NOT NULL THEN TRUE ELSE FALSE END) AS isFollowed, (CASE WHEN SNS_U.id = :snsUserId THEN TRUE ELSE FALSE END) AS isMe "
		+ "FROM SnsPostUserReaction SPUR "
		+ "INNER JOIN SnsUser AS SNS_U "
		+ "ON SPUR.snsUser.id = SNS_U.id "
		+ "LEFT OUTER JOIN SnsUserFollow AS SNS_U_F ON SNS_U_F.followingUser = SPUR.snsUser AND SNS_U_F.followerUser.id = :snsUserId "
		+ "WHERE SPUR.snsPost.id = :snsPostId AND SPUR.isLiked = TRUE AND SPUR.id < :cursorId")
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

	void deleteBySnsPostAndSnsUser_Id(SnsPost snsPost, Long snsUserId);

	@Query(value =
		"SELECT "
			+ "SNS_PUR.id AS cursorId, SNS_P.id AS postId, SNS_P.latitude AS latitude, "
			+ "SNS_P.longitude AS longitude, SNS_P.address AS address, SNS_U.username AS username, SNS_U.id AS userId, "
			+ "SNS_P.snsPostContents AS postContents, SNS_P.createdAt AS postedAt "
			+ "FROM SnsPostUserReaction AS SNS_PUR "
			+ "INNER JOIN FETCH SnsPost AS SNS_P ON SNS_PUR.snsPost = SNS_P "
			+ "INNER JOIN FETCH SnsUser AS SNS_U ON SNS_PUR.snsUser = SNS_U "
			+ "LEFT OUTER JOIN SnsBlockUser AS SNS_BU ON SNS_BU.snsBlockerUser.id = :snsUserId AND SNS_PUR.snsPost.snsUser = SNS_BU.snsBlockedUser "
			+ "WHERE SNS_PUR.snsUser.id = :snsUserId AND SNS_PUR.isClipped = true AND SNS_PUR.id < :cursorId AND SNS_BU.snsBlockedUser IS NULL "
			+ "ORDER BY SNS_PUR.createdAt DESC ")
	List<ProfilePostListDao> findAllBySnsUser_snsUserIdAndIsClippedIsTrue(
		@Param("snsUserId") Long snsUserId,
		@Param("cursorId") Long cursorId,
		Pageable pageable);

}
