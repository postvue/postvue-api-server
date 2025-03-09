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
import com.postvue.feelogserver.domain.snsposts.vo.TgtAudTypeValue;
import com.postvue.feelogserver.domain.snsscrapboard.dao.RelationPostByMapDao;
import com.postvue.feelogserver.global.constant.MapConst;

import org.springframework.data.repository.query.Param;

public interface SnsPostRepository extends JpaRepository<SnsPost, Long>,JpaSpecificationExecutor<SnsPost> {
	@Query(value = "SELECT SNS_P FROM SnsPost AS SNS_P ORDER BY SNS_P.createdAt")
	List<SnsPost> findAllPost(Pageable pageable);

	String FOLLOW_RELATION_NATIVE_QUERY = "follow_relations as (SELECT SNS_F.following_id, SNS_F.follower_id FROM sns_user_follows_tb AS SNS_F where SNS_F.follower_id = :snsUserId)";
	String SNS_POST_DAO_COLUMN_NATIVE_QUERY =
		"SELECT sns_post.sns_post_id AS post_id, SNS_U.profile_path AS profile_path, "
			+ "COALESCE(SPUR.is_liked,false) AS is_liked, COALESCE(SPUR.is_reposted,false) AS is_reposted, COALESCE(SPUR.is_clipped,false) AS is_clipped, "
			+ "sns_post.created_at AS posted_at, "
			+ "latitude, longitude, address, post_title, post_body_text, (CASE WHEN sns_post.sns_user_id = :snsUserId THEN false ELSE true END) AS followable, "
			+ "sns_post_contents, tags, tgt_aud_type, sns_post.sns_user_id AS sns_user_id, SNS_U.username AS username, FOLLOW.following_id as following_id ";

	String SHOW_BY_NOT_BLOCKED_USER_NATIVE_QUERY = "LEFT OUTER JOIN sns_block_users_tb AS SNS_BU "
		+ "ON (SNS_BU.sns_blocker_user_id = :snsUserId AND SNS_BU.sns_blocked_user_id = sns_post.sns_user_id) OR (SNS_BU.sns_blocker_user_id = sns_post.sns_user_id AND SNS_BU.sns_blocked_user_id = :snsUserId) ";

	String POST_QUERY_WHERE_CONDITION_DETAIL = " "
		+ SHOW_BY_NOT_BLOCKED_USER_NATIVE_QUERY
		+ "WHERE "
		+ "SNS_BU.sns_blocker_user_id IS NULL "
		+ "AND sns_post.deleted_at IS NULL "
		+ "AND SNS_U.deleted_at IS NULL "
		+ "AND "
		+ "(sns_post.sns_user_id = :snsUserId "
		+ "OR (sns_post.tgt_aud_type = " + "'" + TgtAudTypeValue.PUBLIC_SCOPE_VALUE + "'" + " ) "
		+ "OR (sns_post.tgt_aud_type = " + "'" + TgtAudTypeValue.FOLLOWERS_SCOPE_VALUE + "'" + " AND :snsUserId IN (SELECT SNS_UF.follower_id FROM sns_user_follows_tb AS SNS_UF WHERE sns_post.sns_user_id = SNS_UF.following_id) )"
		+ ") ";

	String POST_QUERY_WHERE_CONDITION = " "
		+ SHOW_BY_NOT_BLOCKED_USER_NATIVE_QUERY
		+ "WHERE "
		+ "SNS_BU.sns_blocker_user_id IS NULL "
		+ "AND (SPUR.is_shown IS TRUE OR SPUR.is_shown IS NULL) "
		+ "AND sns_post.deleted_at IS NULL "
		+ "AND SNS_U.deleted_at IS NULL "
		+ "AND "
		+ "(sns_post.sns_user_id = :snsUserId "
		+ "OR (sns_post.tgt_aud_type = " + "'" + TgtAudTypeValue.PUBLIC_SCOPE_VALUE + "'" + " ) "
		+ "OR (sns_post.tgt_aud_type = " + "'" + TgtAudTypeValue.FOLLOWERS_SCOPE_VALUE + "'" + " AND :snsUserId IN (SELECT SNS_UF.follower_id FROM sns_user_follows_tb AS SNS_UF WHERE sns_post.sns_user_id = SNS_UF.following_id) )"
		+ ") ";


	String STUFF_FOR_ME_BY_TAG_NATIVE_QUERY = "WITH "
		+ "follow_relations as (SELECT SNS_F.following_id, SNS_F.follower_id FROM sns_user_follows_tb AS SNS_F where SNS_F.follower_id = :snsUserId), "
		+ "interest_scores AS (SELECT sns_post_id, (CASE WHEN is_clipped = TRUE THEN 5 ELSE 0 END) + (CASE WHEN is_liked = TRUE THEN 4 ELSE 0 END) AS interest_score FROM sns_post_user_reactions_tb WHERE sns_user_id = :snsUserId), "
		+ "tag_interest AS (SELECT SNS_TP.sns_tag_id, SUM(INTER_S.interest_score) AS total_interest_score FROM interest_scores AS INTER_S INNER JOIN sns_tag_posts_tb AS SNS_TP ON INTER_S.sns_post_id = SNS_TP.sns_post_id GROUP BY SNS_TP.sns_tag_id),"
		+ "tag_follow_counts AS ( SELECT STF.sns_tag_id, COUNT(STF.sns_user_id) AS follow_count FROM sns_tag_follows_tb AS STF GROUP BY STF.sns_tag_id), "
		+ "total_tag_interest_scores AS (SELECT (CASE WHEN TI.sns_tag_id IS NOT NULL THEN TI.sns_tag_id ELSE TFC.sns_tag_id END) AS sns_tag_id, COALESCE(TI.total_interest_score,0) + COALESCE(TFC.follow_count, 0) * 4 AS adjusted_interest_score FROM tag_interest TI FULL OUTER JOIN tag_follow_counts AS TFC ON TI.sns_tag_id = TFC.sns_tag_id), "
		+ "post_by_interest_scores AS (SELECT STP.sns_post_id, SUM(T_TAG_INTER_S.adjusted_interest_score) AS total_score FROM sns_tag_posts_tb AS STP INNER JOIN total_tag_interest_scores AS T_TAG_INTER_S ON T_TAG_INTER_S.sns_tag_id = STP.sns_tag_id GROUP BY STP.sns_post_id ORDER BY total_score DESC LIMIT :pageSize OFFSET :page) "
		+ "SELECT "
		+ "sns_post.sns_post_id as cursorId, sns_post.sns_post_id AS post_id, SNS_U.profile_path AS profile_path, (CASE WHEN sns_post.sns_user_id = :snsUserId THEN false ELSE true END) AS followable, "
		+ "COALESCE(SPUR.is_liked,false) AS is_liked, COALESCE(SPUR.is_reposted,false) AS is_reposted, COALESCE(SPUR.is_clipped,false) AS is_clipped, "
		+ "sns_post.created_at AS posted_at, "
		+ "latitude, longitude, address, build_name, post_title, post_body_text, "
		+ "sns_post_contents, tags, tgt_aud_type, sns_post.sns_user_id AS sns_user_id, SNS_U.username AS username, FOLLOW.following_id as following_id, "
		+ "(CASE WHEN SNS_BU.sns_blocker_user_id IS NOT NULL THEN TRUE ELSE FALSE END) AS is_blocked "
		+ "from sns_posts_tb AS sns_post "
		+ "INNER JOIN post_by_interest_scores AS PBIS ON sns_post.sns_post_id = PBIS.sns_post_id "
		+ "INNER JOIN sns_users_tb AS SNS_U ON sns_post.sns_user_id = SNS_U.sns_user_id "
		+ "LEFT OUTER JOIN follow_relations AS FOLLOW "
		+ "ON sns_post.sns_user_id = FOLLOW.following_id "
		+ "LEFT OUTER JOIN sns_post_user_reactions_tb AS SPUR "
		+ "ON SPUR.sns_user_id = :snsUserId AND sns_post.sns_post_id = SPUR.sns_post_id "
		// + SHOW_BY_NOT_BLOCKED_USER_NATIVE_QUERY
		// + "WHERE "
		// + "(SPUR.is_shown IS TRUE OR SPUR.is_shown IS NULL) "
		// + "AND SNS_BU.sns_blocker_user_id IS NULL "
		// + "AND sns_post.deleted_at IS NULL "
		// + "AND "
		// + "(sns_post.sns_user_id = :snsUserId "
		// + "OR (sns_post.tgt_aud_type = " + "'" + TgtAudTypeValue.PUBLIC_SCOPE_VALUE + "'" + " ) "
		// + "OR (sns_post.tgt_aud_type = " + "'" + TgtAudTypeValue.FOLLOWERS_SCOPE_VALUE + "'" + " AND :snsUserId IN (SELECT SNS_UF.follower_id FROM sns_user_follows_tb AS SNS_UF WHERE sns_post.sns_user_id = SNS_UF.following_id) )"
		// + ")"
		+ POST_QUERY_WHERE_CONDITION
		;

	String STUFF_FOR_ME_BY_FOLLOW_NATIVE_QUERY = "WITH "
		+ "follow_relations as (SELECT SNS_F.following_id, SNS_F.follower_id FROM sns_user_follows_tb AS SNS_F where SNS_F.follower_id = :snsUserId) "
		+ "SELECT sns_post.sns_post_id as cursorId, sns_post.sns_post_id AS post_id, SNS_U.profile_path AS profile_path, "
		+ "COALESCE(SPUR.is_liked,false) AS is_liked, COALESCE(SPUR.is_reposted,false) AS is_reposted, COALESCE(SPUR.is_clipped,false) AS is_clipped, "
		+ "sns_post.created_at AS posted_at, "
		+ "latitude, longitude, address, build_name, post_title, post_body_text, (CASE WHEN sns_post.sns_user_id = :snsUserId THEN false ELSE true END) AS followable, "
		+ "sns_post_contents, tags, tgt_aud_type, sns_post.sns_user_id AS sns_user_id, SNS_U.username AS username, FOLLOW.following_id as following_id "
		+ "from sns_posts_tb AS sns_post "
		+ "INNER JOIN follow_relations AS FOLLOW "
		+ "ON sns_post.sns_user_id = FOLLOW.following_id "
		+ "INNER JOIN sns_users_tb AS SNS_U ON sns_post.sns_user_id = SNS_U.sns_user_id "
		+ "LEFT OUTER JOIN sns_post_user_reactions_tb AS SPUR "
		+ "ON SPUR.sns_user_id = :snsUserId AND sns_post.sns_post_id = SPUR.sns_post_id "
		// + SHOW_BY_NOT_BLOCKED_USER_NATIVE_QUERY
		// + "WHERE "
		// + "(SPUR.is_shown IS TRUE OR SPUR.is_shown IS NULL) "
		// + "AND SNS_BU.sns_blocker_user_id IS NULL "
		// + "AND sns_post.deleted_at IS NULL "
		// + "AND "
		// + "(sns_post.sns_user_id = :snsUserId "
		// + "OR (sns_post.tgt_aud_type = " + "'" + TgtAudTypeValue.PUBLIC_SCOPE_VALUE + "'" + " ) "
		// + "OR (sns_post.tgt_aud_type = " + "'" + TgtAudTypeValue.FOLLOWERS_SCOPE_VALUE + "'" + " AND :snsUserId IN (SELECT SNS_UF.follower_id FROM sns_user_follows_tb AS SNS_UF WHERE sns_post.sns_user_id = SNS_UF.following_id) )"
		// + ") "
		+ POST_QUERY_WHERE_CONDITION
		+ "AND sns_post.sns_post_id < :snsPostId "
		+ "ORDER BY sns_post.sns_post_id DESC LIMIT :pageSize";


	int SCORE_CONTROL_NUM = 100;
	String STUFF_FOR_ME_BY_POPULAR_NATIVE_QUERY = "with "
		+ "follow_relations as (SELECT SNS_F.following_id, SNS_F.follower_id FROM sns_user_follows_tb AS SNS_F where SNS_F.follower_id = :snsUserId), "
		+ "sns_posts_by_popular AS "
		+ "(SELECT SNS_POST.sns_post_id, "
		+ "(ABS(sns_post.reaction_count) / POWER(((EXTRACT(EPOCH FROM :currentDateTime - created_at) / 3600) + " + SCORE_CONTROL_NUM + "), 1.1)) AS score "
		+ "FROM sns_posts_tb AS SNS_POST ORDER BY score DESC LIMIT :pageSize offset :page) "
		+ "SELECT sns_post.sns_post_id as cursorId, sns_post.sns_post_id AS post_id, SNS_U.profile_path AS profile_path, "
		+ "COALESCE(SPUR.is_liked,false) AS is_liked, COALESCE(SPUR.is_reposted,false) AS is_reposted, COALESCE(SPUR.is_clipped,false) AS is_clipped, "
		+ "sns_post.created_at AS posted_at, "
		+ "latitude, longitude, address, build_name, post_title, post_body_text, (CASE WHEN sns_post.sns_user_id = :snsUserId THEN false ELSE true END) AS followable, "
		+ "sns_post_contents, tags, tgt_aud_type, sns_post.sns_user_id AS sns_user_id, SNS_U.username AS username, FOLLOW.following_id as following_id "
		+ "from sns_posts_tb AS sns_post "
		+ "INNER JOIN sns_posts_by_popular AS SPBP on SPBP.sns_post_id = sns_post.sns_post_id "
		+ "INNER JOIN sns_users_tb AS SNS_U ON sns_post.sns_user_id = SNS_U.sns_user_id "
		+ "LEFT OUTER JOIN follow_relations AS FOLLOW "
		+ "ON sns_post.sns_user_id = FOLLOW.following_id "
		+ "LEFT OUTER JOIN sns_post_user_reactions_tb AS SPUR "
		+ "ON SPUR.sns_user_id = :snsUserId AND sns_post.sns_post_id = SPUR.sns_post_id "
		// + SHOW_BY_NOT_BLOCKED_USER_NATIVE_QUERY
		// + "WHERE (SPUR.is_shown IS TRUE OR SPUR.is_shown IS NULL) "
		// + "AND SNS_BU.sns_blocker_user_id IS NULL "
		// + "AND sns_post.deleted_at IS NULL "
		// + "AND "
		// + "(sns_post.sns_user_id = :snsUserId "
		// + "OR (sns_post.tgt_aud_type = " + "'" + TgtAudTypeValue.PUBLIC_SCOPE_VALUE + "'" + " ) "
		// + "OR (sns_post.tgt_aud_type = " + "'" + TgtAudTypeValue.FOLLOWERS_SCOPE_VALUE + "'" + " AND :snsUserId IN (SELECT SNS_UF.follower_id FROM sns_user_follows_tb AS SNS_UF WHERE sns_post.sns_user_id = SNS_UF.following_id) )"
		// + ")"
		+ POST_QUERY_WHERE_CONDITION
		;

	String TAG_FOR_ME_NATIVE_QUERY = "with "
		+ "follow_relations as (SELECT SNS_F.following_id, SNS_F.follower_id FROM sns_user_follows_tb AS SNS_F where SNS_F.follower_id = :snsUserId), "
		+ "my_follow_tags AS (SELECT SNS_TAG_F.sns_tag_id FROM sns_tag_follows_tb SNS_TAG_F WHERE SNS_TAG_F.sns_user_id = :snsUserId), "
		+ "tag_post AS (SELECT DISTINCT sns_tag_post.sns_post_id FROM sns_tag_posts_tb sns_tag_post INNER JOIN my_follow_tags AS STF ON sns_tag_post.sns_tag_id = STF.sns_tag_id WHERE sns_tag_post.sns_post_id < :snsPostId LIMIT :pageSize) "
		+ "SELECT sns_post.sns_post_id as cursorId, sns_post.sns_post_id AS post_id, SNS_U.profile_path AS profile_path, "
		+ "COALESCE(SPUR.is_liked,false) AS is_liked, COALESCE(SPUR.is_reposted,false) AS is_reposted, COALESCE(SPUR.is_clipped,false) AS is_clipped, "
		+ "sns_post.created_at AS posted_at, "
		+ "latitude, longitude, address, build_name, post_title, post_body_text, (CASE WHEN sns_post.sns_user_id = :snsUserId THEN false ELSE true END) AS followable, "
		+ "sns_post_contents, tags, tgt_aud_type, sns_post.sns_user_id AS sns_user_id, SNS_U.username AS username, FOLLOW.following_id as following_id "
		+ "from sns_posts_tb AS sns_post "
		+ "INNER JOIN tag_post ON sns_post.sns_post_id = tag_post.sns_post_id "
		+ "INNER JOIN sns_users_tb AS SNS_U ON sns_post.sns_user_id = SNS_U.sns_user_id "
		+ "LEFT OUTER JOIN follow_relations AS FOLLOW "
		+ "ON sns_post.sns_user_id = FOLLOW.following_id "
		+ "LEFT OUTER JOIN sns_post_user_reactions_tb AS SPUR "
		+ "ON SPUR.sns_user_id = :snsUserId AND sns_post.sns_post_id = SPUR.sns_post_id "
		// + SHOW_BY_NOT_BLOCKED_USER_NATIVE_QUERY
		// + "WHERE SNS_BU.sns_blocker_user_id IS NULL "
		// + "AND sns_post.deleted_at IS NULL "
		// + "AND "
		// + "(sns_post.sns_user_id = :snsUserId "
		// + "OR (sns_post.tgt_aud_type = " + "'" + TgtAudTypeValue.PUBLIC_SCOPE_VALUE + "'" + " ) "
		// + "OR (sns_post.tgt_aud_type = " + "'" + TgtAudTypeValue.FOLLOWERS_SCOPE_VALUE + "'" + " AND :snsUserId IN (SELECT SNS_UF.follower_id FROM sns_user_follows_tb AS SNS_UF WHERE sns_post.sns_user_id = SNS_UF.following_id) )"
		// + ")"
		+ POST_QUERY_WHERE_CONDITION
		;

	String MAP_POST_BY_ME_NATIVE_QUERY = "SELECT  "
		+ "        sns_post.sns_post_id as cursorId, "
		+ "        sns_post.sns_post_id AS post_id,  "
		+ "        SNS_U.profile_path AS profile_path,  "
		+ "        COALESCE(SPUR.is_liked,false) AS is_liked,  "
		+ "        COALESCE(SPUR.is_reposted,false) AS is_reposted,  "
		+ "        COALESCE(SPUR.is_clipped,false) AS is_clipped,  "
		+ "        sns_post.created_at AS posted_at,  "
		+ "        latitude, longitude, address, build_name, post_title, post_body_text,  "
		+ "        FALSE AS followable,  "
		+ "        sns_post_contents, tags, tgt_aud_type, sns_post.sns_user_id AS sns_user_id,  "
		+ "        SNS_U.username AS username, "
		+ "		   null as following_id  "
		+ "    FROM sns_posts_tb AS sns_post  "
		+ "    INNER JOIN sns_users_tb AS SNS_U ON sns_post.sns_user_id = SNS_U.sns_user_id  "
		+ "    LEFT OUTER JOIN sns_post_user_reactions_tb AS SPUR  "
		+ "        ON SPUR.sns_user_id = :snsUserId "
		+ "        AND sns_post.sns_post_id = SPUR.sns_post_id  "
		+ "    WHERE (SPUR.is_shown IS TRUE OR SPUR.is_shown IS NULL)  "
		+ "      AND sns_post.sns_user_id = :snsUserId "
		+ "      AND (latitude IS NOT NULL AND longitude IS NOT NULL) "
		+ "		 AND sns_post.deleted_at IS NULL "
		+ "		 AND "
		+ "		 (sns_post.sns_user_id = :snsUserId "
		+ "			OR (sns_post.tgt_aud_type = " + "'" + TgtAudTypeValue.PUBLIC_SCOPE_VALUE + "'" + " ) "
		+ "			OR (sns_post.tgt_aud_type = " + "'" + TgtAudTypeValue.FOLLOWERS_SCOPE_VALUE + "'" + " AND :snsUserId IN (SELECT SNS_UF.follower_id FROM sns_user_follows_tb AS SNS_UF WHERE sns_post.sns_user_id = SNS_UF.following_id) )"
		+ "		 )"
		+ "ORDER BY posted_at DESC "
		+ "LIMIT :pageSize OFFSET :page";
	String NEAR_FOR_ME_NATIVE_QUERY = "WITH "
		+ "follow_relations AS ( "
		+ "    SELECT SNS_F.following_id, SNS_F.follower_id  "
		+ "    FROM sns_user_follows_tb AS SNS_F  "
		+ "    WHERE SNS_F.follower_id = :snsUserId "
		+ "), "
		+ "sns_posts_by_popular AS ("
		+ "SELECT SNS_POST.sns_post_id,"
		+ "(ABS(sns_post.reaction_count) / POWER(((EXTRACT(EPOCH FROM :currentDateTime - created_at) / 3600) + " + SCORE_CONTROL_NUM + "), 1.1)) AS rating "
		+ "FROM sns_posts_tb AS SNS_POST"
		+ ") "
		+ "SELECT * FROM ( "
		+ "    SELECT  "
		+ "        sns_post.sns_post_id as cursorId, "
		+ "        sns_post.sns_post_id AS post_id,  "
		+ "        SNS_U.profile_path AS profile_path,  "
		+ "        COALESCE(SPUR.is_liked,false) AS is_liked,  "
		+ "        COALESCE(SPUR.is_reposted,false) AS is_reposted,  "
		+ "        COALESCE(SPUR.is_clipped,false) AS is_clipped,  "
		+ "        sns_post.created_at AS posted_at,  "
		+ "        latitude, longitude, address,build_name, post_title, post_body_text,  "
		+ "        (CASE WHEN sns_post.sns_user_id = :snsUserId THEN false ELSE true END) AS followable,  "
		+ "        sns_post_contents, tags, tgt_aud_type, sns_post.sns_user_id AS sns_user_id,  "
		+ "        SNS_U.username AS username, FOLLOW.following_id as following_id,  "
		+ "        ST_DistanceSphere(geom, ST_SetSRID(ST_MakePoint(:longitude, :latitude), " + MapConst.MAP_COORDINATE_SYSTEM + " )) AS distance, "
		+ "		   h3_index as h3_index, "
		+ "        SNS_PP.rating AS rating"
		+ "    FROM sns_posts_tb AS sns_post  "
		+ "	   INNER JOIN sns_posts_by_popular SNS_PP ON SNS_PP.sns_post_id = sns_post.sns_post_id "
		+ "    INNER JOIN sns_users_tb AS SNS_U ON sns_post.sns_user_id = SNS_U.sns_user_id  "
		+ "    LEFT OUTER JOIN follow_relations AS FOLLOW ON sns_post.sns_user_id = FOLLOW.following_id  "
		+ "    LEFT OUTER JOIN sns_post_user_reactions_tb AS SPUR  "
		+ "        ON SPUR.sns_user_id = :snsUserId "
		+ "        AND sns_post.sns_post_id = SPUR.sns_post_id  "
		// + SHOW_BY_NOT_BLOCKED_USER_NATIVE_QUERY
		// + "    WHERE (SPUR.is_shown IS TRUE OR SPUR.is_shown IS NULL)  "
		// + "      AND (SNS_BU.sns_blocker_user_id IS NULL) "
		// + "		 AND sns_post.deleted_at IS NULL "
		// + "		 AND "
		// + "		 (sns_post.sns_user_id = :snsUserId "
		// + "			OR (sns_post.tgt_aud_type = " + "'" + TgtAudTypeValue.PUBLIC_SCOPE_VALUE + "'" + " ) "
		// + "			OR (sns_post.tgt_aud_type = " + "'" + TgtAudTypeValue.FOLLOWERS_SCOPE_VALUE + "'" + " AND :snsUserId IN (SELECT SNS_UF.follower_id FROM sns_user_follows_tb AS SNS_UF WHERE sns_post.sns_user_id = SNS_UF.following_id) )"
		// + "		 )"
		+ POST_QUERY_WHERE_CONDITION
		+ ") AS subquery "
		+ "WHERE h3_index IN :h3IndexList "
		+ "AND posted_at >= :startDate AND posted_at <= :endDate "
		+ "ORDER BY distance ASC, rating DESC "
		+ "LIMIT :pageSize OFFSET :page";

	String NEAR_FOR_ME_NATIVE_QUERY_BY = "WITH "
		+ "follow_relations AS ( "
		+ "    SELECT SNS_F.following_id, SNS_F.follower_id  "
		+ "    FROM sns_user_follows_tb AS SNS_F  "
		+ "    WHERE SNS_F.follower_id = :snsUserId "
		+ "), "
		+ "SNS_POSTS_BY_POPULAR AS (SELECT SNS_POST.sns_post_id,"
		+ "(ABS(sns_post.reaction_count) / POWER(((EXTRACT(EPOCH FROM :currentDateTime - created_at) / 3600) + " + SCORE_CONTROL_NUM + "), 1.1)) AS rating "
		+ "FROM sns_posts_tb AS SNS_POST) "
		+ "SELECT * FROM ( "
		+ "    SELECT  "
		+ "        sns_post.sns_post_id as cursorId, "
		+ "        sns_post.sns_post_id AS post_id,  "
		+ "        SNS_U.profile_path AS profile_path,  "
		+ "        COALESCE(SPUR.is_liked,false) AS is_liked,  "
		+ "        COALESCE(SPUR.is_reposted,false) AS is_reposted,  "
		+ "        COALESCE(SPUR.is_clipped,false) AS is_clipped,  "
		+ "        sns_post.created_at AS posted_at,  "
		+ "        latitude, longitude, address,build_name, post_title, post_body_text,  "
		+ "        (CASE WHEN sns_post.sns_user_id = :snsUserId THEN false ELSE true END) AS followable,  "
		+ "        sns_post_contents, tags, tgt_aud_type, sns_post.sns_user_id AS sns_user_id,  "
		+ "        SNS_U.username AS username, FOLLOW.following_id as following_id,  "
		+ "        ST_DistanceSphere(geom, ST_SetSRID(ST_MakePoint(:longitude, :latitude), " + MapConst.MAP_COORDINATE_SYSTEM + " )) AS distance, "
		+ "		   h3_index as h3_index, "
		+ "        SNS_PP.rating AS rating"
		+ "    FROM sns_posts_tb AS sns_post  "
		+ "	   INNER JOIN SNS_POSTS_BY_POPULAR AS SNS_PP ON SNS_POST.sns_post_id = SNS_PP.sns_post_id "
		+ "    INNER JOIN sns_users_tb AS SNS_U ON sns_post.sns_user_id = SNS_U.sns_user_id  "
		+ "    LEFT OUTER JOIN follow_relations AS FOLLOW ON sns_post.sns_user_id = FOLLOW.following_id  "
		+ "    LEFT OUTER JOIN sns_post_user_reactions_tb AS SPUR  "
		+ "        ON SPUR.sns_user_id = :snsUserId "
		+ "        AND sns_post.sns_post_id = SPUR.sns_post_id  "
		+ POST_QUERY_WHERE_CONDITION
		+ "AND sns_post.post_content_business_type = :postContentBusinessType "
		+ ") AS subquery  "
		+ "WHERE h3_index IN :h3IndexList "
		+ "AND posted_at >= :startDate AND posted_at <= :endDate "
		+ "ORDER BY distance ASC, rating DESC  "
		+ "LIMIT :pageSize OFFSET :page";

	String PROFILE_POSTS_QUERY = "with "
		+ "follow_relations as (SELECT SNS_F.following_id, SNS_F.follower_id FROM sns_user_follows_tb AS SNS_F where SNS_F.follower_id = :snsUserId) "
		+ "SELECT sns_post.sns_post_id as cursorId, sns_post.sns_post_id AS post_id, SNS_U.profile_path AS profile_path, "
		+ "COALESCE(SPUR.is_liked,false) AS is_liked, COALESCE(SPUR.is_reposted,false) AS is_reposted, COALESCE(SPUR.is_clipped,false) AS is_clipped, "
		+ "sns_post.created_at AS posted_at, "
		+ "latitude, longitude, address,build_name, post_title, post_body_text, (CASE WHEN sns_post.sns_user_id = :snsUserId THEN false ELSE true END) AS followable, "
		+ "sns_post_contents, tags, tgt_aud_type, sns_post.sns_user_id AS sns_user_id, SNS_U.username AS username, FOLLOW.following_id as following_id "
		+ "from sns_posts_tb AS sns_post "
		+ "INNER JOIN sns_users_tb AS SNS_U ON sns_post.sns_user_id = SNS_U.sns_user_id AND LOWER(SNS_U.username) = LOWER(:username) "
		+ "LEFT OUTER JOIN follow_relations AS FOLLOW "
		+ "ON sns_post.sns_user_id = FOLLOW.following_id "
		+ "LEFT OUTER JOIN sns_post_user_reactions_tb AS SPUR "
		+ "ON SPUR.sns_user_id = :snsUserId AND sns_post.sns_post_id = SPUR.sns_post_id "
		// + SHOW_BY_NOT_BLOCKED_USER_NATIVE_QUERY
		// + "WHERE "
		// + "SNS_BU.sns_blocker_user_id IS NULL "
		// + "AND sns_post.deleted_at IS NULL "
		// + "AND "
		// + "(sns_post.sns_user_id = :snsUserId "
		// + "OR (sns_post.tgt_aud_type = " + "'" + TgtAudTypeValue.PUBLIC_SCOPE_VALUE + "'" + " ) "
		// + "OR (sns_post.tgt_aud_type = " + "'" + TgtAudTypeValue.FOLLOWERS_SCOPE_VALUE + "'" + " AND :snsUserId IN (SELECT SNS_UF.follower_id FROM sns_user_follows_tb AS SNS_UF WHERE sns_post.sns_user_id = SNS_UF.following_id) )"
		// + ")"
		+ POST_QUERY_WHERE_CONDITION
		+ "AND sns_post.sns_post_id < :snsPostId "
		+ "ORDER BY sns_post.sns_post_id DESC LIMIT :pageSize";


	String DETAIL_POST_NATIVE_QUERY = "WITH "
		+ "follow_relations as (SELECT SNS_F.following_id, SNS_F.follower_id FROM sns_user_follows_tb AS SNS_F where SNS_F.follower_id = :snsUserId) "
		+ "SELECT sns_post.sns_post_id as cursorId, sns_post.sns_post_id AS post_id, SNS_U.profile_path AS profile_path, "
		+ "COALESCE(SPUR.is_liked,false) AS is_liked, "
		+ "COALESCE(SPUR.is_reposted,false) AS is_reposted, "
		+ "COALESCE(SPUR.is_clipped,false) AS is_clipped, "
		+ "sns_post.created_at AS posted_at, "
		+ "latitude, longitude, address,build_name, post_title, post_body_text, "
		+ "(CASE WHEN sns_post.sns_user_id = :snsUserId THEN false ELSE true END) AS followable, "
		+ "sns_post_contents, tags, tgt_aud_type, sns_post.sns_user_id AS sns_user_id, SNS_U.username AS username, FOLLOW.following_id as following_id "
		+ "from sns_posts_tb AS sns_post "
		+ "INNER JOIN sns_users_tb AS SNS_U ON sns_post.sns_user_id = SNS_U.sns_user_id "
		+ "LEFT OUTER JOIN follow_relations AS FOLLOW "
		+ "ON sns_post.sns_user_id = FOLLOW.following_id "
		+ "LEFT OUTER JOIN sns_post_user_reactions_tb AS SPUR "
		+ "ON SPUR.sns_user_id = :snsUserId AND sns_post.sns_post_id = SPUR.sns_post_id "
		+ POST_QUERY_WHERE_CONDITION_DETAIL
		+ "AND sns_post.sns_post_id = :snsPostId ";

	String POST_INFO_NATIVE_QUERY = "SELECT sns_post.sns_post_id AS post_id, "
		+ "sns_post.created_at AS posted_at, "
		+ "latitude, longitude, address, build_name, "
		+ "post_title, post_body_text, tgt_aud_type, "
		+ "sns_post_contents, tags, tgt_aud_type, "
		+ "sns_post.sns_user_id AS sns_user_id, "
		+ "SNS_U.username AS username "
		+ "from sns_posts_tb AS sns_post "
		+ "INNER JOIN sns_users_tb AS SNS_U ON sns_post.sns_user_id = SNS_U.sns_user_id "
		+ "WHERE "
		+ "sns_post.deleted_at IS NULL "
		+ "AND SNS_U.deleted_at IS NULL "
		+ "AND sns_post.sns_user_id = :snsUserId "
		+ "AND sns_post.sns_post_id = :snsPostId"
		;

	String POPULAR_POST_SEARCH_QUERY_NATIVE_QUERY = "with  "
		+ "follow_relations as (SELECT SNS_F.following_id, SNS_F.follower_id FROM sns_user_follows_tb AS SNS_F where SNS_F.follower_id = :snsUserId),  "
		+ "tag_by_srch_query AS (SELECT SNS_T.sns_tag_id FROM SNS_TAGS_TB AS SNS_T WHERE tag_name @@ :searchQuery),  "
		+ "post_by_tag AS (SELECT DISTINCT sns_tag_post.sns_post_id FROM sns_tag_posts_tb sns_tag_post INNER JOIN tag_by_srch_query AS TAG_SQ ON sns_tag_post.sns_tag_id = TAG_SQ.sns_tag_id ),  "
		+ "scrap_relation AS (SELECT sns_scrap_board_id FROM SNS_SCRAP_BOARDS_TB WHERE scrap_name @@ :searchQuery), "
		+ "post_by_scrap AS (SELECT sns_post_id FROM SNS_SCRAPS_TB WHERE sns_scrap_board_id IN (SELECT * FROM scrap_relation)), "
		+ "post_by_title_body as (SELECT sns_post_id FROM SNS_POSTS_TB WHERE post_title @@ :searchQuery OR post_body_text @@ :searchQuery OR build_name @@ :searchQuery), "
		+ "post_relation AS (SELECT * FROM post_by_tag UNION SELECT * FROM post_by_scrap UNION SELECT * FROM post_by_title_body), "
		+ "sns_posts_by_popular AS (SELECT SNS_POST.sns_post_id,"
		+ "(ABS(sns_post.reaction_count) / POWER(((EXTRACT(EPOCH FROM :currentDateTime - created_at) / 3600) + " + SCORE_CONTROL_NUM + "), 1.1)) AS rating "
		+ "FROM sns_posts_tb AS SNS_POST) "
		+ "SELECT sns_post.sns_post_id as cursorId, sns_post.sns_post_id AS post_id, SNS_U.profile_path AS profile_path,  "
		+ "COALESCE(SPUR.is_liked,false) AS is_liked, COALESCE(SPUR.is_reposted,false) AS is_reposted, COALESCE(SPUR.is_clipped,false) AS is_clipped,  "
		+ "sns_post.created_at AS posted_at,  "
		+ "latitude, longitude, address, build_name, post_title, post_body_text, (CASE WHEN sns_post.sns_user_id = :snsUserId THEN false ELSE true END) AS followable,  "
		+ "sns_post_contents, tags, tgt_aud_type, sns_post.sns_user_id AS sns_user_id, SNS_U.username AS username, FOLLOW.following_id as following_id  "
		+ "from sns_posts_by_popular AS SPBP "
		+ "INNER JOIN sns_posts_tb AS SNS_POST on SPBP.sns_post_id = sns_post.sns_post_id  "
		+ "INNER JOIN post_relation ON sns_post.sns_post_id = post_relation.sns_post_id  "
		+ "INNER JOIN sns_users_tb AS SNS_U ON sns_post.sns_user_id = SNS_U.sns_user_id  "
		+ "LEFT OUTER JOIN follow_relations AS FOLLOW  "
		+ "ON sns_post.sns_user_id = FOLLOW.following_id  "
		+ "LEFT OUTER JOIN sns_post_user_reactions_tb AS SPUR "
		+ "ON SPUR.sns_user_id = :snsUserId AND sns_post.sns_post_id = SPUR.sns_post_id  "
		+ POST_QUERY_WHERE_CONDITION
		+ "ORDER BY SPBP.rating DESC LIMIT :pageSize offset :page";

	String TAG_POPULAR_POST_SEARCH_QUERY_NATIVE_QUERY = "with  "
		+ "follow_relations as (SELECT SNS_F.following_id, SNS_F.follower_id FROM sns_user_follows_tb AS SNS_F where SNS_F.follower_id = :snsUserId),  "
		+ "tag_by_srch_query AS (SELECT SNS_T.sns_tag_id FROM SNS_TAGS_TB AS SNS_T WHERE tag_name = :searchQuery),  "
		+ "post_by_tag AS (SELECT DISTINCT sns_tag_post.sns_post_id FROM sns_tag_posts_tb sns_tag_post INNER JOIN tag_by_srch_query AS TAG_SQ ON sns_tag_post.sns_tag_id = TAG_SQ.sns_tag_id ),  "
		+ "scrap_relation AS (SELECT sns_scrap_board_id FROM SNS_SCRAP_BOARDS_TB WHERE scrap_name @@ :searchQuery), "
		+ "post_by_scrap AS (SELECT sns_post_id FROM SNS_SCRAPS_TB WHERE sns_scrap_board_id IN (SELECT * FROM scrap_relation)), "
		+ "post_by_title_body as (SELECT sns_post_id FROM SNS_POSTS_TB WHERE post_title @@ :searchQuery OR post_body_text @@ :searchQuery OR build_name @@ :searchQuery), "
		+ "post_relation AS (SELECT * FROM post_by_tag UNION SELECT * FROM post_by_scrap UNION SELECT * FROM post_by_title_body), "
		+ "sns_posts_by_popular AS (SELECT SNS_POST.sns_post_id,"
		+ "(ABS(sns_post.reaction_count) / POWER(((EXTRACT(EPOCH FROM :currentDateTime - created_at) / 3600) + " + SCORE_CONTROL_NUM + "), 1.1)) AS rating "
		+ "FROM sns_posts_tb AS SNS_POST) "
		+ "SELECT sns_post.sns_post_id as cursorId, sns_post.sns_post_id AS post_id, SNS_U.profile_path AS profile_path,  "
		+ "COALESCE(SPUR.is_liked,false) AS is_liked, COALESCE(SPUR.is_reposted,false) AS is_reposted, COALESCE(SPUR.is_clipped,false) AS is_clipped,  "
		+ "sns_post.created_at AS posted_at,  "
		+ "latitude, longitude, address, build_name, post_title, post_body_text, (CASE WHEN sns_post.sns_user_id = :snsUserId THEN false ELSE true END) AS followable,  "
		+ "sns_post_contents, tags, tgt_aud_type, sns_post.sns_user_id AS sns_user_id, SNS_U.username AS username, FOLLOW.following_id as following_id  "
		+ "from sns_posts_by_popular AS SPBP "
		+ "INNER JOIN sns_posts_tb AS SNS_POST on SPBP.sns_post_id = sns_post.sns_post_id  "
		+ "INNER JOIN post_relation ON sns_post.sns_post_id = post_relation.sns_post_id  "
		+ "INNER JOIN sns_users_tb AS SNS_U ON sns_post.sns_user_id = SNS_U.sns_user_id  "
		+ "LEFT OUTER JOIN follow_relations AS FOLLOW  "
		+ "ON sns_post.sns_user_id = FOLLOW.following_id  "
		+ "LEFT OUTER JOIN sns_post_user_reactions_tb AS SPUR "
		+ "ON SPUR.sns_user_id = :snsUserId AND sns_post.sns_post_id = SPUR.sns_post_id  "
		+ POST_QUERY_WHERE_CONDITION
		+ "ORDER BY SPBP.rating DESC LIMIT :pageSize offset :page";

	String RECENTLY_POST_SEARCH_QUERY_NATIVE_QUERY = "with "
		+ "follow_relations as (SELECT SNS_F.following_id, SNS_F.follower_id FROM sns_user_follows_tb AS SNS_F where SNS_F.follower_id = :snsUserId), "
		+ "tag_by_srch_query AS (SELECT SNS_T.sns_tag_id FROM SNS_TAGS_TB AS SNS_T WHERE tag_name @@ :searchQuery), "
		+ "post_by_tag AS (SELECT DISTINCT sns_tag_post.sns_post_id FROM sns_tag_posts_tb sns_tag_post INNER JOIN tag_by_srch_query AS TAG_SQ ON sns_tag_post.sns_tag_id = TAG_SQ.sns_tag_id ),  "
		+ "scrap_relation AS (SELECT sns_scrap_board_id FROM SNS_SCRAP_BOARDS_TB WHERE scrap_name @@ :searchQuery), "
		+ "post_by_scrap AS (SELECT sns_post_id FROM SNS_SCRAPS_TB WHERE sns_scrap_board_id IN (SELECT * FROM scrap_relation)), "
		+ "post_by_title_body as (SELECT sns_post_id FROM SNS_POSTS_TB WHERE post_title @@ :searchQuery OR post_body_text @@ :searchQuery OR build_name @@ :searchQuery), "
		+ "post_relation AS (SELECT * FROM post_by_tag UNION SELECT * FROM post_by_scrap UNION SELECT * FROM post_by_title_body) "
		+ "SELECT sns_post.sns_post_id as cursorId, sns_post.sns_post_id AS post_id, SNS_U.profile_path AS profile_path, "
		+ "COALESCE(SPUR.is_liked,false) AS is_liked, COALESCE(SPUR.is_reposted,false) AS is_reposted, COALESCE(SPUR.is_clipped,false) AS is_clipped, "
		+ "sns_post.created_at AS posted_at, "
		+ "latitude, longitude, address, build_name, post_title, post_body_text, (CASE WHEN sns_post.sns_user_id = :snsUserId THEN false ELSE true END) AS followable, "
		+ "sns_post_contents, tags, tgt_aud_type, sns_post.sns_user_id AS sns_user_id, SNS_U.username AS username, FOLLOW.following_id as following_id "
		+ "from sns_posts_tb AS sns_post "
		+ "INNER JOIN post_relation ON sns_post.sns_post_id = post_relation.sns_post_id "
		+ "INNER JOIN sns_users_tb AS SNS_U ON sns_post.sns_user_id = SNS_U.sns_user_id "
		+ "LEFT OUTER JOIN follow_relations AS FOLLOW "
		+ "ON sns_post.sns_user_id = FOLLOW.following_id "
		+ "LEFT OUTER JOIN sns_post_user_reactions_tb AS SPUR "
		+ "ON SPUR.sns_user_id = :snsUserId AND sns_post.sns_post_id = SPUR.sns_post_id "
		+ POST_QUERY_WHERE_CONDITION
		+ "ORDER BY post_id DESC LIMIT :pageSize offset :page";

	String NEAR_POST_SEARCH_QUERY_NATIVE_QUERY = "with "
		+ "follow_relations as (SELECT SNS_F.following_id, SNS_F.follower_id FROM sns_user_follows_tb AS SNS_F where SNS_F.follower_id = :snsUserId), "
		+ "tag_by_srch_query AS (SELECT SNS_T.sns_tag_id FROM SNS_TAGS_TB AS SNS_T WHERE tag_name @@ :searchQuery), "
		+ "post_by_tag AS (SELECT DISTINCT sns_tag_post.sns_post_id FROM sns_tag_posts_tb sns_tag_post INNER JOIN tag_by_srch_query AS TAG_SQ ON sns_tag_post.sns_tag_id = TAG_SQ.sns_tag_id ),  "
		+ "scrap_relation AS (SELECT sns_scrap_board_id FROM SNS_SCRAP_BOARDS_TB WHERE scrap_name @@ :searchQuery), "
		+ "post_by_scrap AS (SELECT sns_post_id FROM SNS_SCRAPS_TB WHERE sns_scrap_board_id IN (SELECT * FROM scrap_relation)), "
		+ "post_by_title_body as (SELECT sns_post_id FROM SNS_POSTS_TB WHERE post_title @@ :searchQuery OR post_body_text @@ :searchQuery OR build_name @@ :searchQuery OR address @@ :searchQuery), "
		+ "post_relation AS (SELECT * FROM post_by_tag UNION SELECT * FROM post_by_scrap UNION SELECT * FROM post_by_title_body) "
		+ "SELECT sns_post.sns_post_id as cursorId, sns_post.sns_post_id AS post_id, SNS_U.profile_path AS profile_path, "
		+ "COALESCE(SPUR.is_liked,false) AS is_liked, COALESCE(SPUR.is_reposted,false) AS is_reposted, COALESCE(SPUR.is_clipped,false) AS is_clipped, "
		+ "sns_post.created_at AS posted_at, "
		+ "latitude, longitude, address, build_name, post_title, post_body_text, (CASE WHEN sns_post.sns_user_id = :snsUserId THEN false ELSE true END) AS followable, "
		+ "sns_post_contents, tags, tgt_aud_type, sns_post.sns_user_id AS sns_user_id, SNS_U.username AS username, FOLLOW.following_id as following_id, "
		+ "ST_DistanceSphere(geom, ST_SetSRID(ST_MakePoint(:longitude, :latitude), " + MapConst.MAP_COORDINATE_SYSTEM + " )) AS distance, "
		+ "h3_index as h3_index "
		+ "from sns_posts_tb AS sns_post "
		+ "INNER JOIN post_relation ON sns_post.sns_post_id = post_relation.sns_post_id "
		+ "INNER JOIN sns_users_tb AS SNS_U ON sns_post.sns_user_id = SNS_U.sns_user_id "
		+ "LEFT OUTER JOIN follow_relations AS FOLLOW "
		+ "ON sns_post.sns_user_id = FOLLOW.following_id "
		+ "LEFT OUTER JOIN sns_post_user_reactions_tb AS SPUR "
		+ "ON SPUR.sns_user_id = :snsUserId AND sns_post.sns_post_id = SPUR.sns_post_id "
		+ POST_QUERY_WHERE_CONDITION
		+ "AND h3_index IN :h3IndexList "
		+ "ORDER BY distance ASC "
		+ "LIMIT :pageSize offset :page";

	String POST_RELATION_NATIVE_QUERY = "WITH "
		+ "follow_relations as (SELECT SNS_F.following_id, SNS_F.follower_id FROM sns_user_follows_tb AS SNS_F where SNS_F.follower_id = :snsUserId), "
		+ "tag_relation AS (SELECT sns_tag_id FROM SNS_TAGS_TB AS SNS_T WHERE tag_name &@~ :relatedTagListString), "
		+ "post_by_tag AS (SELECT sns_post_id FROM SNS_TAG_POSTS_TB AS SNS_TP WHERE SNS_TP.sns_tag_id IN (SELECT * FROM tag_relation)), "
		+ "scrap_relation AS (SELECT sns_scrap_board_id FROM SNS_SCRAP_BOARDS_TB WHERE scrap_name &@~ :relatedTagListString), "
		+ "post_by_scrap AS (SELECT sns_post_id FROM SNS_SCRAPS_TB WHERE sns_scrap_board_id IN (SELECT * FROM scrap_relation)), "
		+ "post_by_title_body as (SELECT sns_post_id FROM SNS_POSTS_TB WHERE post_title @@ :searchQuery OR post_body_text @@ :searchQuery OR build_name @@ :searchQuery OR address @@ :searchQuery), "
		+ "post_relation AS (SELECT * FROM post_by_tag UNION SELECT * FROM post_by_scrap) "
		+ "SELECT sns_post.sns_post_id as cursorId, sns_post.sns_post_id AS post_id, SNS_U.profile_path AS profile_path,  "
		+ "COALESCE(SPUR.is_liked,false) AS is_liked, COALESCE(SPUR.is_reposted,false) AS is_reposted, COALESCE(SPUR.is_clipped,false) AS is_clipped,  "
		+ "sns_post.created_at AS posted_at,  "
		+ "latitude, longitude, address, build_name, post_title, post_body_text, (CASE WHEN sns_post.sns_user_id = :snsUserId THEN false ELSE true END) AS followable,  "
		+ "sns_post_contents, tags, tgt_aud_type, sns_post.sns_user_id AS sns_user_id, SNS_U.username AS username, FOLLOW.following_id as following_id  "
		+ "from sns_posts_tb AS sns_post  "
		+ "INNER JOIN post_relation ON sns_post.sns_post_id = post_relation.sns_post_id  "
		+ "INNER JOIN sns_users_tb AS SNS_U ON sns_post.sns_user_id = SNS_U.sns_user_id  "
		+ "LEFT OUTER JOIN follow_relations AS FOLLOW ON sns_post.sns_user_id = FOLLOW.following_id  "
		+ "LEFT OUTER JOIN sns_post_user_reactions_tb AS SPUR "
		+ "ON SPUR.sns_user_id = :snsUserId "
		+ "AND sns_post.sns_post_id = SPUR.sns_post_id "
		// + SHOW_BY_NOT_BLOCKED_USER_NATIVE_QUERY
		// + "WHERE (SPUR.is_shown IS TRUE OR SPUR.is_shown IS NULL) "
		// + "AND SNS_BU.sns_blocker_user_id IS NULL "
		// + "AND sns_post.deleted_at IS NULL "
		// + "AND "
		// + "(sns_post.sns_user_id = :snsUserId "
		// + "OR (sns_post.tgt_aud_type = " + "'" + TgtAudTypeValue.PUBLIC_SCOPE_VALUE + "'" + " ) "
		// + "OR (sns_post.tgt_aud_type = " + "'" + TgtAudTypeValue.FOLLOWERS_SCOPE_VALUE + "'" + " AND :snsUserId IN (SELECT SNS_UF.follower_id FROM sns_user_follows_tb AS SNS_UF WHERE sns_post.sns_user_id = SNS_UF.following_id) )"
		// + ")"
		+ POST_QUERY_WHERE_CONDITION
		+ "AND sns_post.sns_post_id < :cursorId "
		+ "ORDER BY sns_post.sns_post_id DESC LIMIT :pageSize";

	String POST_RELATION_BY_RECOMM_NATIVE_QUERY = "WITH "
		+ "follow_relations as (SELECT SNS_F.following_id, SNS_F.follower_id FROM sns_user_follows_tb AS SNS_F where SNS_F.follower_id = :snsUserId), "
		+ "post_text_info AS (SELECT * FROM sns_posts_tb WHERE sns_post_id = :snsPostId), "
		+ " "
		+ "tag_relation AS (SELECT sns_tag_id FROM SNS_TAGS_TB AS SNS_T WHERE tag_name &@~ :relatedTagListString), "
		+ "post_by_tag AS (SELECT SNS_P.sns_post_id FROM SNS_TAG_POSTS_TB AS SNS_TP "
		+ "INNER JOIN sns_posts_tb AS SNS_P ON SNS_P.sns_post_id = SNS_TP.sns_post_id "
		+ "WHERE SNS_TP.sns_tag_id IN (SELECT * FROM tag_relation) "
		+ "ORDER BY "
		+ "(ABS(SNS_P.reaction_count) / POWER(((EXTRACT(EPOCH FROM :currentDateTime - SNS_P.created_at) / 3600) + " + SCORE_CONTROL_NUM + "), 1.1)) DESC OFFSET :recommPage LIMIT :recommPageSize), "
		+ " "
		+ "scrap_relation_by_post_id AS (SELECT sns_scrap_board_id FROM SNS_SCRAPS_TB WHERE sns_post_id = :snsPostId), "
		+ "post_by_scrap_by_post_id AS (SELECT sns_post_id FROM sns_scraps_tb AS SNS_S "
		+ "WHERE SNS_S.sns_scrap_board_id IN (SELECT * FROM scrap_relation_by_post_id) "
		+ "ORDER BY sns_post_id DESC OFFSET :livePage LIMIT :livePageSize), "
		+ " "
		+ "scrap_relation AS (SELECT sns_scrap_board_id FROM SNS_SCRAP_BOARDS_TB WHERE scrap_name &@~ :relatedTagListString OR scrap_name &@~ (SELECT build_name FROM post_text_info) OR scrap_name &@~ (SELECT post_title FROM post_text_info) ), "
		+ "post_by_scrap AS (SELECT SNS_P.sns_post_id FROM sns_scraps_tb AS SNS_S INNER JOIN sns_posts_tb AS SNS_P ON SNS_P.sns_post_id = SNS_S.sns_post_id "
		+ "WHERE SNS_S.sns_scrap_board_id IN (SELECT * FROM scrap_relation) "
		+ "ORDER BY "
		+ "(ABS(SNS_P.reaction_count) / POWER(((EXTRACT(EPOCH FROM :currentDateTime - SNS_P.created_at) / 3600) + " + SCORE_CONTROL_NUM + "), 1.1)) DESC OFFSET :recommPage LIMIT :recommPageSize), "
		+ " "
		+ "post_gis_table as (SELECT sns_post_id "
		+ "FROM SNS_POSTS_TB AS SNS_P "
		+ "WHERE SNS_P.h3_index IN :h3IndexList "
		+ "ORDER BY ST_DistanceSphere(geom, ST_SetSRID(ST_MakePoint(:longitude, :latitude), " + MapConst.MAP_COORDINATE_SYSTEM + " )) ASC offset :distancePage limit :distancePageSize), "
		+ " "
		+ "post_relation AS (SELECT * FROM post_by_tag UNION SELECT * FROM post_by_scrap UNION SELECT * FROM post_gis_table UNION SELECT * FROM post_by_scrap_by_post_id) "
		+ " "
		+ "SELECT sns_post.sns_post_id as cursorId, sns_post.sns_post_id AS post_id, SNS_U.profile_path AS profile_path,  "
		+ "COALESCE(SPUR.is_liked,false) AS is_liked, COALESCE(SPUR.is_reposted,false) AS is_reposted, COALESCE(SPUR.is_clipped,false) AS is_clipped,  "
		+ "sns_post.created_at AS posted_at,  "
		+ "latitude, longitude, address, build_name, post_title, post_body_text, (CASE WHEN sns_post.sns_user_id = :snsUserId THEN false ELSE true END) AS followable,  "
		+ "sns_post_contents, tags, tgt_aud_type, sns_post.sns_user_id AS sns_user_id, SNS_U.username AS username, FOLLOW.following_id as following_id  "
		+ "from sns_posts_tb AS sns_post  "
		+ "INNER JOIN post_relation ON sns_post.sns_post_id = post_relation.sns_post_id  "
		+ "INNER JOIN sns_users_tb AS SNS_U ON sns_post.sns_user_id = SNS_U.sns_user_id  "
		+ "LEFT OUTER JOIN follow_relations AS FOLLOW ON sns_post.sns_user_id = FOLLOW.following_id  "
		+ "LEFT OUTER JOIN sns_post_user_reactions_tb AS SPUR "
		+ "ON SPUR.sns_user_id = :snsUserId "
		+ "AND sns_post.sns_post_id = SPUR.sns_post_id "
		+ POST_QUERY_WHERE_CONDITION
		;

	// String POST_RELATION_BY_LIVE_NATIVE_QUERY = "WITH "
	// 	+ "follow_relations as (SELECT SNS_F.following_id, SNS_F.follower_id FROM sns_user_follows_tb AS SNS_F where SNS_F.follower_id = :snsUserId), "
	// 	+ "post_text_info AS (SELECT * FROM sns_posts_tb WHERE sns_post_id = :snsPostId), "
	// 	+ " "
	// 	+ "tag_relation AS (SELECT sns_tag_id FROM SNS_TAGS_TB AS SNS_T WHERE tag_name &@~ :relatedTagListString), "
	// 	+ "post_by_tag AS (SELECT SNS_P.sns_post_id FROM SNS_TAG_POSTS_TB AS SNS_TP "
	// 	+ "INNER JOIN sns_posts_tb AS SNS_P ON SNS_P.sns_post_id = SNS_TP.sns_post_id "
	// 	+ "WHERE SNS_TP.sns_tag_id IN (SELECT * FROM tag_relation) "
	// 	+ "ORDER BY "
	// 	+ "(ABS(SNS_P.reaction_count) / POWER(((EXTRACT(EPOCH FROM :currentDateTime - SNS_P.created_at) / 3600) + " + SCORE_CONTROL_NUM + "), 1.1)) DESC OFFSET :page LIMIT :pageSize), "
	// 	+ " "
	// 	+ "scrap_relation_by_post_id AS (SELECT sns_scrap_board_id FROM SNS_SCRAPS_TB WHERE sns_post_id = :snsPostId), "
	// 	+ "post_by_scrap_by_post_id AS (SELECT sns_post_id FROM sns_scraps_tb AS SNS_S "
	// 	+ "WHERE SNS_S.sns_scrap_board_id IN (SELECT * FROM scrap_relation_by_post_id) "
	// 	+ "ORDER BY sns_post_id DESC OFFSET :page LIMIT :pageSize), "
	// 	+ " "
	// 	+ "scrap_relation AS (SELECT sns_scrap_board_id FROM SNS_SCRAP_BOARDS_TB WHERE scrap_name &@~ :relatedTagListString OR scrap_name &@~ (SELECT build_name FROM post_text_info) OR scrap_name &@~ (SELECT post_title FROM post_text_info) ), "
	// 	+ "post_by_scrap AS (SELECT SNS_P.sns_post_id FROM sns_scraps_tb AS SNS_S INNER JOIN sns_posts_tb AS SNS_P ON SNS_P.sns_post_id = SNS_S.sns_post_id "
	// 	+ "WHERE SNS_S.sns_scrap_board_id IN (SELECT * FROM scrap_relation) "
	// 	+ "ORDER BY "
	// 	+ "(ABS(SNS_P.reaction_count) / POWER(((EXTRACT(EPOCH FROM :currentDateTime - SNS_P.created_at) / 3600) + " + SCORE_CONTROL_NUM + "), 1.1)) DESC OFFSET :page LIMIT :pageSize), "
	// 	+ " "
	// 	+ "post_gis_table as (SELECT sns_post_id "
	// 	+ "FROM SNS_POSTS_TB AS SNS_P "
	// 	+ "WHERE SNS_P.h3_index IN :h3IndexList "
	// 	+ "ORDER BY ST_DistanceSphere(geom, ST_SetSRID(ST_MakePoint(:longitude, :latitude), " + MapConst.MAP_COORDINATE_SYSTEM + " )) ASC offset :page limit :pageSize), "
	// 	+ " "
	// 	+ "post_relation AS (SELECT * FROM post_by_tag UNION SELECT * FROM post_by_scrap UNION SELECT * FROM post_gis_table UNION SELECT * FROM post_by_scrap_by_post_id) "
	// 	+ " "
	// 	+ "SELECT sns_post.sns_post_id as cursorId, sns_post.sns_post_id AS post_id, SNS_U.profile_path AS profile_path,  "
	// 	+ "COALESCE(SPUR.is_liked,false) AS is_liked, COALESCE(SPUR.is_reposted,false) AS is_reposted, COALESCE(SPUR.is_clipped,false) AS is_clipped,  "
	// 	+ "sns_post.created_at AS posted_at,  "
	// 	+ "latitude, longitude, address, build_name, post_title, post_body_text, (CASE WHEN sns_post.sns_user_id = :snsUserId THEN false ELSE true END) AS followable,  "
	// 	+ "sns_post_contents, tags, tgt_aud_type, sns_post.sns_user_id AS sns_user_id, SNS_U.username AS username, FOLLOW.following_id as following_id  "
	// 	+ "from sns_posts_tb AS sns_post  "
	// 	+ "INNER JOIN post_relation ON sns_post.sns_post_id = post_relation.sns_post_id  "
	// 	+ "INNER JOIN sns_users_tb AS SNS_U ON sns_post.sns_user_id = SNS_U.sns_user_id  "
	// 	+ "LEFT OUTER JOIN follow_relations AS FOLLOW ON sns_post.sns_user_id = FOLLOW.following_id  "
	// 	+ "LEFT OUTER JOIN sns_post_user_reactions_tb AS SPUR "
	// 	+ "ON SPUR.sns_user_id = :snsUserId "
	// 	+ "AND sns_post.sns_post_id = SPUR.sns_post_id "
	// 	+ SHOW_BY_NOT_BLOCKED_USER_NATIVE_QUERY
	// 	+ "WHERE (SPUR.is_shown IS TRUE OR SPUR.is_shown IS NULL) "
	// 	+ "AND SNS_BU.sns_blocker_user_id IS NULL "
	// 	+ "AND sns_post.deleted_at IS NULL "
	// 	+ "AND "
	// 	+ "(sns_post.sns_user_id = :snsUserId "
	// 	+ "OR (sns_post.tgt_aud_type = " + "'" + TgtAudTypeValue.PUBLIC_SCOPE_VALUE + "'" + " ) "
	// 	+ "OR (sns_post.tgt_aud_type = " + "'" + TgtAudTypeValue.FOLLOWERS_SCOPE_VALUE + "'" + " AND :snsUserId IN (SELECT SNS_UF.follower_id FROM sns_user_follows_tb AS SNS_UF WHERE sns_post.sns_user_id = SNS_UF.following_id) )"
	// 	+ ")"
	// 	+ "ORDER BY sns_post.sns_post_id DESC LIMIT :pageSize";

	// String POST_RELATION_BY_DISTANCE_NATIVE_QUERY = "WITH "
	// 	+ "follow_relations as (SELECT SNS_F.following_id, SNS_F.follower_id FROM sns_user_follows_tb AS SNS_F where SNS_F.follower_id = :snsUserId), "
	// 	+ "post_text_info AS (SELECT * FROM sns_posts_tb WHERE sns_post_id = :snsPostId), "
	// 	+ " "
	// 	+ "tag_relation AS (SELECT sns_tag_id FROM SNS_TAGS_TB AS SNS_T WHERE tag_name &@~ :relatedTagListString), "
	// 	+ "post_by_tag AS (SELECT SNS_P.sns_post_id FROM SNS_TAG_POSTS_TB AS SNS_TP "
	// 	+ "INNER JOIN sns_posts_tb AS SNS_P ON SNS_P.sns_post_id = SNS_TP.sns_post_id "
	// 	+ "WHERE SNS_TP.sns_tag_id IN (SELECT * FROM tag_relation) "
	// 	+ "ORDER BY "
	// 	+ "(ABS(SNS_P.reaction_count) / POWER(((EXTRACT(EPOCH FROM :currentDateTime - SNS_P.created_at) / 3600) + " + SCORE_CONTROL_NUM + "), 1.1)) DESC OFFSET :page LIMIT :pageSize), "
	// 	+ " "
	// 	+ "scrap_relation_by_post_id AS (SELECT sns_scrap_board_id FROM SNS_SCRAPS_TB WHERE sns_post_id = :snsPostId), "
	// 	+ "post_by_scrap_by_post_id AS (SELECT sns_post_id FROM sns_scraps_tb AS SNS_S "
	// 	+ "WHERE SNS_S.sns_scrap_board_id IN (SELECT * FROM scrap_relation_by_post_id) "
	// 	+ "ORDER BY sns_post_id DESC OFFSET :page LIMIT :pageSize), "
	// 	+ " "
	// 	+ "scrap_relation AS (SELECT sns_scrap_board_id FROM SNS_SCRAP_BOARDS_TB WHERE scrap_name &@~ :relatedTagListString OR scrap_name &@~ (SELECT build_name FROM post_text_info) OR scrap_name &@~ (SELECT post_title FROM post_text_info) ), "
	// 	+ "post_by_scrap AS (SELECT SNS_P.sns_post_id FROM sns_scraps_tb AS SNS_S INNER JOIN sns_posts_tb AS SNS_P ON SNS_P.sns_post_id = SNS_S.sns_post_id "
	// 	+ "WHERE SNS_S.sns_scrap_board_id IN (SELECT * FROM scrap_relation) "
	// 	+ "ORDER BY "
	// 	+ "(ABS(SNS_P.reaction_count) / POWER(((EXTRACT(EPOCH FROM :currentDateTime - SNS_P.created_at) / 3600) + " + SCORE_CONTROL_NUM + "), 1.1)) DESC OFFSET :page LIMIT :pageSize), "
	// 	+ " "
	// 	+ "post_gis_table as (SELECT sns_post_id "
	// 	+ "FROM SNS_POSTS_TB AS SNS_P "
	// 	+ "WHERE SNS_P.h3_index IN :h3IndexList "
	// 	+ "ORDER BY ST_DistanceSphere(geom, ST_SetSRID(ST_MakePoint(:longitude, :latitude), " + MapConst.MAP_COORDINATE_SYSTEM + " )) ASC offset :page limit :pageSize), "
	// 	+ " "
	// 	+ "post_relation AS (SELECT * FROM post_by_tag UNION SELECT * FROM post_by_scrap UNION SELECT * FROM post_gis_table UNION SELECT * FROM post_by_scrap_by_post_id) "
	// 	+ " "
	// 	+ "SELECT sns_post.sns_post_id as cursorId, sns_post.sns_post_id AS post_id, SNS_U.profile_path AS profile_path,  "
	// 	+ "COALESCE(SPUR.is_liked,false) AS is_liked, COALESCE(SPUR.is_reposted,false) AS is_reposted, COALESCE(SPUR.is_clipped,false) AS is_clipped,  "
	// 	+ "sns_post.created_at AS posted_at,  "
	// 	+ "latitude, longitude, address, build_name, post_title, post_body_text, (CASE WHEN sns_post.sns_user_id = :snsUserId THEN false ELSE true END) AS followable,  "
	// 	+ "sns_post_contents, tags, tgt_aud_type, sns_post.sns_user_id AS sns_user_id, SNS_U.username AS username, FOLLOW.following_id as following_id  "
	// 	+ "from sns_posts_tb AS sns_post  "
	// 	+ "INNER JOIN post_relation ON sns_post.sns_post_id = post_relation.sns_post_id  "
	// 	+ "INNER JOIN sns_users_tb AS SNS_U ON sns_post.sns_user_id = SNS_U.sns_user_id  "
	// 	+ "LEFT OUTER JOIN follow_relations AS FOLLOW ON sns_post.sns_user_id = FOLLOW.following_id  "
	// 	+ "LEFT OUTER JOIN sns_post_user_reactions_tb AS SPUR "
	// 	+ "ON SPUR.sns_user_id = :snsUserId "
	// 	+ "AND sns_post.sns_post_id = SPUR.sns_post_id "
	// 	+ SHOW_BY_NOT_BLOCKED_USER_NATIVE_QUERY
	// 	+ "WHERE (SPUR.is_shown IS TRUE OR SPUR.is_shown IS NULL) "
	// 	+ "AND SNS_BU.sns_blocker_user_id IS NULL "
	// 	+ "AND sns_post.deleted_at IS NULL "
	// 	+ "AND "
	// 	+ "(sns_post.sns_user_id = :snsUserId "
	// 	+ "OR (sns_post.tgt_aud_type = " + "'" + TgtAudTypeValue.PUBLIC_SCOPE_VALUE + "'" + " ) "
	// 	+ "OR (sns_post.tgt_aud_type = " + "'" + TgtAudTypeValue.FOLLOWERS_SCOPE_VALUE + "'" + " AND :snsUserId IN (SELECT SNS_UF.follower_id FROM sns_user_follows_tb AS SNS_UF WHERE sns_post.sns_user_id = SNS_UF.following_id) )"
	// 	+ ")"
	// 	+ "ORDER BY sns_post.sns_post_id DESC LIMIT :pageSize";


	// String MAP_POST_RELATION_QUERY = "WITH "
	// 	+ "INCLUDE_LOCATION_TB AS (SELECT SNS_P.sns_post_id, FROM SNS_POSTS_TB AS SNS_P WHERE SNS_P.latitude IS NOT NULL), "
	// 	+ "RELATION_TAG_BY_SEARCH_QUERY AS (SELECT "
	// 	+ " SNS_T.sns_tag_id AS tag_id "
	// 	+ "FROM "
	// 	+ " sns_tags_tb AS SNS_T  "
	// 	+ "WHERE "
	// 	+ " SNS_T.tag_name @@ :searchQuery), "
	// 	+ "TAG_RELATION AS (SELECT "
	// 	+ " SNS_TP.sns_post_id as post_id "
	// 	+ "FROM "
	// 	+ " SNS_TAG_POSTS_TB AS SNS_TP  "
	// 	+ "INNER JOIN "
	// 	+ "INCLUDE_LOCATION_TB ON INCLUDE_LOCATION_TB.sns_post_id = SNS_TP.sns_post_id "
	// 	+ "INNER JOIN "
	// 	+ " RELATION_TAG_BY_SEARCH_QUERY  "
	// 	+ " ON SNS_TP.sns_tag_id = RELATION_TAG_BY_SEARCH_QUERY.tag_id  "
	// 	+ "OFFSET :page LIMIT :pageSize "
	// 	+ "), "
	// 	+ "RELATION_SCARP_BY_SEARCH_QUERY AS (SELECT "
	// 	+ " SNS_SB.sns_scrap_board_id AS scrap_board_id "
	// 	+ "FROM "
	// 	+ " sns_scrap_boards_tb AS SNS_SB  "
	// 	+ "WHERE "
	// 	+ " SNS_SB.scrap_name @@ :searchQuery), "
	// 	+ "SCRAP_RELATION AS (SELECT "
	// 	+ "SNS_S.sns_post_id AS post_id "
	// 	+ "FROM "
	// 	+ "SNS_SCRAPS_TB AS SNS_S  "
	// 	+ "INNER JOIN "
	// 	+ "INCLUDE_LOCATION_TB ON INCLUDE_LOCATION_TB.sns_post_id = SNS_S.sns_post_id "
	// 	+ "INNER JOIN "
	// 	+ "RELATION_SCARP_BY_SEARCH_QUERY  "
	// 	+ "ON SNS_S.sns_scrap_board_id = RELATION_SCARP_BY_SEARCH_QUERY.scrap_board_id "
	// 	+ "OFFSET :page LIMIT :pageSize "
	// 	+ "), "
	// 	+ "RELATION_POST_BY_SEARCH_QUERY AS (SELECT "
	// 	+ "SNS_P.sns_post_id AS post_id "
	// 	+ "FROM "
	// 	+ " sns_posts_tb AS SNS_P "
	// 	+ "INNER JOIN "
	// 	+ " INCLUDE_LOCATION_TB ON INCLUDE_LOCATION_TB.sns_post_id = SNS_P.sns_post_id "
	// 	+ "WHERE "
	// 	+ " SNS_P.post_title @@ :searchQuery OR SNS_P.post_body_text @@ :searchQuery "
	// 	+ "OFFSET :page LIMIT :pageSize "
	// 	+ "), "
	// 	+ "POST_ID_BY_MAP_RELATION AS (SELECT post_id FROM RELATION_POST_BY_SEARCH_QUERY  "
	// 	+ "UNION  "
	// 	+ "SELECT post_id FROM TAG_RELATION "
	// 	+ "UNION "
	// 	+ "SELECT post_id FROM SCRAP_RELATION), "
	// 	+ "follow_relations as (SELECT SNS_F.following_id, SNS_F.follower_id FROM sns_user_follows_tb AS SNS_F where SNS_F.follower_id = :snsUserId)  "
	// 	+ "SELECT sns_post.sns_post_id as cursorId, sns_post.sns_post_id AS post_id, SNS_U.profile_path AS profile_path,  "
	// 	+ "COALESCE(SPUR.is_liked,false) AS is_liked, COALESCE(SPUR.is_reposted,false) AS is_reposted, COALESCE(SPUR.is_clipped,false) AS is_clipped,  "
	// 	+ "sns_post.created_at AS posted_at, "
	// 	+ "latitude, longitude, address, build_name, post_title, post_body_text, (CASE WHEN sns_post.sns_user_id = :snsUserId THEN false ELSE true END) AS followable,  "
	// 	+ "sns_post_contents, tags, tgt_aud_type, sns_post.sns_user_id AS sns_user_id, SNS_U.username AS username, FOLLOW.following_id as following_id  "
	// 	+ "from sns_posts_tb AS sns_post "
	// 	+ "INNER JOIN POST_ID_BY_MAP_RELATION ON POST_ID_BY_MAP_RELATION.post_id = sns_post.sns_post_id "
	// 	+ "INNER JOIN sns_users_tb AS SNS_U ON sns_post.sns_user_id = SNS_U.sns_user_id  "
	// 	+ "LEFT OUTER JOIN follow_relations AS FOLLOW  "
	// 	+ "ON sns_post.sns_user_id = FOLLOW.following_id  "
	// 	+ "LEFT OUTER JOIN sns_post_user_reactions_tb AS SPUR  "
	// 	+ "ON SPUR.sns_user_id = :snsUserId AND sns_post.sns_post_id = SPUR.sns_post_id  "
	// 	+ SHOW_BY_NOT_BLOCKED_USER_NATIVE_QUERY
	// 	+ "WHERE SNS_BU.sns_blocker_user_id IS NULL "
	// 	+ "AND sns_post.deleted_at IS NULL "
	// 	+ "AND "
	// 	+ "(sns_post.sns_user_id = :snsUserId "
	// 	+ "OR (sns_post.tgt_aud_type = " + "'" + TgtAudTypeValue.PUBLIC_SCOPE_VALUE + "'" + " ) "
	// 	+ "OR (sns_post.tgt_aud_type = " + "'" + TgtAudTypeValue.FOLLOWERS_SCOPE_VALUE + "'" + " AND :snsUserId IN (SELECT SNS_UF.follower_id FROM sns_user_follows_tb AS SNS_UF WHERE sns_post.sns_user_id = SNS_UF.following_id) )"
	// 	+ ")";


	String MAP_POST_RELATION_BY_POST_GIS_QUERY = "WITH "
		+ "INCLUDE_LOCATION_TB AS (SELECT SNS_P.sns_post_id,"
		+ "ST_DistanceSphere(geom, ST_SetSRID(ST_MakePoint(:longitude, :latitude), " + MapConst.MAP_COORDINATE_SYSTEM + " )) AS distance "
		+ " FROM SNS_POSTS_TB AS SNS_P WHERE SNS_P.latitude IS NOT NULL), "
		+ "RELATION_TAG_BY_SEARCH_QUERY AS (SELECT "
		+ "SNS_T.sns_tag_id AS tag_id "
		+ "FROM "
		+ " sns_tags_tb AS SNS_T  "
		+ "WHERE "
		+ " SNS_T.tag_name @@ :searchQuery), "
		+ " "
		+ "TAG_RELATION AS (SELECT "
		+ " SNS_TP.sns_post_id as post_id, INCLUDE_LOCATION_TB.distance as distance "
		+ "FROM "
		+ " SNS_TAG_POSTS_TB AS SNS_TP  "
		+ "INNER JOIN "
		+ "INCLUDE_LOCATION_TB ON INCLUDE_LOCATION_TB.sns_post_id = SNS_TP.sns_post_id "
		+ "INNER JOIN "
		+ " RELATION_TAG_BY_SEARCH_QUERY  "
		+ " ON SNS_TP.sns_tag_id = RELATION_TAG_BY_SEARCH_QUERY.tag_id  "
		+ "OFFSET :page LIMIT :pageSize "
		+ "), "
		+ " "
		+ "RELATION_SCARP_BY_SEARCH_QUERY AS (SELECT "
		+ " SNS_SB.sns_scrap_board_id AS scrap_board_id "
		+ "FROM "
		+ " sns_scrap_boards_tb AS SNS_SB  "
		+ "WHERE "
		+ " SNS_SB.scrap_name @@ :searchQuery), "
		+ " "
		+ "SCRAP_RELATION AS (SELECT "
		+ "SNS_S.sns_post_id AS post_id, INCLUDE_LOCATION_TB.distance as distance "
		+ "FROM "
		+ "SNS_SCRAPS_TB AS SNS_S  "
		+ "INNER JOIN "
		+ "INCLUDE_LOCATION_TB ON INCLUDE_LOCATION_TB.sns_post_id = SNS_S.sns_post_id "
		+ "INNER JOIN "
		+ "RELATION_SCARP_BY_SEARCH_QUERY  "
		+ "ON SNS_S.sns_scrap_board_id = RELATION_SCARP_BY_SEARCH_QUERY.scrap_board_id "
		+ "OFFSET :page LIMIT :pageSize "
		+ "), "
		+ " "
		+ "RELATION_POST_BY_SEARCH_QUERY AS (SELECT "
		+ "SNS_P.sns_post_id AS post_id, INCLUDE_LOCATION_TB.distance as distance "
		+ "FROM "
		+ " sns_posts_tb AS SNS_P "
		+ "INNER JOIN "
		+ "INCLUDE_LOCATION_TB ON INCLUDE_LOCATION_TB.sns_post_id = SNS_P.sns_post_id "
		+ "WHERE "
		+ " SNS_P.post_title @@ :searchQuery OR SNS_P.post_body_text @@ :searchQuery OR SNS_P.build_name @@ :searchQuery OR SNS_P.address @@ :searchQuery "
		+ "OFFSET :page LIMIT :pageSize "
		+ "), "
		+ " "
		+ "POST_ID_BY_MAP_RELATION AS (SELECT post_id,distance FROM RELATION_POST_BY_SEARCH_QUERY  "
		+ "UNION  "
		+ "SELECT post_id,distance FROM TAG_RELATION "
		+ "UNION "
		+ "SELECT post_id,distance FROM SCRAP_RELATION"
		+ "), "
		+ "follow_relations as (SELECT SNS_F.following_id, SNS_F.follower_id FROM sns_user_follows_tb AS SNS_F where SNS_F.follower_id = :snsUserId)  "
		+ "SELECT sns_post.sns_post_id as cursorId, sns_post.sns_post_id AS post_id, SNS_U.profile_path AS profile_path,  "
		+ "COALESCE(SPUR.is_liked,false) AS is_liked, COALESCE(SPUR.is_reposted,false) AS is_reposted, COALESCE(SPUR.is_clipped,false) AS is_clipped,  "
		+ "sns_post.created_at AS posted_at, "
		+ "latitude, longitude, address, build_name, post_title, post_body_text, (CASE WHEN sns_post.sns_user_id = :snsUserId THEN false ELSE true END) AS followable,  "
		+ "sns_post_contents, tags, tgt_aud_type, sns_post.sns_user_id AS sns_user_id, SNS_U.username AS username, FOLLOW.following_id as following_id  "
		+ "from sns_posts_tb AS sns_post "
		+ "INNER JOIN POST_ID_BY_MAP_RELATION ON POST_ID_BY_MAP_RELATION.post_id = sns_post.sns_post_id "
		+ "INNER JOIN sns_users_tb AS SNS_U ON sns_post.sns_user_id = SNS_U.sns_user_id  "
		+ "LEFT OUTER JOIN follow_relations AS FOLLOW  "
		+ "ON sns_post.sns_user_id = FOLLOW.following_id  "
		+ "LEFT OUTER JOIN sns_post_user_reactions_tb AS SPUR  "
		+ "ON SPUR.sns_user_id = :snsUserId AND sns_post.sns_post_id = SPUR.sns_post_id  "
		// + SHOW_BY_NOT_BLOCKED_USER_NATIVE_QUERY
		// + "WHERE "
		// + "SNS_BU.sns_blocker_user_id IS NULL "
		// + "AND sns_post.deleted_at IS NULL "
		// + "AND "
		// + "(sns_post.sns_user_id = :snsUserId "
		// + "OR (sns_post.tgt_aud_type = " + "'" + TgtAudTypeValue.PUBLIC_SCOPE_VALUE + "'" + " ) "
		// + "OR (sns_post.tgt_aud_type = " + "'" + TgtAudTypeValue.FOLLOWERS_SCOPE_VALUE + "'" + " AND :snsUserId IN (SELECT SNS_UF.follower_id FROM sns_user_follows_tb AS SNS_UF WHERE sns_post.sns_user_id = SNS_UF.following_id) )"
		// + ")"
		+ POST_QUERY_WHERE_CONDITION
		+ "AND sns_post.created_at >= :startDate AND sns_post.created_at <= :endDate "
		+ "ORDER BY POST_ID_BY_MAP_RELATION.distance ASC"
		;

	String MAP_POST_BY_SERACH_NATIVE_QUERY = "WITH    "
		+ "INCLUDE_LOCATION_TB AS (   "
		+ "SELECT SNS_P.sns_post_id "
		+ "FROM SNS_POSTS_TB AS SNS_P "
		+ "WHERE SNS_P.latitude IS NOT NULL AND SNS_P.longitude IS NOT NULL "
		+ "AND SNS_P.deleted_at IS NULL "
		+ "AND "
		+ "(SNS_P.sns_user_id = :snsUserId "
		+ "OR (SNS_P.tgt_aud_type = " + "'" + TgtAudTypeValue.PUBLIC_SCOPE_VALUE + "'" + " ) "
		+ "OR (SNS_P.tgt_aud_type = " + "'" + TgtAudTypeValue.FOLLOWERS_SCOPE_VALUE + "'" + " AND :snsUserId IN (SELECT SNS_UF.follower_id FROM sns_user_follows_tb AS SNS_UF WHERE SNS_P.sns_user_id = SNS_UF.following_id) )"
		+ ") "
		+ "), "
		+ "RELATION_TAG_BY_SEARCH_QUERY AS (   "
		+ "SELECT   "
		+ "SNS_T.sns_tag_id AS tag_id,   "
		+ "SNS_T.tag_name AS tag_name   "
		+ "FROM   "
		+ "sns_tags_tb AS SNS_T    "
		+ "WHERE   "
		+ "SNS_T.tag_name LIKE CONCAT(:searchQuery, '%') "
		+ "),    "
		+ "TAG_CHECK AS (   "
		+ "SELECT  "
		+ "RELATION_TAG_BY_SEARCH_QUERY.tag_name AS SEARCH_QUERY "
		// + "CASE    "
		// + "WHEN EXISTS (   "
		// + "SELECT 1   "
		// + "FROM SNS_TAG_POSTS_TB AS SNS_TP    "
		// + "INNER JOIN INCLUDE_LOCATION_TB    "
		// + "ON INCLUDE_LOCATION_TB.sns_post_id = SNS_TP.sns_post_id "
		// + "WHERE  "
		// + "SNS_TP.sns_tag_id = RELATION_TAG_BY_SEARCH_QUERY.tag_id "
		// + ") THEN TRUE   "
		// + "ELSE FALSE   "
		// + "END AS exists_flag   "
		+ "FROM "
		+ "RELATION_TAG_BY_SEARCH_QUERY   "
		+ "),   "
		+ "RELATION_SCARP_BY_SEARCH_QUERY AS (SELECT   "
		+ "SNS_SB.sns_scrap_board_id AS scrap_board_id,   "
		+ "SNS_SB.scrap_name AS scrap_name   "
		+ "FROM   "
		+ "sns_scrap_boards_tb AS SNS_SB    "
		+ "WHERE   "
		+ "SNS_SB.scrap_name LIKE CONCAT(:searchQuery, '%')),    "
		+ "SCRAP_CHECK AS (   "
		+ "SELECT    "
		+ "RELATION_SCARP_BY_SEARCH_QUERY.scrap_name AS SEARCH_QUERY "
		// + "CASE    "
		// + "WHEN EXISTS (   "
		// + "SELECT 1   "
		// + "FROM SNS_SCRAPS_TB AS SNS_S   "
		// + "INNER JOIN INCLUDE_LOCATION_TB    "
		// + "ON INCLUDE_LOCATION_TB.sns_post_id = SNS_S.sns_post_id   "
		// + "WHERE    "
		// + "SNS_S.sns_scrap_board_id = RELATION_SCARP_BY_SEARCH_QUERY.scrap_board_id   "
		// + ") THEN TRUE   "
		// + "ELSE FALSE   "
		// + "END AS exists_flag "
		+ "FROM "
		+ "RELATION_SCARP_BY_SEARCH_QUERY   "
		+ "), "
		+ "POST_CHECK AS (   "
		+ "SELECT "
		+ ":searchQuery AS SEARCH_QUERY "
		// + "CASE    "
		// + "WHEN EXISTS (   "
		// + "SELECT 1   "
		// + "FROM sns_posts_tb AS SNS_P   "
		// + "INNER JOIN INCLUDE_LOCATION_TB    "
		// + "ON INCLUDE_LOCATION_TB.sns_post_id = SNS_P.sns_post_id   "
		// + "WHERE "
		// + "SNS_P.post_title @@ :searchQuery OR SNS_P.post_body_text @@ :searchQuery OR SNS_P.build_name @@ :searchQuery OR SNS_P.address @@ :searchQuery "
		// + ") THEN TRUE   "
		// + "ELSE FALSE   "
		// + "END AS exists_flag "
		+ "), "
		+ "POST_BUILD_NAME_CHECK AS (   "
		+ "SELECT "
		+ "build_name AS SEARCH_QUERY "
		+ "FROM SNS_POSTS_TB AS SNS_P "
		+ "WHERE "
		+ "SNS_P.build_name LIKE CONCAT(:searchQuery, '%') "
		+ ") "
		+ "SELECT SEARCH_QUERY FROM TAG_CHECK   "
		+ "UNION "
		+ "SELECT SEARCH_QUERY FROM SCRAP_CHECK  "
		+ "UNION "
		+ "SELECT SEARCH_QUERY FROM POST_BUILD_NAME_CHECK "
		+ "UNION "
		+ "SELECT SEARCH_QUERY FROM POST_CHECK "
		+ "offset :page LIMIT :pageSize";

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

	String MY_CLIP_QUERY =
		"select "
			+ "        SNS_PUR.sns_post_user_reaction_id AS cursorId, "
			+ "        SNS_P.sns_post_id AS postId, "
			+ "        SNS_P.is_repost AS isReposted, "
			+ "        FALSE AS followable, "
			+ "        :snsUserId AS followingId, "
			+ "        SNS_P.latitude AS latitude, "
			+ "        SNS_P.longitude AS longitude, "
			+ "        SNS_P.address AS address, SNS_P.build_name as build_name, "
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
			+ "        SNS_PUR.sns_user_id=:snsUserId "
			+ "        and SNS_PUR.is_clipped=true "
			+ "        and SNS_PUR.sns_post_user_reaction_id < :cursorId "
			+ "        AND SNS_P.deleted_at IS NULL "
			+ "	   	   AND SNS_U.deleted_at IS NULL "
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

	@Query(
		"select new com.postvue.feelogserver.domain.snsposts.dto.SnsPostUserTagDto(snsPost, "
			+ "follow.followerUser.id, "
			+ "COALESCE(SPUR.isLiked,false), "
			+ "COALESCE(SPUR.isClipped,false), "
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

	@Query(value = MAP_POST_BY_ME_NATIVE_QUERY, nativeQuery = true)
	List<SnsPostDao> selectMapPostByMe(
		@Param("snsUserId") Long snsUserId,
		@Param("page") Integer page,
		@Param("pageSize") Integer pageSize);

	@Query(value = NEAR_FOR_ME_NATIVE_QUERY, nativeQuery = true)
	List<SnsPostDao> selectNearForMe(
		@Param("snsUserId") Long snsUserId,
		@Param("page") Integer page,
		@Param("pageSize") Integer pageSize,
		@Param("latitude") Float latitude,
		@Param("longitude") Float longitude,
		@Param("h3IndexList") List<Long> h3IndexList,
		@Param("currentDateTime") LocalDateTime currentDateTime,
		@Param("startDate") LocalDateTime startDate,
		@Param("endDate") LocalDateTime endDate
	);

	@Query(value = NEAR_FOR_ME_NATIVE_QUERY_BY, nativeQuery = true)
	List<SnsPostDao> selectNearForMeBy(
		@Param("snsUserId") Long snsUserId,
		@Param("page") Integer page,
		@Param("pageSize") Integer pageSize,
		@Param("latitude") Float latitude,
		@Param("longitude") Float longitude,
		@Param("h3IndexList") List<Long> h3IndexList,
		@Param("postContentBusinessType") String postContentBusinessType,
		@Param("currentDateTime") LocalDateTime currentDateTime,
		@Param("startDate") LocalDateTime startDate,
		@Param("endDate") LocalDateTime endDate
);

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
		@Param("snsPostId") Long snsPostId,
		@Param("snsUserId") Long snsUserId
	);

	Optional<SnsPost> findByIdAndSnsUser_Id(
		@Param("snsPostId") Long snsPostId,
		@Param("snsUserId") Long snsUserId);

	@Query(value = RECENTLY_POST_SEARCH_QUERY_NATIVE_QUERY, nativeQuery = true)
	List<SnsPostDao> selectPostBySearchQueryRecently(
		@Param("snsUserId") Long snsUserId,
		@Param("page") Integer page,
		@Param("searchQuery") String searchQuery,
		@Param("pageSize") Integer pageSize);

	@Query(value = NEAR_POST_SEARCH_QUERY_NATIVE_QUERY, nativeQuery = true)
	List<SnsPostDao> selectPostBySearchQueryNear(
		@Param("snsUserId") Long snsUserId,
		@Param("page") Integer page,
		@Param("searchQuery") String searchQuery,
		@Param("pageSize") Integer pageSize,
		@Param("latitude") Float latitude,
		@Param("longitude") Float longitude,
		@Param("h3IndexList") List<Long> h3IndexList
	);

	@Query(value = POPULAR_POST_SEARCH_QUERY_NATIVE_QUERY, nativeQuery = true)
	List<SnsPostDao> selectPostBySearchQueryPopular(
		@Param("snsUserId") Long snsUserId,
		@Param("searchQuery") String searchQuery,
		@Param("page") Integer page,
		@Param("pageSize") Integer pageSize,
		@Param("currentDateTime") LocalDateTime currentDateTime);

	@Query(value = TAG_POPULAR_POST_SEARCH_QUERY_NATIVE_QUERY, nativeQuery = true)
	List<SnsPostDao> selectTagPostBySearchQueryPopular(
		@Param("snsUserId") Long snsUserId,
		@Param("searchQuery") String searchQuery,
		@Param("page") Integer page,
		@Param("pageSize") Integer pageSize,
		@Param("currentDateTime") LocalDateTime currentDateTime);



	// @Query(value = POST_RELATION_NATIVE_QUERY, nativeQuery = true)
	// List<SnsPostDao> selectPostRelation(
	// 	@Param("relatedTagListString") String relatedTagListString,
	// 	@Param("snsUserId") Long snsUserId,
	// 	@Param("snsPostId") Long snsPostId,
	// 	@Param("cursorId") Long cursorId,
	// 	@Param("pageSize") Integer pageSize);

	@Query(value = POST_RELATION_BY_RECOMM_NATIVE_QUERY, nativeQuery = true)
	List<SnsPostDao> selectPostRelation(
		@Param("relatedTagListString") String relatedTagListString,
		@Param("snsUserId") Long snsUserId,
		@Param("snsPostId") Long snsPostId,
		@Param("recommPage") Integer recommPage,
		@Param("recommPageSize") Integer recommPageSize,
		@Param("distancePage") Integer distancePage,
		@Param("distancePageSize") Integer distancePageSize,
		@Param("livePage") Integer livePage,
		@Param("livePageSize") Integer livePageSize,
		@Param("h3IndexList") List<Long> h3IndexList,
		@Param("latitude") Float laitude,
		@Param("longitude") Float longitude,
		@Param("currentDateTime") LocalDateTime currentDateTime
	);

	// @Query(value = POST_RELATION_BY_DISTANCE_NATIVE_QUERY, nativeQuery = true)
	// List<SnsPostDao> selectPostRelationDistance(
	// 	@Param("relatedTagListString") String relatedTagListString,
	// 	@Param("snsUserId") Long snsUserId,
	// 	@Param("snsPostId") Long snsPostId,
	// 	@Param("page") Integer page,
	// 	@Param("pageSize") Integer pageSize,
	// 	@Param("h3IndexList") List<Long> h3IndexList,
	// 	@Param("latitude") Float laitude,
	// 	@Param("longitude") Float longitude,
	// 	@Param("currentDateTime") LocalDateTime currentDateTime
	// );
	//
	// @Query(value = POST_RELATION_BY_LIVE_NATIVE_QUERY, nativeQuery = true)
	// List<SnsPostDao> selectPostRelationLive(
	// 	@Param("relatedTagListString") String relatedTagListString,
	// 	@Param("snsUserId") Long snsUserId,
	// 	@Param("snsPostId") Long snsPostId,
	// 	@Param("page") Integer page,
	// 	@Param("pageSize") Integer pageSize,
	// 	@Param("h3IndexList") List<Long> h3IndexList,
	// 	@Param("latitude") Float laitude,
	// 	@Param("longitude") Float longitude,
	// 	@Param("currentDateTime") LocalDateTime currentDateTime
	// );



	@Query(
		value = MAP_POST_BY_SERACH_NATIVE_QUERY
		, nativeQuery = true)
	List<RelationPostByMapDao> findAllMapPostBySearchQuery(
		@Param("searchQuery") String searchQuery,
		@Param("page") Integer page,
		@Param("pageSize") Integer pageSize,
		@Param("snsUserId") Long snsUserId
	);

	// @Query(value = MAP_POST_RELATION_QUERY, nativeQuery = true)
	// List<SnsPostDao> findAllMapPostRelationBySearchQuery(
	// 	@Param("snsUserId") Long snsUserId,
	// 	@Param("searchQuery") String searchQuery,
	// 	@Param("page") Integer page,
	// 	@Param("pageSize") Integer pageSize
	// );

	@Query(value = MAP_POST_RELATION_BY_POST_GIS_QUERY, nativeQuery = true)
	List<SnsPostDao> findAllMapPostRelationBySearchQueryByPostGis(
		@Param("snsUserId") Long snsUserId,
		@Param("searchQuery") String searchQuery,
		@Param("page") Integer page,
		@Param("pageSize") Integer pageSize,
		@Param("latitude") Float latitude,
		@Param("longitude") Float longitude,
		@Param("startDate") LocalDateTime startDate,
		@Param("endDate") LocalDateTime endDate
	);

	@Query("SELECT SNS_P FROM SnsPost AS SNS_P "
		+ "LEFT OUTER JOIN SnsBlockUser AS SNS_BU "
		+ "ON (SNS_BU.snsBlockerUser.id = :snsUserId AND SNS_BU.snsBlockedUser.id = SNS_P.snsUser.id) "
		+ "OR (SNS_BU.snsBlockerUser.id = SNS_P.snsUser.id AND SNS_BU.snsBlockedUser.id = :snsUserId) "
		+ "WHERE SNS_P.id = :snsPostId AND SNS_BU.snsBlockedUser IS NULL")
	Optional<SnsPost> findByIdByNotBlocked(Long snsPostId, Long snsUserId);


}
