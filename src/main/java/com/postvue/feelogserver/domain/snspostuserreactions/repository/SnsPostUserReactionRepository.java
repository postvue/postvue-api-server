package com.postvue.feelogserver.domain.snspostuserreactions.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Repository;

import com.postvue.feelogserver.domain.snsposts.SnsPost;
import com.postvue.feelogserver.domain.snsposts.dto.SnsPostDao;
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


	String MY_CLIP_QUERY =
		"select "
		+ "        SNS_PUR.sns_post_user_reaction_id AS cursorId, "
		+ "        SNS_P.sns_post_id AS postId, "
		+ "        SNS_P.is_repost AS isReposted, "
		+ "        FALSE AS followable, "
		+ "        :snsUserId AS followingId, "
		+ "        SNS_P.latitude AS latitude, "
		+ "        SNS_P.longitude AS longitude, "
		+ "        SNS_P.address AS address, "
		+ "        SNS_U.username AS username, "
		+ "        SNS_U.profile_path AS profilePath, "
		+ "        SNS_PUR.is_liked AS isLiked, "
		+ "        SNS_PUR.is_clipped AS isClipped, "
		+ "        SNS_U.sns_user_id AS snsUserId, "
		+ "        SNS_P.post_title AS postTitle, "
		+ "        SNS_P.post_body_text AS postBodyTest, "
		+ "        SNS_P.sns_post_contents AS snsPostContents, "
		+ "        SNS_P.tags AS tags, "
		+ "        SNS_P.created_at AS postedAt "
		+ "    from "
		+ "        sns_post_user_reactions_tb SNS_PUR  "
		+ "    join "
		+ "        sns_posts_tb SNS_P  "
		+ "            on SNS_PUR.sns_post_id=SNS_P.sns_post_id  "
		+ "    join "
		+ "        sns_users_tb SNS_U  "
		+ "            on SNS_PUR.sns_user_id=SNS_U.sns_user_id  "
		+ "    where "
		+ "        SNS_PUR.sns_user_id=:snsUserId  "
		+ "        and SNS_PUR.is_clipped=true  "
		+ "        and SNS_PUR.sns_post_user_reaction_id<:cursorId  "
		+ "        AND SNS_P.deleted_at IS NULL "
		+ "    order by "
		+ "        SNS_PUR.created_at desc  "
		+ "    offset "
		+ "        0 rows  "
		+ "    fetch "
		+ "        first :pageSize rows only";
	@Query(value = MY_CLIP_QUERY, nativeQuery = true)
	List<SnsPostDao> findAllMyClipList(
		@Param("snsUserId") Long snsUserId,
		@Param("cursorId") Long cursorId,
		@Param("pageSize") Integer pageSize
		);

	@Query("SELECT SNS_PUR FROM SnsPostUserReaction SNS_PUR "
		+ "INNER JOIN SnsScrap SNS_S ON SNS_S.snsPost = SNS_PUR.snsPost AND SNS_S.snsUser.id = :snsUserId "
		+ "WHERE SNS_PUR.isClipped = true AND SNS_S.snsScrapBoard.id = :snsScrapBoardId ")
	List<SnsPostUserReaction> findAllByScrapAndClipTrue(
		@Param("snsUserId") Long snsUserId,
		@Param("snsScrapBoardId") Long snsScrapBoardId
	);
}
