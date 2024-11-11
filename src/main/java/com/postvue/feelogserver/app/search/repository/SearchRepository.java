package com.postvue.feelogserver.app.search.repository;

import org.springframework.stereotype.Repository;

@Repository
public interface SearchRepository {
	String POST_RELATION_NATIVE_QUERY = "WITH "
		+ "tag_relation AS (SELECT sns_tag_id FROM SNS_TAGS_TB AS SNS_T WHERE tag_name &@~ :relatedTagListString), "
		+ "post_by_tag AS (SELECT sns_post_id FROM SNS_TAG_POSTS_TB AS SNS_TP WHERE SNS_TP.sns_tag_id IN (SELECT * FROM tag_relation)), "
		+ "scrap_relation AS (SELECT sns_scrap_board_id FROM SNS_SCRAP_BOARDS_TB WHERE scrap_name &@~ :relatedTagListString), "
		+ "post_by_scrap AS (SELECT sns_post_id FROM SNS_SCRAPS_TB WHERE sns_scrap_board_id IN (SELECT * FROM scrap_relation)), "
		+ "post_relation AS (SELECT * FROM post_by_tag UNION SELECT * FROM post_by_scrap) "
		+ "SELECT sns_post.sns_post_id AS post_id, SNS_U.profile_path AS profile_path,  "
		+ "COALESCE(SPUR.is_liked,false) AS is_liked, COALESCE(SPUR.is_reposted,false) AS is_reposted, COALESCE(SPUR.is_clipped,false) AS is_clipped,  "
		+ "COALESCE(SPUR.is_bookmarked,false) AS is_bookmarked, sns_post.last_updated_at AS posted_at,  "
		+ "latitude, longitude, address, post_category, reaction_count, (CASE WHEN sns_post.sns_user_id = :snsUserId THEN false ELSE true END) AS followable,  "
		+ "sns_post_contents, tags, tgt_aud_type, sns_post.sns_user_id AS sns_user_id, SNS_U.username AS username, FOLLOW.following_id as following_id  "
		+ "from sns_posts_tb AS sns_post  "
		+ "INNER JOIN post_relation ON sns_post.sns_post_id = post_relation.sns_post_id  "
		+ "INNER JOIN sns_users_tb AS SNS_U ON sns_post.sns_user_id = SNS_U.sns_user_id  "
		+ "LEFT OUTER JOIN follow_relations AS FOLLOW ON sns_post.sns_user_id = FOLLOW.following_id  "
		+ "LEFT OUTER JOIN sns_post_user_reactions_tb AS SPUR  "
		+ "ON SPUR.sns_user_id = :snsUserId AND sns_post.sns_post_id = SPUR.sns_post_id "
		+ "WHERE (SPUR.is_shown IS TRUE OR SPUR.is_shown IS NULL) AND SNS_BU.sns_blocker_user_id IS NULL AND sns_post.sns_post_id < :cursorId ORDER BY sns_post.sns_post_id DESC LIMIT :pageSize";

}
