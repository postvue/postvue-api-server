package com.postvue.feelogserver.domain.snspostcommentreactions.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.postvue.feelogserver.domain.snspostcommentreactions.SnsPostCommentReaction;
import com.postvue.feelogserver.domain.snspostcommentreactions.dao.PostCommentDao;
import com.postvue.feelogserver.domain.snspostcommentreactions.dao.PostCommentNumDao;
import com.postvue.feelogserver.domain.snspostcommentreactions.dao.PostReplyNumDao;

import org.springframework.data.repository.query.Param;

@Repository
public interface SnsPostCommentReactionRepository extends JpaRepository<SnsPostCommentReaction, Long>,
	JpaSpecificationExecutor<SnsPostCommentReaction> {

	// @Query(value = "WITH "
	// 	+ "LIKE_COUNT_TB AS (SELECT SUM(CASE WHEN COMMNET_LIKE.isLiked = TRUE THEN 1 ELSE 0 END) AS likeCount "
	// 	+ "FROM SnsPostCommentLike AS COMMNET_LIKE )"
	// 	+ "SELECT "
	// 	+ "SPCR.snsPostCommentReactionId AS postCommentId, SPCR.postCommentType AS postCommentType, "
	// 	+ "SPCR.postCommentContent AS postCommentContent, COALESCE(IS_LIKE_TB.isLiked,FALSE) AS isLiked, SPCR.createdAt AS postedAt,  "
	// 	+ "SUM(CASE WHEN COMMNET_LIKE.isLiked = TRUE THEN 1 ELSE 0 END) AS likeCount, "
	// 	+ "SUM(CASE WHEN SOURCE_COMMENT.isCommented = TRUE THEN 1 ELSE 0 END) AS commentCount, "
	// 	+ "COMMENT_USER.profilePath AS profilePath, COMMENT_USER.snsUserId AS commentUserId, COMMENT_USER.username AS username, SPCR.sourceComment.snsPostCommentReactionId AS commentSourceId, COALESCE(SPCR.isSource,FALSE) AS isSource  "
	// 	+ "FROM SnsPostCommentReaction AS SPCR "
	// 	+ "INNER JOIN SPCR.commentUser AS COMMENT_USER "
	// 	+ " "
	// 	+ "LEFT OUTER JOIN SnsPostCommentLike AS COMMNET_LIKE "
	// 	+ "ON SPCR.snsPostCommentReactionId = COMMNET_LIKE.snsPostCommentReaction.snsPostCommentReactionId "
	// 	+ " "
	// 	+ "LEFT OUTER JOIN SnsPostCommentReaction AS SOURCE_COMMENT "
	// 	+ "ON SPCR.snsPostCommentReactionId = SOURCE_COMMENT.sourceComment.snsPostCommentReactionId "
	// 	+ " "
	// 	+ "LEFT OUTER JOIN SnsPostCommentLike AS IS_LIKE_TB "
	// 	+ "ON SPCR.snsPostCommentReactionId = IS_LIKE_TB.snsPostCommentReaction.snsPostCommentReactionId AND IS_LIKE_TB.snsUser.snsUserId = :snsUserId AND IS_LIKE_TB.isLiked = TRUE "
	// 	+ " "
	// 	+ "WHERE SPCR.snsPost.snsPostId = :snsPostId AND SPCR.isSource = TRUE AND SPCR.isCommented = TRUE "
	// 	+ "AND SPCR.snsPostCommentReactionId < :snsPostCommentReactionId "
	// 	+ "GROUP BY SPCR.snsPostCommentReactionId, SPCR.postCommentType, SPCR.postCommentContent, IS_LIKE_TB.isLiked, "
	// 	+ "SPCR.createdAt, COMMENT_USER.profilePath, COMMENT_USER.snsUserId "
	// 	+ "ORDER BY SPCR.snsPostCommentReactionId ASC", nativeQuery = true
	// )
	// List<PostCommentDao> selectCommentByPostIdTest(
	// 	@Param("snsPostId") Long snsPostId,
	// 	@Param("snsUserId") Long snsUserId,
	// 	@Param("snsPostCommentReactionId") Long snsPostCommentReactionId);

	Optional<SnsPostCommentReaction> findByIdAndDeletedAtIsNull(Long id);

	@Query("SELECT "
		+ "SPCR.id AS postCommentId, "
		+ "SPCR.commentMediaType AS postCommentMediaType, "
		+ "SPCR.commentMediaContent AS postCommentMediaContent, "
		+ "SPCR.commentMsg AS commentMsg, "
		+ "COALESCE(IS_LIKE_TB.isLiked,FALSE) AS isLiked, "
		+ "SPCR.createdAt AS postedAt,  "
		+ "(SELECT COUNT(*) FROM SnsPostCommentLike CL WHERE CL.snsPostCommentReaction.id = SPCR.id AND CL.isLiked = TRUE) AS likeCount, "
		+ "(SELECT COUNT(*) FROM SnsPostCommentReaction CR WHERE CR.sourceComment.id = SPCR.id) AS commentCount, "
		+ "SPCR.commentUser.profilePath AS profilePath, SPCR.commentUser.id AS commentUserId, SPCR.commentUser.username AS username, SPCR.sourceComment.id AS commentSourceId, COALESCE(SPCR.isSource,FALSE) AS isSource "
		+ "FROM SnsPostCommentReaction AS SPCR "
		+ "LEFT OUTER JOIN SnsPostCommentLike AS IS_LIKE_TB "
		+ "ON SPCR.id = IS_LIKE_TB.snsPostCommentReaction.id AND IS_LIKE_TB.snsUser.id = :snsUserId AND IS_LIKE_TB.isLiked = TRUE "
		+ "WHERE SPCR.snsPost.id = :snsPostId AND SPCR.isSource = TRUE "
		+ "AND SPCR.id < :snsPostCommentReactionId "
		+ "AND SPCR.deletedAt IS NULL "
		+ "ORDER BY SPCR.createdAt DESC"
		// + "GROUP BY "
		// + "SPCR.id, "
		// + "SPCR.commentMediaType, "
		// + "SPCR.commentMediaContent,"
		// + "SPCR.commentMsg, "
		// + "IS_LIKE_TB.isLiked, "
		// + "SPCR.createdAt, "
		// + "SPCR.commentUser.profilePath, "
		// + "SPCR.commentUser.id, "
		// + "SPCR.sourceComment.id, "
		// + "SPCR.commentUser.username "
		// + "ORDER BY SPCR.id DESC"
	)
	List<PostCommentDao> selectCommentByPostId(
		@Param("snsPostId") Long snsPostId,
		@Param("snsUserId") Long snsUserId,
		@Param("snsPostCommentReactionId") Long snsPostCommentReactionId,
		Pageable pageable);

	// @Query(value = "WITH "
	// 	+ "POST_COMMENT AS ("
	// 	+ "SELECT SPCR.sns_post_comment_reaction_id AS postCommentId, "
	// 	+ "SPCR.comment_media_type AS postCommentMediaType, "
	// 	+ "SPCR.comment_media_content AS postCommentMediaContent, "
	// 	+ "SPCR.comment_msg AS commentMsg, "
	// 	+ "SPCR.created_at AS postedAt, "
	// 	+ "SUM(CASE WHEN SOURCE_COMMENT.is_liked = TRUE THEN 1 ELSE 0 END) AS likeCount, "
	// 	+ "SUM(CASE WHEN SOURCE_COMMENT.is_commented = TRUE THEN 1 ELSE 0 END) AS commentCount, "
	// 	+ "COMMENT_USER.profile_path AS profilePath, COMMENT_USER.sns_user_id AS commentUserId, COMMENT_USER.username AS username, SPCR.sourceComment.snsPostCommentReactionId AS commentSourceId, COALESCE(SPCR.isSource,FALSE) AS isSource  "
	// 	+ "FROM sns_post_comment_reactions_tb AS SPCR "
	// 	+ " "
	// 	+ "INNER JOIN sns_users_tb AS COMMENT_USER ON COMMENT_USER.sns_user_id = SPCR.comment_user_id "
	// 	+ " "
	// 	+ "LEFT OUTER JOIN sns_post_comment_reactions_tb AS SOURCE_COMMENT "
	// 	+ "ON SPCR.sns_post_comment_reaction_Id = SOURCE_COMMENT.source_comment_id "
	// 	+ " "
	// 	+ "WHERE SPCR.sns_post_id = :snsPostId AND SPCR.is_source = TRUE AND SPCR.is_commented = TRUE "
	// 	+ "AND SPCR.sns_post_comment_reaction_id < :snsPostCommentReactionId "
	// 	+ "GROUP BY "
	// 	+ "SPCR.sns_post_comment_reaction_id, "
	// 	+ "SPCR.comment_media_type, SPCR.comment_media_content, SPCR.comment_msg, "
	// 	+ "SPCR.created_at, COMMENT_USER.profile_path, COMMENT_USER.sns_user_id LIMIT :pageSize) "
	// 	+ "SELECT POST_COMMENT.*, COALESCE(SPCR.is_liked,false) AS isLiked  FROM POST_COMMENT LEFT OUTER JOIN sns_post_comment_reactions_tb AS SPCR ON POST_COMMENT.postCommentId = SPCR.source_comment_id AND SPCR.comment_user_id = :snsUserId "
	// 	+ "ORDER BY POST_COMMENT.postCommentId ASC", nativeQuery = true
	// )
	// List<PostCommentDao> selectCommentByPostId(
	// 	@Param("snsUserId") Long snsUserId,
	// 	@Param("snsPostId") Long snsPostId,
	// 	@Param("snsPostCommentReactionId") Long snsPostCommentReactionId,
	// 	@Param("pageSize") Integer pageSize);

	@Query("SELECT "
		+ "SPCR.id AS postCommentId, "
		+ "SPCR.commentMediaType AS postCommentMediaType, "
		+ "SPCR.commentMediaContent AS postCommentMediaContent, "
		+ "SPCR.commentMsg AS commentMsg, "
		+ "COALESCE(IS_LIKE_TB.isLiked,FALSE) AS isLiked, SPCR.createdAt AS postedAt,  "
		+ "(SELECT COUNT(*) FROM SnsPostCommentLike CL WHERE CL.snsPostCommentReaction.id = SPCR.id AND CL.isLiked = TRUE) AS likeCount, "
		+ "(SELECT COUNT(*) FROM SnsPostCommentReaction CR WHERE CR.sourceComment.id = SPCR.id) AS commentCount, "
		+ "SPCR.commentUser.profilePath AS profilePath, SPCR.commentUser.id AS commentUserId, SPCR.commentUser.username AS username, "
		+ "SPCR.sourceComment.id AS commentSourceId, COALESCE(SPCR.isSource,FALSE) AS isSource "
		+ "FROM SnsPostCommentReaction AS SPCR "
		+ " "
		+ "LEFT OUTER JOIN SnsPostCommentReaction AS SOURCE_COMMENT "
		+ "ON SPCR.id = SOURCE_COMMENT.sourceComment.id "
		+ " "
		+ "LEFT OUTER JOIN SnsPostCommentLike AS IS_LIKE_TB "
		+ "ON SPCR.id = IS_LIKE_TB.snsPostCommentReaction.id "
		+ "AND IS_LIKE_TB.snsUser.id = :snsUserId AND IS_LIKE_TB.isLiked = TRUE "
		+ " "
		+ "WHERE SPCR.snsPost.id = :snsPostId AND SPCR.isSource = FALSE "
		+ "AND SPCR.sourceComment.id = :postCommentId "
		+ "AND SPCR.id < :snsPostCommentReactionId "
		+ "AND SPCR.deletedAt IS NULL "
		// + "GROUP BY SPCR.id, "
		// + "SPCR.commentMediaType, SPCR.commentMediaContent, SPCR.commentMsg, "
		// + "IS_LIKE_TB.isLiked, SPCR.createdAt, COMMENT_USER.profilePath, COMMENT_USER.id "
		+ "ORDER BY SPCR.id DESC")
	List<PostCommentDao> selectCommentByCommentId(
		@Param("snsPostId") Long snsPostId,
		@Param("snsUserId") Long snsUserId,
		@Param("postCommentId") Long postCommentId,
		@Param("snsPostCommentReactionId") Long snsPostCommentReactionId,
		Pageable pageable);

	@Query("SELECT "
		+ "SPCR.id AS postCommentId, "
		+ "SPCR.commentMediaType AS postCommentMediaType, "
		+ "SPCR.commentMediaContent AS postCommentMediaContent, "
		+ "SPCR.commentMsg AS commentMsg, "
		+ "COALESCE(IS_LIKE_TB.isLiked,FALSE) AS isLiked, SPCR.createdAt AS postedAt,  "
		+ "(SELECT COUNT(*) FROM SnsPostCommentLike CL WHERE CL.snsPostCommentReaction.id = SPCR.id AND CL.isLiked = TRUE) AS likeCount, "
		+ "(SELECT COUNT(*) FROM SnsPostCommentReaction CR WHERE CR.sourceComment.id = SPCR.id) AS commentCount, "
		+ "COMMENT_USER.profilePath AS profilePath, COMMENT_USER.id AS commentUserId, COMMENT_USER.username AS username, SPCR.sourceComment.id AS commentSourceId, COALESCE(SPCR.isSource,FALSE) AS isSource "
		+ "FROM SnsPostCommentReaction AS SPCR "
		+ "INNER JOIN SPCR.commentUser AS COMMENT_USER ON SPCR.commentUser.id = COMMENT_USER.id "
		+ " "
		+ "LEFT OUTER JOIN SnsPostCommentReaction AS SOURCE_COMMENT "
		+ "ON SPCR.id = SOURCE_COMMENT.sourceComment.id "
		+ " "
		+ "LEFT OUTER JOIN SnsPostCommentLike AS IS_LIKE_TB "
		+ "ON SPCR.id = IS_LIKE_TB.snsPostCommentReaction.id AND IS_LIKE_TB.snsUser.id = :snsUserId AND IS_LIKE_TB.isLiked = TRUE "
		+ " "
		+ "WHERE SPCR.snsPost.id = :snsPostId AND SPCR.isSource = FALSE "
		+ "AND SPCR.sourceComment.id = :replyCommentId "
		+ "AND SPCR.deletedAt IS NULL "
		+ "GROUP BY SPCR.id, "
		+ "SPCR.commentMediaType, SPCR.commentMediaContent, SPCR.commentMsg, "
		+ "IS_LIKE_TB.isLiked, SPCR.createdAt, COMMENT_USER.profilePath, COMMENT_USER.id "

		+ "ORDER BY SPCR.id DESC")
	List<PostCommentDao> selectRepliesByReplyCommentId(
		@Param("snsPostId") Long snsPostId,
		@Param("snsUserId") Long snsUserId,
		@Param("replyCommentId") Long replyCommentId);

	@Query(value = "SELECT SPCR FROM SnsPostCommentReaction SPCR WHERE "
		+ "SPCR.id = :snsPostCommentReactionId "
		+ "AND SPCR.commentUser.id = :snsUserId "
		+ "AND SPCR.deletedAt IS NULL ")
	Optional<SnsPostCommentReaction> findBySnsPostCommentReactionIdAndCommentUser_SnsUserId(
		@Param("snsPostCommentReactionId") Long snsPostCommentReactionId,
		@Param("snsUserId") Long snsUserId);


	@Query(value = "SELECT "
		+ "COUNT (SPCR) AS commentNum "
		+ "FROM SnsPostCommentReaction SPCR WHERE SPCR.snsPost.id = :snsPostId "
		+ "AND SPCR.commentUser.id != :snsUserId "
		+ "AND SPCR.deletedAt IS NULL ")
	Optional<PostCommentNumDao> findCommentNumWithoutMe(
		@Param("snsPostId") Long snsPostId,
		@Param("snsUserId") Long snsUserId);

	@Query(value = "SELECT "
		+ "COUNT (SPCR) AS replyNum "
		+ "FROM SnsPostCommentReaction SPCR WHERE SPCR.isSource = false "
		+ "AND SPCR.sourceComment.id = :commentReactionId "
		+ "AND SPCR.commentUser.id != :snsUserId "
		+ "AND SPCR.deletedAt IS NULL ")
	Optional<PostReplyNumDao> findReplyNumByCommentWithoutMe(
		@Param("commentReactionId") Long commentReactionId,
		@Param("snsUserId") Long snsUserId);

	// @Modifying(clearAutomatically = true, flushAutomatically = true)
	// @Query("delete from SnsPostCommentReaction SNS_PCR where SNS_PCR.snsPost = :snsPost AND SNS_PCR.sourceComment")
	// void deleteAllBySnsPostTagId(SnsPost snsPost);
}
