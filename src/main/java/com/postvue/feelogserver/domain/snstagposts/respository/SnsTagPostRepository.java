package com.postvue.feelogserver.domain.snstagposts.respository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.postvue.feelogserver.domain.snsposts.SnsPost;
import com.postvue.feelogserver.domain.snstagposts.SnsTagPost;
import com.postvue.feelogserver.domain.snstagposts.dao.SnsRecommTagDao;

import org.springframework.data.repository.query.Param;

@Repository
public interface SnsTagPostRepository extends JpaRepository<SnsTagPost, Long>, JpaSpecificationExecutor<SnsTagPost> {
	int SCORE_CONTROL_NUM = 50;
	String RECOMM_TAG_NATIVE_QUERY = "with "
		+ "sns_posts_by_popular AS "
		+ "(SELECT SNS_POST.sns_post_id "
		+ "FROM sns_posts_tb AS SNS_POST ORDER BY "
		+ "(ABS(sns_post.reaction_count) / POWER(((EXTRACT(EPOCH FROM :currentDateTime - created_at) / 3600) + " + SCORE_CONTROL_NUM + "), 1.1)) DESC), "
		+ "tag_by_popular AS (SELECT sns_tag_id as sns_tag_id FROM sns_tag_posts_tb where sns_post_id in (SELECT * FROM sns_posts_by_popular) LIMIT :pageNumByPopular), "
		+ "interest_scores AS (SELECT sns_post_id, (CASE WHEN is_clipped = TRUE THEN 5 ELSE 0 END) + (CASE WHEN is_liked = TRUE THEN 3 ELSE 0 END) + (CASE WHEN is_reposted = TRUE THEN 1 ELSE 0 END) AS interest_score FROM sns_post_user_reactions_tb WHERE sns_user_id = :snsUserId), "
		+ "tag_interest AS (SELECT SNS_TP.sns_tag_id, SUM(INTER_S.interest_score) AS total_interest_score FROM interest_scores AS INTER_S INNER JOIN sns_tag_posts_tb AS SNS_TP ON INTER_S.sns_post_id = SNS_TP.sns_post_id GROUP BY SNS_TP.sns_tag_id),tag_follow_counts AS ( SELECT STF.sns_tag_id, COUNT(STF.sns_user_id) AS follow_count FROM sns_tag_follows_tb AS STF GROUP BY STF.sns_tag_id), "
		+ "total_tag_interest_scores AS (SELECT (CASE WHEN TI.sns_tag_id IS NOT NULL THEN TI.sns_tag_id ELSE TFC.sns_tag_id END) AS sns_tag_id, COALESCE(TI.total_interest_score,0) + COALESCE(TFC.follow_count, 0) * 4 AS adjusted_interest_score FROM tag_interest TI FULL OUTER JOIN tag_follow_counts AS TFC ON TI.sns_tag_id = TFC.sns_tag_id), "
		+ "tag_by_total_tag_interest_scores as (SELECT sns_tag_id FROM total_tag_interest_scores order by adjusted_interest_score desc LIMIT :pageNumByInterest), "
		+ "tag_id_by_recomm AS (SELECT sns_tag_id FROM tag_by_total_tag_interest_scores UNION SELECT sns_tag_id FROM tag_by_popular) "
		+ "SELECT SNS_T.sns_tag_id AS TAG_ID, SNS_T.tag_name AS TAG_NAME, SNS_T.tag_reps_batch_content AS tag_reps_batch_content, SNS_T.tag_reps_batch_content_type FROM tag_id_by_recomm INNER JOIN sns_tags_tb AS SNS_T ON tag_id_by_recomm.sns_tag_id = SNS_T.sns_tag_id";

	String POPULAR_TAG_NATIVE_QUERY = "with "
		+ "sns_posts_by_popular AS (SELECT SNS_POST.sns_post_id FROM sns_posts_tb AS SNS_POST ORDER BY (sns_post.reaction_count - 2 * POWER(EXTRACT(day FROM (sns_post.created_at - :currentDateTime)),2)) DESC), "
		+ "tag_by_popular AS (SELECT DISTINCT sns_tag_id as sns_tag_id FROM sns_tag_posts_tb where sns_post_id in (SELECT * FROM sns_posts_by_popular) offset :pageByPopular LIMIT :pageNumByPopular), "
		+ "tag_id_by_recomm AS (SELECT sns_tag_id FROM tag_by_popular) "
		+ "SELECT SNS_T.sns_tag_id AS TAG_ID, SNS_T.tag_name AS TAG_NAME, SNS_T.tag_reps_batch_content AS tag_reps_batch_content, SNS_T.tag_reps_batch_content_type FROM tag_id_by_recomm INNER JOIN sns_tags_tb AS SNS_T ON tag_id_by_recomm.sns_tag_id = SNS_T.sns_tag_id";


	@Modifying(clearAutomatically = true, flushAutomatically = true)
	@Query("delete from SnsTagPost SNS_TP where SNS_TP.snsPost = :snsPost")
	void deleteAllBySnsPostId(@Param("snsPost") SnsPost snsPost);

	List<SnsTagPost> findBySnsPost_Id(Long snsPostId);

	@Query(value = RECOMM_TAG_NATIVE_QUERY, nativeQuery = true)
	List<SnsRecommTagDao> findRecommTagList(
		@Param("snsUserId") Long snsUserId,
		@Param("currentDateTime") LocalDateTime currentDateTime,
		@Param("pageNumByPopular") Integer pageNumByPopular,
		@Param("pageNumByInterest") Integer pageNumByInterest);

	@Query(value = POPULAR_TAG_NATIVE_QUERY, nativeQuery = true)
	List<SnsRecommTagDao> findPopularTagListByPageable(
		@Param("currentDateTime") LocalDateTime currentDateTime,
		@Param("pageByPopular") Integer pageByPopular,
		@Param("pageNumByPopular") Integer pageNumByPopular);

}
