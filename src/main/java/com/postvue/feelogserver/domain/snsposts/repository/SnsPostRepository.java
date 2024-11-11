package com.postvue.feelogserver.domain.snsposts.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import com.postvue.feelogserver.domain.snsposts.SnsPost;
import com.postvue.feelogserver.domain.snsposts.dto.SnsPostDao;
import com.postvue.feelogserver.domain.snsposts.dto.SnsPostInfoDao;
import com.postvue.feelogserver.domain.snsposts.dto.SnsPostUserTagDto;
import com.postvue.feelogserver.domain.snsscrapboard.dao.RelationPostByMapDao;

import org.springframework.data.repository.query.Param;

public interface SnsPostRepository extends JpaRepository<SnsPost, Long>,JpaSpecificationExecutor<SnsPost> {
	@Query(value = "SELECT SNS_P FROM SnsPost AS SNS_P ORDER BY SNS_P.createdAt")
	List<SnsPost> findAllPost(Pageable pageable);

	String FOLLOW_RELATION_NATIVE_QUERY = "follow_relations as (SELECT SNS_F.following_id, SNS_F.follower_id FROM sns_user_follows_tb AS SNS_F where SNS_F.follower_id = :snsUserId)";
	String SNS_POST_DAO_COLUMN_NATIVE_QUERY =
		"SELECT sns_post.sns_post_id AS post_id, SNS_U.profile_path AS profile_path, "
			+ "COALESCE(SPUR.is_liked,false) AS is_liked, COALESCE(SPUR.is_reposted,false) AS is_reposted, COALESCE(SPUR.is_clipped,false) AS is_clipped, "
			+ "COALESCE(SPUR.is_bookmarked,false) AS is_bookmarked, sns_post.created_at AS posted_at, "
			+ "latitude, longitude, address, post_title, post_body_text, (CASE WHEN sns_post.sns_user_id = :snsUserId THEN false ELSE true END) AS followable, "
			+ "sns_post_contents, tags, tgt_aud_type, sns_post.sns_user_id AS sns_user_id, SNS_U.username AS username, FOLLOW.following_id as following_id ";

	String SHOW_BY_NOT_BLOCKED_USER_NATIVE_QUERY = "LEFT OUTER JOIN sns_block_users_tb AS SNS_BU "
		+ "ON SNS_BU.sns_blocker_user_id = :snsUserId AND SNS_BU.sns_blocked_user_id = sns_post.sns_user_id ";

	// @REFER: (CASE WHEN is_commented = TRUE THEN 4 ELSE 0 END) : 현재 sns_post_user_reactions_tb에서 is_commented 제거 => sns_post_comment_reactions에서 가져오도록 고려
	String STUFF_FOR_ME_BY_TAG_NATIVE_QUERY = "WITH "
		+ "follow_relations as (SELECT SNS_F.following_id, SNS_F.follower_id FROM sns_user_follows_tb AS SNS_F where SNS_F.follower_id = :snsUserId), "
		+ "interest_scores AS (SELECT sns_post_id, (CASE WHEN is_clipped = TRUE THEN 5 ELSE 0 END) + (CASE WHEN is_liked = TRUE THEN 3 ELSE 0 END) + (CASE WHEN is_bookmarked = TRUE THEN 2 ELSE 0 END) + (CASE WHEN is_reposted = TRUE THEN 1 ELSE 0 END) AS interest_score FROM sns_post_user_reactions_tb WHERE sns_user_id = :snsUserId), "
		+ "tag_interest AS (SELECT SNS_TP.sns_tag_id, SUM(INTER_S.interest_score) AS total_interest_score FROM interest_scores AS INTER_S INNER JOIN sns_tag_posts_tb AS SNS_TP ON INTER_S.sns_post_id = SNS_TP.sns_post_id GROUP BY SNS_TP.sns_tag_id),"
		+ "tag_follow_counts AS ( SELECT STF.sns_tag_id, COUNT(STF.sns_user_id) AS follow_count FROM sns_tag_follows_tb AS STF GROUP BY STF.sns_tag_id), "
		+ "total_tag_interest_scores AS (SELECT (CASE WHEN TI.sns_tag_id IS NOT NULL THEN TI.sns_tag_id ELSE TFC.sns_tag_id END) AS sns_tag_id, TFC.sns_tag_id AS test, COALESCE(TI.total_interest_score,0) + COALESCE(TFC.follow_count, 0) * 4 AS adjusted_interest_score FROM tag_interest TI FULL OUTER JOIN tag_follow_counts AS TFC ON TI.sns_tag_id = TFC.sns_tag_id), "
		+ "post_by_interest_scores AS (SELECT STP.sns_post_id, SUM(T_TAG_INTER_S.adjusted_interest_score) AS total_score FROM sns_tag_posts_tb AS STP INNER JOIN total_tag_interest_scores AS T_TAG_INTER_S ON T_TAG_INTER_S.sns_tag_id = STP.sns_tag_id GROUP BY STP.sns_post_id ORDER BY total_score DESC LIMIT :pageSize OFFSET :page) "
		+ "SELECT "
		+ "sns_post.sns_post_id AS post_id, SNS_U.profile_path AS profile_path, (CASE WHEN sns_post.sns_user_id = :snsUserId THEN false ELSE true END) AS followable, "
		+ "COALESCE(SPUR.is_liked,false) AS is_liked, COALESCE(SPUR.is_reposted,false) AS is_reposted, COALESCE(SPUR.is_clipped,false) AS is_clipped, "
		+ "COALESCE(SPUR.is_bookmarked,false) AS is_bookmarked, sns_post.created_at AS posted_at, "
		+ "latitude, longitude, address, post_title, post_body_text, "
		+ "sns_post_contents, tags, tgt_aud_type, sns_post.sns_user_id AS sns_user_id, SNS_U.username AS username, FOLLOW.following_id as following_id, "
		+ "(CASE WHEN SNS_BU.sns_blocker_user_id IS NOT NULL THEN TRUE ELSE FALSE END) AS is_blocked "
		+ "from sns_posts_tb AS sns_post "
		+ "INNER JOIN post_by_interest_scores AS PBIS ON sns_post.sns_post_id = PBIS.sns_post_id "
		+ "INNER JOIN sns_users_tb AS SNS_U ON sns_post.sns_user_id = SNS_U.sns_user_id "
		+ "LEFT OUTER JOIN follow_relations AS FOLLOW "
		+ "ON sns_post.sns_user_id = FOLLOW.following_id "
		+ "LEFT OUTER JOIN sns_post_user_reactions_tb AS SPUR "
		+ "ON SPUR.sns_user_id = :snsUserId AND sns_post.sns_post_id = SPUR.sns_post_id "
		+ SHOW_BY_NOT_BLOCKED_USER_NATIVE_QUERY
		+ "WHERE (SPUR.is_shown IS TRUE OR SPUR.is_shown IS NULL) AND SNS_BU.sns_blocker_user_id IS NULL ";

	String STUFF_FOR_ME_BY_FOLLOW_NATIVE_QUERY = "WITH "
		+ "follow_relations as (SELECT SNS_F.following_id, SNS_F.follower_id FROM sns_user_follows_tb AS SNS_F where SNS_F.follower_id = :snsUserId) "
		+ "SELECT sns_post.sns_post_id AS post_id, SNS_U.profile_path AS profile_path, "
		+ "COALESCE(SPUR.is_liked,false) AS is_liked, COALESCE(SPUR.is_reposted,false) AS is_reposted, COALESCE(SPUR.is_clipped,false) AS is_clipped, "
		+ "COALESCE(SPUR.is_bookmarked,false) AS is_bookmarked, sns_post.created_at AS posted_at, "
		+ "latitude, longitude, address, post_title, post_body_text, (CASE WHEN sns_post.sns_user_id = :snsUserId THEN false ELSE true END) AS followable, "
		+ "sns_post_contents, tags, tgt_aud_type, sns_post.sns_user_id AS sns_user_id, SNS_U.username AS username, FOLLOW.following_id as following_id "
		+ "from sns_posts_tb AS sns_post "
		+ "INNER JOIN follow_relations AS FOLLOW "
		+ "ON sns_post.sns_user_id = FOLLOW.following_id "
		+ "INNER JOIN sns_users_tb AS SNS_U ON sns_post.sns_user_id = SNS_U.sns_user_id "
		+ "LEFT OUTER JOIN sns_post_user_reactions_tb AS SPUR "
		+ "ON SPUR.sns_user_id = :snsUserId AND sns_post.sns_post_id = SPUR.sns_post_id "
		+ SHOW_BY_NOT_BLOCKED_USER_NATIVE_QUERY
		+ "WHERE sns_post.sns_post_id < :snsPostId AND (SPUR.is_shown IS TRUE OR SPUR.is_shown IS NULL) AND SNS_BU.sns_blocker_user_id IS NULL "
		+ "ORDER BY sns_post.sns_post_id DESC LIMIT :pageSize";

	String STUFF_FOR_ME_BY_POPULAR_NATIVE_QUERY = "with "
		+ "follow_relations as (SELECT SNS_F.following_id, SNS_F.follower_id FROM sns_user_follows_tb AS SNS_F where SNS_F.follower_id = :snsUserId), "
		+ "sns_posts_by_popular AS (SELECT SNS_POST.sns_post_id FROM sns_posts_tb AS SNS_POST ORDER BY (sns_post.reaction_count - 2 * POWER(EXTRACT(day FROM (sns_post.created_at - :currentDateTime)),2)) DESC LIMIT :pageSize offset :page)"
		+ "SELECT sns_post.sns_post_id AS post_id, SNS_U.profile_path AS profile_path, "
		+ "COALESCE(SPUR.is_liked,false) AS is_liked, COALESCE(SPUR.is_reposted,false) AS is_reposted, COALESCE(SPUR.is_clipped,false) AS is_clipped, "
		+ "COALESCE(SPUR.is_bookmarked,false) AS is_bookmarked, sns_post.created_at AS posted_at, "
		+ "latitude, longitude, address, post_title, post_body_text, (CASE WHEN sns_post.sns_user_id = :snsUserId THEN false ELSE true END) AS followable, "
		+ "sns_post_contents, tags, tgt_aud_type, sns_post.sns_user_id AS sns_user_id, SNS_U.username AS username, FOLLOW.following_id as following_id "
		+ "from sns_posts_tb AS sns_post "
		+ "INNER JOIN sns_posts_by_popular AS SPBP on SPBP.sns_post_id = sns_post.sns_post_id "
		+ "INNER JOIN sns_users_tb AS SNS_U ON sns_post.sns_user_id = SNS_U.sns_user_id "
		+ "LEFT OUTER JOIN follow_relations AS FOLLOW "
		+ "ON sns_post.sns_user_id = FOLLOW.following_id "
		+ "LEFT OUTER JOIN sns_post_user_reactions_tb AS SPUR "
		+ "ON SPUR.sns_user_id = :snsUserId AND sns_post.sns_post_id = SPUR.sns_post_id "
		+ SHOW_BY_NOT_BLOCKED_USER_NATIVE_QUERY
		+ "WHERE (SPUR.is_shown IS TRUE OR SPUR.is_shown IS NULL) AND SNS_BU.sns_blocker_user_id IS NULL";

	String TAG_FOR_ME_NATIVE_QUERY = "with "
		+ "follow_relations as (SELECT SNS_F.following_id, SNS_F.follower_id FROM sns_user_follows_tb AS SNS_F where SNS_F.follower_id = :snsUserId), "
		+ "my_follow_tags AS (SELECT SNS_TAG_F.sns_tag_id FROM sns_tag_follows_tb SNS_TAG_F WHERE SNS_TAG_F.sns_user_id = :snsUserId), "
		+ "tag_post AS (SELECT DISTINCT sns_tag_post.sns_post_id FROM sns_tag_posts_tb sns_tag_post INNER JOIN my_follow_tags AS STF ON sns_tag_post.sns_tag_id = STF.sns_tag_id WHERE sns_tag_post.sns_post_id < :snsPostId LIMIT :pageSize) "
		+ "SELECT sns_post.sns_post_id AS post_id, SNS_U.profile_path AS profile_path, "
		+ "COALESCE(SPUR.is_liked,false) AS is_liked, COALESCE(SPUR.is_reposted,false) AS is_reposted, COALESCE(SPUR.is_clipped,false) AS is_clipped, "
		+ "COALESCE(SPUR.is_bookmarked,false) AS is_bookmarked, sns_post.created_at AS posted_at, "
		+ "latitude, longitude, address, post_title, post_body_text, (CASE WHEN sns_post.sns_user_id = :snsUserId THEN false ELSE true END) AS followable, "
		+ "sns_post_contents, tags, tgt_aud_type, sns_post.sns_user_id AS sns_user_id, SNS_U.username AS username, FOLLOW.following_id as following_id "
		+ "from sns_posts_tb AS sns_post "
		+ "INNER JOIN tag_post ON sns_post.sns_post_id = tag_post.sns_post_id "
		+ "INNER JOIN sns_users_tb AS SNS_U ON sns_post.sns_user_id = SNS_U.sns_user_id "
		+ "LEFT OUTER JOIN follow_relations AS FOLLOW "
		+ "ON sns_post.sns_user_id = FOLLOW.following_id "
		+ "LEFT OUTER JOIN sns_post_user_reactions_tb AS SPUR "
		+ "ON SPUR.sns_user_id = :snsUserId AND sns_post.sns_post_id = SPUR.sns_post_id "
		+ SHOW_BY_NOT_BLOCKED_USER_NATIVE_QUERY
		+ "WHERE SNS_BU.sns_blocker_user_id IS NULL";

	String MAX_DISTANCE_QUERY = "5";  //5km
	String NEAR_FOR_ME_NATIVE_QUERY = "WITH "
		+ "follow_relations AS (\n"
		+ "    SELECT SNS_F.following_id, SNS_F.follower_id \n"
		+ "    FROM sns_user_follows_tb AS SNS_F \n"
		+ "    WHERE SNS_F.follower_id = :snsUserId\n"
		+ "),\n"
		+ "sns_posts_by_popular AS ("
		+ "SELECT SNS_POST.sns_post_id,(sns_post.reaction_count - 2 * POWER(EXTRACT(day FROM (sns_post.created_at - :currentDateTime)),2)) as rating FROM sns_posts_tb AS SNS_POST"
		+ ") "
		+ "SELECT * FROM (\n"
		+ "    SELECT \n"
		+ "        sns_post.sns_post_id AS post_id, \n"
		+ "        SNS_U.profile_path AS profile_path, \n"
		+ "        COALESCE(SPUR.is_liked,false) AS is_liked, \n"
		+ "        COALESCE(SPUR.is_reposted,false) AS is_reposted, \n"
		+ "        COALESCE(SPUR.is_clipped,false) AS is_clipped, \n"
		+ "        COALESCE(SPUR.is_bookmarked,false) AS is_bookmarked, \n"
		+ "        sns_post.created_at AS posted_at, \n"
		+ "        latitude, longitude, address, post_title, post_body_text, \n"
		+ "        (CASE WHEN sns_post.sns_user_id = :snsUserId THEN false ELSE true END) AS followable, \n"
		+ "        sns_post_contents, tags, tgt_aud_type, sns_post.sns_user_id AS sns_user_id, \n"
		+ "        SNS_U.username AS username, FOLLOW.following_id as following_id, \n"
		+ "        (111.32 * SQRT(POW(latitude - :userLatitude, 2) + POW((longitude - :userLongitude) * COS(RADIANS(:userLatitude)), 2))) AS distance,\n"
		+ "        SNS_PP.rating AS rating"
		+ "    FROM sns_posts_tb AS sns_post \n"
		+ "	   INNER JOIN sns_posts_by_popular SNS_PP ON SNS_PP.sns_post_id = sns_post.sns_post_id "
		+ "    INNER JOIN sns_users_tb AS SNS_U ON sns_post.sns_user_id = SNS_U.sns_user_id \n"
		+ "    LEFT OUTER JOIN follow_relations AS FOLLOW ON sns_post.sns_user_id = FOLLOW.following_id \n"
		+ "    LEFT OUTER JOIN sns_post_user_reactions_tb AS SPUR \n"
		+ "        ON SPUR.sns_user_id = :snsUserId\n"
		+ "        AND sns_post.sns_post_id = SPUR.sns_post_id \n"
		+ SHOW_BY_NOT_BLOCKED_USER_NATIVE_QUERY
		+ "    WHERE (SPUR.is_shown IS TRUE OR SPUR.is_shown IS NULL) \n"
		+ "      AND (SNS_BU.sns_blocker_user_id IS NULL)\n"
		+ ") AS subquery \n"
		+ "WHERE distance <= " + MAX_DISTANCE_QUERY + " "
		+ "ORDER BY distance ASC, rating DESC\n"
		+ "LIMIT :pageSize OFFSET :page";

	String NEAR_FOR_ME_NATIVE_QUERY_BY = "WITH "
		+ "follow_relations AS (\n"
		+ "    SELECT SNS_F.following_id, SNS_F.follower_id \n"
		+ "    FROM sns_user_follows_tb AS SNS_F \n"
		+ "    WHERE SNS_F.follower_id = :snsUserId\n"
		+ "),\n"
		+ "sns_posts_by_popular AS (SELECT SNS_POST.sns_post_id,(sns_post.reaction_count - 2 * POWER(EXTRACT(day FROM (sns_post.created_at - :currentDateTime)),2)) as rating FROM sns_posts_tb AS SNS_POST) "
		+ "SELECT * FROM (\n"
		+ "    SELECT \n"
		+ "        sns_post.sns_post_id AS post_id, \n"
		+ "        SNS_U.profile_path AS profile_path, \n"
		+ "        COALESCE(SPUR.is_liked,false) AS is_liked, \n"
		+ "        COALESCE(SPUR.is_reposted,false) AS is_reposted, \n"
		+ "        COALESCE(SPUR.is_clipped,false) AS is_clipped, \n"
		+ "        COALESCE(SPUR.is_bookmarked,false) AS is_bookmarked, \n"
		+ "        sns_post.created_at AS posted_at, \n"
		+ "        latitude, longitude, address, post_title, post_body_text, \n"
		+ "        (CASE WHEN sns_post.sns_user_id = :snsUserId THEN false ELSE true END) AS followable, \n"
		+ "        sns_post_contents, tags, tgt_aud_type, sns_post.sns_user_id AS sns_user_id, \n"
		+ "        SNS_U.username AS username, FOLLOW.following_id as following_id, \n"
		+ "        (111.32 * SQRT(POW(latitude - :userLatitude, 2) + POW((longitude - :userLongitude) * COS(RADIANS(:userLatitude)), 2))) AS distance,\n"
		+ "        SNS_PP.rating AS rating"
		+ "    FROM sns_posts_tb AS sns_post \n"
		+ "	   INNER JOIN SNS_POSTS_BY_POPULAR AS SPBR ON SNS_POST.sns_post_id = SPBR.sns_post_id "
		+ "    INNER JOIN sns_users_tb AS SNS_U ON sns_post.sns_user_id = SNS_U.sns_user_id \n"
		+ "    LEFT OUTER JOIN follow_relations AS FOLLOW ON sns_post.sns_user_id = FOLLOW.following_id \n"
		+ "    LEFT OUTER JOIN sns_post_user_reactions_tb AS SPUR \n"
		+ "        ON SPUR.sns_user_id = :snsUserId\n"
		+ "        AND sns_post.sns_post_id = SPUR.sns_post_id \n"
		+ SHOW_BY_NOT_BLOCKED_USER_NATIVE_QUERY
		+ "    WHERE (SPUR.is_shown IS TRUE OR SPUR.is_shown IS NULL) \n"
		+ "      AND (SNS_BU.sns_blocker_user_id IS NULL) AND (sns_post.post_content_business_type = :postContentBusinessType)\n"
		+ ") AS subquery \n"
		+ "WHERE distance <= " + MAX_DISTANCE_QUERY + " "
		+ "ORDER BY distance ASC, rating DESC \n"
		+ "LIMIT :pageSize OFFSET :page";

	String PROFILE_POSTS_QUERY = "with "
		+ "follow_relations as (SELECT SNS_F.following_id, SNS_F.follower_id FROM sns_user_follows_tb AS SNS_F where SNS_F.follower_id = :snsUserId) "
		+ "SELECT sns_post.sns_post_id AS post_id, SNS_U.profile_path AS profile_path, "
		+ "COALESCE(SPUR.is_liked,false) AS is_liked, COALESCE(SPUR.is_reposted,false) AS is_reposted, COALESCE(SPUR.is_clipped,false) AS is_clipped, "
		+ "COALESCE(SPUR.is_bookmarked,false) AS is_bookmarked, sns_post.created_at AS posted_at, "
		+ "latitude, longitude, address, post_title, post_body_text, (CASE WHEN sns_post.sns_user_id = :snsUserId THEN false ELSE true END) AS followable, "
		+ "sns_post_contents, tags, tgt_aud_type, sns_post.sns_user_id AS sns_user_id, SNS_U.username AS username, FOLLOW.following_id as following_id "
		+ "from sns_posts_tb AS sns_post "
		+ "INNER JOIN sns_users_tb AS SNS_U ON sns_post.sns_user_id = SNS_U.sns_user_id AND SNS_U.username = :username "
		+ "LEFT OUTER JOIN follow_relations AS FOLLOW "
		+ "ON sns_post.sns_user_id = FOLLOW.following_id "
		+ "LEFT OUTER JOIN sns_post_user_reactions_tb AS SPUR "
		+ "ON SPUR.sns_user_id = :snsUserId AND sns_post.sns_post_id = SPUR.sns_post_id "
		+ SHOW_BY_NOT_BLOCKED_USER_NATIVE_QUERY
		+ "WHERE sns_post.sns_post_id < :snsPostId AND SNS_BU.sns_blocker_user_id IS NULL "
		+ "ORDER BY sns_post.sns_post_id DESC LIMIT :pageSize";

	String DETAIL_POST_NATIVE_QUERY = "WITH "
		+ "follow_relations as (SELECT SNS_F.following_id, SNS_F.follower_id FROM sns_user_follows_tb AS SNS_F where SNS_F.follower_id = :snsUserId) "
		+ "SELECT sns_post.sns_post_id AS post_id, SNS_U.profile_path AS profile_path, "
		+ "COALESCE(SPUR.is_liked,false) AS is_liked, COALESCE(SPUR.is_reposted,false) AS is_reposted, COALESCE(SPUR.is_clipped,false) AS is_clipped, "
		+ "COALESCE(SPUR.is_bookmarked,false) AS is_bookmarked, sns_post.created_at AS posted_at, "
		+ "latitude, longitude, address, post_title, post_body_text, (CASE WHEN sns_post.sns_user_id = :snsUserId THEN false ELSE true END) AS followable, "
		+ "sns_post_contents, tags, tgt_aud_type, sns_post.sns_user_id AS sns_user_id, SNS_U.username AS username, FOLLOW.following_id as following_id "
		+ "from sns_posts_tb AS sns_post "
		+ "INNER JOIN sns_users_tb AS SNS_U ON sns_post.sns_user_id = SNS_U.sns_user_id "
		+ "LEFT OUTER JOIN follow_relations AS FOLLOW "
		+ "ON sns_post.sns_user_id = FOLLOW.following_id "
		+ "LEFT OUTER JOIN sns_post_user_reactions_tb AS SPUR "
		+ "ON SPUR.sns_user_id = :snsUserId AND sns_post.sns_post_id = SPUR.sns_post_id "
		+ SHOW_BY_NOT_BLOCKED_USER_NATIVE_QUERY
		+ "WHERE sns_post.sns_post_id = :snsPostId AND SNS_BU.sns_blocker_user_id IS NULL ";

	String POST_INFO_NATIVE_QUERY = "SELECT sns_post.sns_post_id AS post_id, "
		+ "sns_post.created_at AS posted_at, "
		+ "latitude, longitude, address, post_title, post_body_text, tgt_aud_type, "
		+ "sns_post_contents, tags, tgt_aud_type, sns_post.sns_user_id AS sns_user_id "
		+ "from sns_posts_tb AS sns_post "
		+ "INNER JOIN sns_users_tb AS SNS_U ON sns_post.sns_user_id = SNS_U.sns_user_id "
		+ "WHERE sns_post.sns_post_id = :snsPostId";

	String SEARCH_POST_QUERY_POPULAR_NATIVE_QUERY = "with  "
		+ "follow_relations as (SELECT SNS_F.following_id, SNS_F.follower_id FROM sns_user_follows_tb AS SNS_F where SNS_F.follower_id = :snsUserId),  "
		+ "tag_by_srch_query AS (SELECT SNS_T.sns_tag_id FROM SNS_TAGS_TB AS SNS_T WHERE tag_name @@ :searchQuery),  "
		+ "post_by_tag AS (SELECT DISTINCT sns_tag_post.sns_post_id FROM sns_tag_posts_tb sns_tag_post INNER JOIN tag_by_srch_query AS TAG_SQ ON sns_tag_post.sns_tag_id = TAG_SQ.sns_tag_id ),  "
		+ "scrap_relation AS (SELECT sns_scrap_board_id FROM SNS_SCRAP_BOARDS_TB WHERE scrap_name @@ :searchQuery), "
		+ "post_by_scrap AS (SELECT sns_post_id FROM SNS_SCRAPS_TB WHERE sns_scrap_board_id IN (SELECT * FROM scrap_relation)), "
		+ "post_relation AS (SELECT * FROM post_by_tag UNION SELECT * FROM post_by_scrap), "
		+ "sns_posts_by_popular AS (SELECT SNS_POST.sns_post_id,(sns_post.reaction_count - 2 * POWER(EXTRACT(day FROM (sns_post.created_at - :currentDateTime)),2)) as rating FROM sns_posts_tb AS SNS_POST) "
		+ "SELECT sns_post.sns_post_id AS post_id, SNS_U.profile_path AS profile_path,  "
		+ "COALESCE(SPUR.is_liked,false) AS is_liked, COALESCE(SPUR.is_reposted,false) AS is_reposted, COALESCE(SPUR.is_clipped,false) AS is_clipped,  "
		+ "COALESCE(SPUR.is_bookmarked,false) AS is_bookmarked, sns_post.created_at AS posted_at,  "
		+ "latitude, longitude, address, post_title, post_body_text, (CASE WHEN sns_post.sns_user_id = :snsUserId THEN false ELSE true END) AS followable,  "
		+ "sns_post_contents, tags, tgt_aud_type, sns_post.sns_user_id AS sns_user_id, SNS_U.username AS username, FOLLOW.following_id as following_id  "
		+ "from sns_posts_by_popular AS SPBP "
		+ "INNER JOIN sns_posts_tb AS SNS_POST on SPBP.sns_post_id = sns_post.sns_post_id  "
		+ "INNER JOIN post_relation ON sns_post.sns_post_id = post_relation.sns_post_id  "
		+ "INNER JOIN sns_users_tb AS SNS_U ON sns_post.sns_user_id = SNS_U.sns_user_id  "
		+ "LEFT OUTER JOIN follow_relations AS FOLLOW  "
		+ "ON sns_post.sns_user_id = FOLLOW.following_id  "
		+ "LEFT OUTER JOIN sns_post_user_reactions_tb AS SPUR "
		+ "ON SPUR.sns_user_id = :snsUserId AND sns_post.sns_post_id = SPUR.sns_post_id  "
		+ SHOW_BY_NOT_BLOCKED_USER_NATIVE_QUERY
		+ "WHERE SNS_BU.sns_blocker_user_id IS NULL ORDER BY SPBP.rating DESC LIMIT :pageSize offset :page";

	String TAG_RELATION_SEARCH_QUERY_NATIVE_QUERY = "with "
		+ "follow_relations as (SELECT SNS_F.following_id, SNS_F.follower_id FROM sns_user_follows_tb AS SNS_F where SNS_F.follower_id = :snsUserId), "
		+ "tag_by_srch_query AS (SELECT SNS_T.sns_tag_id FROM SNS_TAGS_TB AS SNS_T WHERE tag_name @@ :searchQuery), "
		+ "post_by_tag AS (SELECT DISTINCT sns_tag_post.sns_post_id FROM sns_tag_posts_tb sns_tag_post INNER JOIN tag_by_srch_query AS TAG_SQ ON sns_tag_post.sns_tag_id = TAG_SQ.sns_tag_id ),  "
		+ "scrap_relation AS (SELECT sns_scrap_board_id FROM SNS_SCRAP_BOARDS_TB WHERE scrap_name @@ :searchQuery), "
		+ "post_by_scrap AS (SELECT sns_post_id FROM SNS_SCRAPS_TB WHERE sns_scrap_board_id IN (SELECT * FROM scrap_relation)), "
		+ "post_relation AS (SELECT * FROM post_by_tag UNION SELECT * FROM post_by_scrap) "
		+ "SELECT sns_post.sns_post_id AS post_id, SNS_U.profile_path AS profile_path, "
		+ "COALESCE(SPUR.is_liked,false) AS is_liked, COALESCE(SPUR.is_reposted,false) AS is_reposted, COALESCE(SPUR.is_clipped,false) AS is_clipped, "
		+ "COALESCE(SPUR.is_bookmarked,false) AS is_bookmarked, sns_post.created_at AS posted_at, "
		+ "latitude, longitude, address, post_title, post_body_text, (CASE WHEN sns_post.sns_user_id = :snsUserId THEN false ELSE true END) AS followable, "
		+ "sns_post_contents, tags, tgt_aud_type, sns_post.sns_user_id AS sns_user_id, SNS_U.username AS username, FOLLOW.following_id as following_id "
		+ "from sns_posts_tb AS sns_post "
		+ "INNER JOIN post_relation ON sns_post.sns_post_id = post_relation.sns_post_id "
		+ "INNER JOIN sns_users_tb AS SNS_U ON sns_post.sns_user_id = SNS_U.sns_user_id "
		+ "LEFT OUTER JOIN follow_relations AS FOLLOW "
		+ "ON sns_post.sns_user_id = FOLLOW.following_id "
		+ "LEFT OUTER JOIN sns_post_user_reactions_tb AS SPUR "
		+ "ON SPUR.sns_user_id = :snsUserId AND sns_post.sns_post_id = SPUR.sns_post_id "
		+ SHOW_BY_NOT_BLOCKED_USER_NATIVE_QUERY
		+ "WHERE SNS_BU.sns_blocker_user_id IS NULL ORDER BY post_id DESC LIMIT :pageSize offset :page";

	String POST_RELATION_NATIVE_QUERY = "WITH "
		+ "follow_relations as (SELECT SNS_F.following_id, SNS_F.follower_id FROM sns_user_follows_tb AS SNS_F where SNS_F.follower_id = :snsUserId), "
		+ "tag_relation AS (SELECT sns_tag_id FROM SNS_TAGS_TB AS SNS_T WHERE tag_name &@~ :relatedTagListString), "
		+ "post_by_tag AS (SELECT sns_post_id FROM SNS_TAG_POSTS_TB AS SNS_TP WHERE SNS_TP.sns_tag_id IN (SELECT * FROM tag_relation)), "
		+ "scrap_relation AS (SELECT sns_scrap_board_id FROM SNS_SCRAP_BOARDS_TB WHERE scrap_name &@~ :relatedTagListString), "
		+ "post_by_scrap AS (SELECT sns_post_id FROM SNS_SCRAPS_TB WHERE sns_scrap_board_id IN (SELECT * FROM scrap_relation)), "
		+ "post_relation AS (SELECT * FROM post_by_tag UNION SELECT * FROM post_by_scrap) "
		+ "SELECT sns_post.sns_post_id AS post_id, SNS_U.profile_path AS profile_path,  "
		+ "COALESCE(SPUR.is_liked,false) AS is_liked, COALESCE(SPUR.is_reposted,false) AS is_reposted, COALESCE(SPUR.is_clipped,false) AS is_clipped,  "
		+ "COALESCE(SPUR.is_bookmarked,false) AS is_bookmarked, sns_post.created_at AS posted_at,  "
		+ "latitude, longitude, address, post_title, post_body_text, (CASE WHEN sns_post.sns_user_id = :snsUserId THEN false ELSE true END) AS followable,  "
		+ "sns_post_contents, tags, tgt_aud_type, sns_post.sns_user_id AS sns_user_id, SNS_U.username AS username, FOLLOW.following_id as following_id  "
		+ "from sns_posts_tb AS sns_post  "
		+ "INNER JOIN post_relation ON sns_post.sns_post_id = post_relation.sns_post_id  "
		+ "INNER JOIN sns_users_tb AS SNS_U ON sns_post.sns_user_id = SNS_U.sns_user_id  "
		+ "LEFT OUTER JOIN follow_relations AS FOLLOW ON sns_post.sns_user_id = FOLLOW.following_id  "
		+ "LEFT OUTER JOIN sns_post_user_reactions_tb AS SPUR "
		+ "ON SPUR.sns_user_id = :snsUserId "
		+ "AND sns_post.sns_post_id = SPUR.sns_post_id "
		+ SHOW_BY_NOT_BLOCKED_USER_NATIVE_QUERY
		+ "WHERE (SPUR.is_shown IS TRUE OR SPUR.is_shown IS NULL) AND SNS_BU.sns_blocker_user_id IS NULL AND sns_post.sns_post_id < :cursorId ORDER BY sns_post.sns_post_id DESC LIMIT :pageSize";

	String MAP_POST_RELATION_QUERY = "WITH\n"
		+ "INCLUDE_LOCATION_TB AS (SELECT SNS_P.sns_post_id FROM SNS_POSTS_TB AS SNS_P WHERE SNS_P.latitude IS NOT NULL),\n"
		+ "RELATION_TAG_BY_SEARCH_QUERY AS (SELECT\n"
		+ "\tSNS_T.sns_tag_id AS tag_id\n"
		+ "FROM\n"
		+ "\tsns_tags_tb AS SNS_T \n"
		+ "WHERE\n"
		+ "\tSNS_T.tag_name @@ :searchQuery),\n"
		+ "TAG_RELATION AS (SELECT\n"
		+ "\tSNS_TP.sns_post_id as post_id\n"
		+ "FROM\n"
		+ "\tSNS_TAG_POSTS_TB AS SNS_TP \n"
		+ "INNER JOIN\n"
		+ "\tINCLUDE_LOCATION_TB ON INCLUDE_LOCATION_TB.sns_post_id = SNS_TP.sns_post_id\n"
		+ "INNER JOIN\n"
		+ "\tRELATION_TAG_BY_SEARCH_QUERY \n"
		+ "\tON SNS_TP.sns_tag_id = RELATION_TAG_BY_SEARCH_QUERY.tag_id \n"
		+ "OFFSET :page LIMIT :pageSize\n"
		+ "),\n"
		+ "RELATION_SCARP_BY_SEARCH_QUERY AS (SELECT\n"
		+ "\tSNS_SB.sns_scrap_board_id AS scrap_board_id\n"
		+ "FROM\n"
		+ "\tsns_scrap_boards_tb AS SNS_SB \n"
		+ "WHERE\n"
		+ "\tSNS_SB.scrap_name @@ :searchQuery),\n"
		+ "SCRAP_RELATION AS (SELECT\n"
		+ "\tSNS_S.sns_post_id AS post_id\n"
		+ "FROM\n"
		+ "\tSNS_SCRAPS_TB AS SNS_S \n"
		+ "INNER JOIN\n"
		+ "\tINCLUDE_LOCATION_TB ON INCLUDE_LOCATION_TB.sns_post_id = SNS_S.sns_post_id\n"
		+ "INNER JOIN\n"
		+ "\tRELATION_SCARP_BY_SEARCH_QUERY \n"
		+ "\tON SNS_S.sns_scrap_board_id = RELATION_SCARP_BY_SEARCH_QUERY.scrap_board_id\n"
		+ "OFFSET :page LIMIT :pageSize\n"
		+ "),\t\n"
		+ "RELATION_POST_BY_SEARCH_QUERY AS (SELECT\n"
		+ "\tSNS_P.sns_post_id AS post_id\n"
		+ "FROM\n"
		+ "\tsns_posts_tb AS SNS_P\n"
		+ "INNER JOIN\n"
		+ "\tINCLUDE_LOCATION_TB ON INCLUDE_LOCATION_TB.sns_post_id = SNS_P.sns_post_id\n"
		+ "WHERE\n"
		+ "\tSNS_P.post_title @@ :searchQuery OR SNS_P.post_body_text @@ :searchQuery\n"
		+ "OFFSET :page LIMIT :pageSize\n"
		+ "),\n"
		+ "POST_ID_BY_MAP_RELATION AS (SELECT post_id FROM RELATION_POST_BY_SEARCH_QUERY \n"
		+ "UNION \n"
		+ "SELECT post_id FROM TAG_RELATION\n"
		+ "UNION\n"
		+ "SELECT post_id FROM SCRAP_RELATION),\n"
		+ "follow_relations as (SELECT SNS_F.following_id, SNS_F.follower_id FROM sns_user_follows_tb AS SNS_F where SNS_F.follower_id = :snsUserId) \n"
		+ "SELECT sns_post.sns_post_id AS post_id, SNS_U.profile_path AS profile_path, \n"
		+ "COALESCE(SPUR.is_liked,false) AS is_liked, COALESCE(SPUR.is_reposted,false) AS is_reposted, COALESCE(SPUR.is_clipped,false) AS is_clipped, \n"
		+ "COALESCE(SPUR.is_bookmarked,false) AS is_bookmarked, sns_post.created_at AS posted_at, \n"
		+ "latitude, longitude, address, post_title, post_body_text, (CASE WHEN sns_post.sns_user_id = :snsUserId THEN false ELSE true END) AS followable, \n"
		+ "sns_post_contents, tags, tgt_aud_type, sns_post.sns_user_id AS sns_user_id, SNS_U.username AS username, FOLLOW.following_id as following_id \n"
		+ "from sns_posts_tb AS sns_post\n"
		+ "INNER JOIN POST_ID_BY_MAP_RELATION ON POST_ID_BY_MAP_RELATION.post_id = sns_post.sns_post_id\n"
		+ "INNER JOIN sns_users_tb AS SNS_U ON sns_post.sns_user_id = SNS_U.sns_user_id \n"
		+ "LEFT OUTER JOIN follow_relations AS FOLLOW \n"
		+ "ON sns_post.sns_user_id = FOLLOW.following_id \n"
		+ "LEFT OUTER JOIN sns_post_user_reactions_tb AS SPUR \n"
		+ "ON SPUR.sns_user_id = :snsUserId AND sns_post.sns_post_id = SPUR.sns_post_id \n"
		+ SHOW_BY_NOT_BLOCKED_USER_NATIVE_QUERY
		+ "WHERE SNS_BU.sns_blocker_user_id IS NULL";

	// rerfer: 수정
	// @Query(
	// 	"select new com.postvue.feelogserver.domain.snsposts.dto.SnsPostRelation(snsPost, "
	// 		+ "follow.followerUser.snsUserId, "
	// 		+ "like.snsPostLikeId, "
	// 		+ "bookmark.snsUserBookmarkId, "
	// 		+ "clip.snsPostClipId, "
	// 		+ "repost.snsRepostId) "
	// 		+ "from SnsPost snsPost "
	// 		+ "LEFT OUTER JOIN SnsUserFollow follow ON snsPost.snsUser = follow.followingUser "
	// 		+ "AND follow.followerUser.snsUserId = :snsUserId "
	// 		+ "LEFT OUTER JOIN SnsPostLike like ON snsPost = like.snsPost "
	// 		+ "AND like.snsUser.snsUserId = :snsUserId "
	// 		+ "LEFT OUTER JOIN SnsPostClip clip ON snsPost = clip.snsPost "
	// 		+ "AND clip.snsUser.snsUserId = :snsUserId "
	// 		+ "LEFT OUTER JOIN SnsUserBookmark bookmark ON snsPost = bookmark.snsPost "
	// 		+ "AND bookmark.snsUser.snsUserId = :snsUserId "
	// 		+ "LEFT OUTER JOIN SnsUserRepost repost ON snsPost = repost.snsPost "
	// 		+ "AND repost.snsUser.snsUserId = :snsUserId")
	// List<SnsPostRelation> findSnsPostRelation(Pageable pageable, Long snsUserId);

	@Query(
		"select new com.postvue.feelogserver.domain.snsposts.dto.SnsPostUserTagDto(snsPost, "
			+ "follow.followerUser.id, "
			+ "COALESCE(SPUR.isLiked,false), "
			+ "COALESCE(SPUR.isClipped,false), "
			+ "COALESCE(SPUR.isBookmarked,false), "
			+ "COALESCE(SPUR.isReposted,false)) "
			+ "from SnsPost snsPost "
			+ "LEFT OUTER JOIN SnsUserFollow follow ON snsPost.snsUser = follow.followingUser "
			+ "AND follow.followerUser.id = :snsUserId "
			+ "LEFT OUTER JOIN SnsPostUserReaction SPUR ON snsPost.id = SPUR.snsPost.id "
			+ "AND SPUR.snsUser.id = :snsUserId")
	List<SnsPostUserTagDto> findSnsPostRelationWithTag(
		@Param("snsUserId") Long snsUserId,
		Pageable pageable
	);

	@Query(value = STUFF_FOR_ME_BY_TAG_NATIVE_QUERY, nativeQuery = true)
	List<SnsPostDao> selectTasteForMeByTag(
		@Param("snsUserId") Long snsUserId,
		@Param("page") Integer page,
		@Param("pageSize") Integer pageSize);

	@Query(value = STUFF_FOR_ME_BY_FOLLOW_NATIVE_QUERY, nativeQuery = true)
	List<SnsPostDao> selectTasteForMeByFollow(
		@Param("snsUserId") Long snsUserId,
		@Param("snsPostId") Long snsPostId,
		@Param("pageSize") Integer pageSize);

	@Query(value = STUFF_FOR_ME_BY_POPULAR_NATIVE_QUERY, nativeQuery = true)
	List<SnsPostDao> selectTasteForMeByPopular(@Param("snsUserId") Long snsUserId, @Param("page") Integer page,
		@Param("pageSize") Integer pageSize,
		@Param("currentDateTime") LocalDateTime currentDateTime);

	@Query(value = TAG_FOR_ME_NATIVE_QUERY, nativeQuery = true)
	List<SnsPostDao> selectTagForMe(
		@Param("snsUserId") Long snsUserId,
		@Param("snsPostId") Long snsPostId,
		@Param("pageSize") Integer pageSize);

	@Query(value = NEAR_FOR_ME_NATIVE_QUERY, nativeQuery = true)
	List<SnsPostDao> selectNearForMe(
		@Param("snsUserId") Long snsUserId,
		@Param("page") Integer page,
		@Param("pageSize") Integer pageSize,
		@Param("userLatitude") Float userLatitude,
		@Param("userLongitude") Float userLongitude,
		@Param("currentDateTime") LocalDateTime currentDateTime);

	@Query(value = NEAR_FOR_ME_NATIVE_QUERY_BY, nativeQuery = true)
	List<SnsPostDao> selectNearForMeBy(
		@Param("snsUserId") Long snsUserId,
		@Param("page") Integer page,
		@Param("pageSize") Integer pageSize,
		@Param("userLatitude") Float userLatitude,
		@Param("userLongitude") Float userLongitude,
		@Param("postContentBusinessType") String postContentBusinessType,
		@Param("currentDateTime") LocalDateTime currentDateTime);

	@Query(value = PROFILE_POSTS_QUERY, nativeQuery = true)
	List<SnsPostDao> selectProfilePosts(
		@Param("snsUserId") Long snsUserId,
		@Param("username") String username,
		@Param("snsPostId") Long snsPostId,
		@Param("pageSize") Integer pageSize);

	@Query(value = DETAIL_POST_NATIVE_QUERY, nativeQuery = true)
	Optional<SnsPostDao> selectDetailPost(
		@Param("snsUserId") Long snsUserId,
		@Param("snsPostId") Long snsPostId);

	@Query(value = POST_INFO_NATIVE_QUERY, nativeQuery = true)
	Optional<SnsPostInfoDao> selectPostInfo(
		@Param("snsPostId") Long snsPostId);

	Optional<SnsPost> findByIdAndSnsUser_Id(
		@Param("snsPostId") Long snsPostId,
		@Param("snsUserId") Long snsUserId);

	@Query(value = TAG_RELATION_SEARCH_QUERY_NATIVE_QUERY, nativeQuery = true)
	List<SnsPostDao> selectTagByRelationSearchQuery(
		@Param("snsUserId") Long snsUserId,
		@Param("page") Integer page,
		@Param("searchQuery") String searchQuery,
		@Param("pageSize") Integer pageSize);

	@Query(value = SEARCH_POST_QUERY_POPULAR_NATIVE_QUERY, nativeQuery = true)
	List<SnsPostDao> selectTagByRelationSearchQueryByPopular(
		@Param("snsUserId") Long snsUserId,
		@Param("searchQuery") String searchQuery,
		@Param("page") Integer page,
		@Param("pageSize") Integer pageSize,
		@Param("currentDateTime") LocalDateTime currentDateTime);

	@Query(value = POST_RELATION_NATIVE_QUERY, nativeQuery = true)
	List<SnsPostDao> selectPostRelation(
		@Param("relatedTagListString") String relatedTagListString,
		@Param("snsUserId") Long snsUserId,
		@Param("cursorId") Long cursorId,
		@Param("pageSize") Integer pageSize);

	@Query(
		value = "WITH \n"
			+ "INCLUDE_LOCATION_TB AS (\n"
			+ "    SELECT SNS_P.sns_post_id \n"
			+ "    FROM SNS_POSTS_TB AS SNS_P \n"
			+ "    WHERE SNS_P.latitude IS NOT NULL\n"
			+ "),\n"
			+ "RELATION_TAG_BY_SEARCH_QUERY AS (\n"
			+ "    SELECT\n"
			+ "        SNS_T.sns_tag_id AS tag_id,\n"
			+ "        SNS_T.tag_name AS tag_name\n"
			+ "    FROM\n"
			+ "        sns_tags_tb AS SNS_T \n"
			+ "    WHERE\n"
			+ "        SNS_T.tag_name @@ :searchQuery\n"
			+ "), \n"
			+ "TAG_CHECK AS (\n"
			+ "    SELECT \n"
			+ "        RELATION_TAG_BY_SEARCH_QUERY.tag_name AS SEARCH_QUERY,\n"
			+ "        CASE \n"
			+ "            WHEN EXISTS (\n"
			+ "                SELECT 1\n"
			+ "                FROM SNS_TAG_POSTS_TB AS SNS_TP \n"
			+ "                INNER JOIN INCLUDE_LOCATION_TB \n"
			+ "                    ON INCLUDE_LOCATION_TB.sns_post_id = SNS_TP.sns_post_id\n"
			+ "                WHERE \n"
			+ "                    SNS_TP.sns_tag_id = RELATION_TAG_BY_SEARCH_QUERY.tag_id\n"
			+ "            ) THEN TRUE\n"
			+ "            ELSE FALSE\n"
			+ "        END AS exists_flag\n"
			+ "    FROM \n"
			+ "        RELATION_TAG_BY_SEARCH_QUERY\n"
			+ "),\n"
			+ "RELATION_SCARP_BY_SEARCH_QUERY AS (SELECT\n"
			+ "\tSNS_SB.sns_scrap_board_id AS scrap_board_id,\n"
			+ "\tSNS_SB.scrap_name AS scrap_name\n"
			+ "FROM\n"
			+ "\tsns_scrap_boards_tb AS SNS_SB \n"
			+ "WHERE\n"
			+ "\tSNS_SB.scrap_name @@ :searchQuery), \n"
			+ "SCRAP_CHECK AS (\n"
			+ "    SELECT \n"
			+ "        RELATION_SCARP_BY_SEARCH_QUERY.scrap_name AS SEARCH_QUERY,\n"
			+ "        CASE \n"
			+ "            WHEN EXISTS (\n"
			+ "                SELECT 1\n"
			+ "                FROM SNS_SCRAPS_TB AS SNS_S\n"
			+ "                INNER JOIN INCLUDE_LOCATION_TB \n"
			+ "                    ON INCLUDE_LOCATION_TB.sns_post_id = SNS_S.sns_post_id\n"
			+ "                WHERE \n"
			+ "                    SNS_S.sns_scrap_board_id = RELATION_SCARP_BY_SEARCH_QUERY.scrap_board_id\n"
			+ "            ) THEN TRUE\n"
			+ "            ELSE FALSE\n"
			+ "        END AS exists_flag\n"
			+ "    FROM \n"
			+ "        RELATION_SCARP_BY_SEARCH_QUERY\n"
			+ "),\n"
			+ "POST_CHECK AS (\n"
			+ "    SELECT\n"
			+ "        :searchQuery AS SEARCH_QUERY,\n"
			+ "        CASE \n"
			+ "            WHEN EXISTS (\n"
			+ "                SELECT 1\n"
			+ "                FROM sns_posts_tb AS SNS_P\n"
			+ "                INNER JOIN INCLUDE_LOCATION_TB \n"
			+ "                    ON INCLUDE_LOCATION_TB.sns_post_id = SNS_P.sns_post_id\n"
			+ "                WHERE \n"
			+ "                    SNS_P.post_title @@ :searchQuery OR SNS_P.post_body_text @@ :searchQuery\n"
			+ "            ) THEN TRUE\n"
			+ "            ELSE FALSE\n"
			+ "        END AS exists_flag\n"
			+ ")\n"
			+ "SELECT SEARCH_QUERY FROM TAG_CHECK "
			+ "UNION "
			+ "SELECT SEARCH_QUERY FROM SCRAP_CHECK "
			+ "UNION "
			+ "SELECT SEARCH_QUERY FROM POST_CHECK "
			+ "offset :page LIMIT :pageSize"
		, nativeQuery = true)
	List<RelationPostByMapDao> findAllMapPostBySearchQuery(
		@Param("searchQuery") String searchQuery,
		@Param("page") Integer page,
		@Param("pageSize") Integer pageSize
	);

	@Query(value = MAP_POST_RELATION_QUERY, nativeQuery = true)
	List<SnsPostDao> findAllMapPostRelationBySearchQuery(
		@Param("snsUserId") Long snsUserId,
		@Param("searchQuery") String searchQuery,
		@Param("page") Integer page,
		@Param("pageSize") Integer pageSize
	);
}
