package com.postvue.feelogserver.domain.snsuserfollows.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.postvue.feelogserver.domain.snsuserfollows.SnsUserFollow;
import com.postvue.feelogserver.domain.snsuserfollows.dao.FollowRecommInfoDao;
import com.postvue.feelogserver.domain.snsuserfollows.dao.ProfileFollowDao;

import org.springframework.data.repository.query.Param;

@Repository
public interface SnsUserFollowRepository extends JpaRepository<SnsUserFollow, Long>, JpaSpecificationExecutor<SnsUserFollow> {

	String RECOMMEND_FOLLOW_POST_LIST_NATIVE_QUERY = "WITH "
		+ "FOLLOW_LIST_TB AS ("
		+ "SELECT SNS_U.sns_user_id, username, profile_path, "
		+ "COALESCE(SNS_UFS.follower_num,0) as follower_num,  "
		+ "COALESCE(following_num,0) as following_num "
		+ "FROM SNS_USERS_TB AS SNS_U "
		+ "LEFT OUTER JOIN sns_user_follow_statistics_tb AS SNS_UFS ON SNS_UFS.sns_user_id = SNS_U.sns_user_id "
		+ "LEFT OUTER JOIN sns_block_users_tb AS SNS_BU ON (SNS_BU.sns_blocker_user_id = :myUserId AND SNS_BU.sns_blocked_user_id = SNS_U.sns_user_id) OR (SNS_BU.sns_blocker_user_id = SNS_U.sns_user_id AND SNS_BU.sns_blocked_user_id = :myUserId)  "
		+ "WHERE SNS_BU.sns_blocked_user_id IS NULL "
		+ "ORDER BY SNS_UFS.follower_num DESC offset 0 LIMIT :pageSize "
		+ "),"
		+ "POST_ROW_LIST_TB AS ( "
		+ "SELECT sns_user_id, sns_post_id,sns_post_contents, ROW_NUMBER() OVER (PARTITION BY sns_user_id ORDER BY sns_post_id DESC) AS post_row_num FROM sns_posts_tb WHERE deleted_at IS NULL), "
		+ "POST_LIST_TB AS (SELECT sns_user_id, '[' || STRING_AGG("
		+ "jsonb_build_object('postId', sns_post_id, 'postContents', sns_post_contents)::TEXT, "
		+ "',' "
		+ ") || ']' AS post_id_contents_string FROM POST_ROW_LIST_TB WHERE post_row_num <= 7 GROUP BY sns_user_id) "
		+ " "
		+ "SELECT FOLLOW_LT.*, COALESCE(POST_LIST_TB.post_id_contents_string,'[]') as post_id_contents_string FROM FOLLOW_LIST_TB AS FOLLOW_LT "
		+ "LEFT OUTER JOIN POST_LIST_TB ON FOLLOW_LT.sns_user_id = POST_LIST_TB.sns_user_id ";

	@Query(value = RECOMMEND_FOLLOW_POST_LIST_NATIVE_QUERY, nativeQuery = true)
	List<FollowRecommInfoDao> selectRecommendFollowPostList(
		@Param("myUserId") Long myUserId,
		@Param("pageSize") Integer pageSize
	);

	String RECOMMEND_FOLLOW_POST_LIST_BY_ADMIN_NATIVE_QUERY = "WITH FOLLOW_LIST_TB AS ("
		+ "SELECT SNS_U.sns_user_id, username, profile_path, "
		+ "COALESCE(SNS_UFS.follower_num,0) as follower_num,  "
		+ "COALESCE(following_num,0) as following_num "
		+ "FROM SNS_USERS_TB AS SNS_U "
		+ "LEFT OUTER JOIN sns_user_follow_statistics_tb AS SNS_UFS ON SNS_UFS.sns_user_id = SNS_U.sns_user_id "
		+ "LEFT OUTER JOIN sns_block_users_tb AS SNS_BU ON (SNS_BU.sns_blocker_user_id = :myUserId AND SNS_BU.sns_blocked_user_id = SNS_U.sns_user_id) OR (SNS_BU.sns_blocker_user_id = SNS_U.sns_user_id AND SNS_BU.sns_blocked_user_id = :myUserId)  "
		+ "WHERE SNS_U.sns_user_id IN :followList AND SNS_BU.sns_blocked_user_id IS NULL "
		+ "),"
		+ "POST_ROW_LIST_TB AS ( "
		+ "SELECT sns_user_id, sns_post_id,sns_post_contents, ROW_NUMBER() OVER (PARTITION BY sns_user_id ORDER BY sns_post_id) AS post_row_num FROM sns_posts_tb where deleted_at IS NULL), "
		+ "POST_LIST_TB AS (SELECT sns_user_id, '[' || STRING_AGG("
		+ "jsonb_build_object('postId', sns_post_id, 'postContents', sns_post_contents)::TEXT, "
		+ "',' "
		+ ") || ']' AS post_id_contents_string FROM POST_ROW_LIST_TB WHERE post_row_num <= 7 GROUP BY sns_user_id) "
		+ " "
		+ "SELECT FOLLOW_LT.*, COALESCE(POST_LIST_TB.post_id_contents_string,'[]') as post_id_contents_string FROM FOLLOW_LIST_TB AS FOLLOW_LT "
		+ "LEFT OUTER JOIN POST_LIST_TB ON FOLLOW_LT.sns_user_id = POST_LIST_TB.sns_user_id ";
	@Query(value = RECOMMEND_FOLLOW_POST_LIST_BY_ADMIN_NATIVE_QUERY, nativeQuery = true)
	List<FollowRecommInfoDao> selectRecommendFollowListByAdmin(
		@Param("follow") List<Long> followList,
		@Param("myUserId") Long myUserId);

	//@ANSWER: 수정 필요, 현재 메시지에 대한 유저 정보의 경우, follow와 아에 관련되어 있지 않고, 오로지 message 테이블로 로써만 관계를 가짐 -> 수정됨
	// @Query(value =
	// 	"SELECT "
	// 		+ "SNS_UF "
	// 		+ "FROM SnsUserFollow AS SNS_UF "
	// 		+ "WHERE (SNS_UF.followingUser.username = :username AND SNS_UF.followerUser.snsUserId = :myUserId ) "
	// 		+ "OR (SNS_UF.followerUser.username = :username AND SNS_UF .followingUser.snsUserId = :myUserId) "
	// 		+ "AND SNS_UF.isFollowerBack IS TRUE")
	// Optional<SnsUserFollow> findByUsernameAndUserId(String username, Long myUserId);

	@Query(value = "SELECT SNS_UF FROM SnsUserFollow AS SNS_UF WHERE SNS_UF.followingUser.id = :snsUserId")
	List<SnsUserFollow> findAllFollowerList(@Param("snsUserId") Long snsUserId);

	@Query(value = "SELECT SNS_UF FROM SnsUserFollow AS SNS_UF WHERE SNS_UF.followerUser.id = :snsUserId")
	List<SnsUserFollow> findAllFollowingList(@Param("snsUserId") Long snsUserId);

	@Query(value = "SELECT SNS_UF FROM SnsUserFollow AS SNS_UF WHERE SNS_UF.followerUser.id = :snsUserId")
	List<SnsUserFollow> findAllFollowingListByPageable(@Param("snsUserId") Long snsUserId, Pageable pageable);

	@Query(value =
		"WITH "
			+ "follow_relations as (SELECT SNS_F.following_id, SNS_F.follower_id FROM sns_user_follows_tb AS SNS_F where SNS_F.follower_id = :myUserId), "
			+ "user_by_username as (SELECT sns_user_id FROM sns_users_tb WHERE LOWER(username) = LOWER(:username)),"
			+ "FOLLOWERS as (SELECT * FROM sns_user_follows_tb AS SNS_UF WHERE SNS_UF.follower_id IN (SELECT * FROM user_by_username)) "
			+ "SELECT sns_user_id, username, profile_path, nickname, "
			+ "(CASE WHEN sns_user_id = :myUserId THEN TRUE ELSE FALSE END) as is_me, "
			+ "(CASE WHEN follow_relations.follower_id IS NOT NULL THEN TRUE ELSE FALSE END) as is_followed "
			+ "FROM FOLLOWERS "
			+ "LEFT OUTER JOIN follow_relations ON FOLLOWERS.following_id = follow_relations.following_id "
			+ "LEFT OUTER JOIN sns_block_users_tb AS SNS_BU "
			+ "ON (SNS_BU.sns_blocker_user_id = COALESCE(:myUserId,-1) AND SNS_BU.sns_blocked_user_id = FOLLOWERS.following_id) "
			+ "OR (SNS_BU.sns_blocker_user_id = FOLLOWERS.following_id AND SNS_BU.sns_blocked_user_id = COALESCE(:myUserId,-1)) "
			+ "INNER JOIN sns_users_tb AS SNS_U ON FOLLOWERS.following_id = SNS_U.sns_user_id "
			+ "WHERE SNS_BU.sns_blocker_user_id IS NULL "
			+ "ORDER BY SNS_U.sns_user_id DESC LIMIT :pageNum OFFSET :page"

		, nativeQuery = true)
	List<ProfileFollowDao> selectAllFollowingListByPageable(
		@Param("myUserId") Long myUserId,
		@Param("username") String username,
		@Param("page") Integer page,
		@Param("pageNum") Integer pageNum);

	@Query(value =
		"WITH "
			+ "follow_relations as (SELECT SNS_F.following_id, SNS_F.follower_id FROM sns_user_follows_tb AS SNS_F where SNS_F.follower_id = :myUserId), "
			+ "user_by_username as (SELECT sns_user_id FROM sns_users_tb WHERE LOWER(username) = LOWER(:username)),"
			+ "FOLLOWERS as (SELECT * FROM sns_user_follows_tb AS SNS_UF WHERE SNS_UF.following_id IN (SELECT * FROM user_by_username)) "
			+ "SELECT sns_user_id, username, profile_path, nickname, "
			+ "(CASE WHEN sns_user_id = :myUserId THEN TRUE ELSE FALSE END) as is_me, "
			+ "(CASE WHEN follow_relations.follower_id IS NOT NULL THEN TRUE ELSE FALSE END) as is_followed "
			+ "FROM FOLLOWERS "
			+ "LEFT OUTER JOIN follow_relations ON FOLLOWERS.follower_id = follow_relations.following_id  "
			+ "LEFT OUTER JOIN sns_block_users_tb AS SNS_BU "
			+ "ON (SNS_BU.sns_blocker_user_id = COALESCE(:myUserId,-1) AND SNS_BU.sns_blocked_user_id = FOLLOWERS.follower_id) "
			+ "OR (SNS_BU.sns_blocker_user_id = FOLLOWERS.follower_id AND SNS_BU.sns_blocked_user_id = COALESCE(:myUserId,-1)) "
			+ "INNER JOIN sns_users_tb AS SNS_U ON FOLLOWERS.follower_id = SNS_U.sns_user_id "
			+ "WHERE SNS_BU.sns_blocker_user_id IS NULL "
			+ "ORDER BY SNS_U.sns_user_id DESC LIMIT :pageNum OFFSET :page"
		, nativeQuery = true)
	List<ProfileFollowDao> selectAllFollowerListByPageable(
		@Param("myUserId") Long myUserId,
		@Param("username") String username,
		@Param("page") Integer page,
		@Param("pageNum") Integer pageNum);

	// @Query(value = "SELECT SNS_UF FROM SnsUserFollow AS SNS_UF "
	// 	+ "WHERE (SNS_UF.followerUser.snsUserId = :myUserId AND SNS_UF.followingUser.snsUserId = :followId) "
	// 	+ "OR (SNS_UF.followingUser.snsUserId = :myUserId AND SNS_UF.followerUser.snsUserId = :followId)")
	// Optional<SnsUserFollow> findByFollowerUseAndFollowingUser(Long myUserId, Long followId);

	@Query(value = "SELECT SNS_UF FROM SnsUserFollow AS SNS_UF "
		+ "WHERE SNS_UF.followerUser.id = :myUserId AND SNS_UF.followingUser.id = :followId")
	Optional<SnsUserFollow> findByFollowerUseAndFollowingUser(
		@Param("myUserId") Long myUserId,
		@Param("followId") Long followId);


	@Query(value = "SELECT SNS_UF FROM SnsUserFollow AS SNS_UF "
		+ "WHERE SNS_UF.followerUser.id = :myUserId OR SNS_UF.followingUser.id = :myUserId")
	List<SnsUserFollow> findAllByMyFollow(
		@Param("myUserId")Long myUserId);

	@Query(value = "SELECT SNS_UF FROM SnsUserFollow AS SNS_UF "
		+ "WHERE SNS_UF.followerUser.id IN :snsUserIdList OR SNS_UF.followingUser.id IN :snsUserIdList")
	List<SnsUserFollow> findAllByMyFollowByUserList(
		@Param("snsUserIdList") List<Long> snsUserIdList);
}
