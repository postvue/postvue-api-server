package com.postvue.feelogserver.domain.snstags.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.postvue.feelogserver.app.search.dao.SearchQueryDao;
import com.postvue.feelogserver.app.search.dao.SearchTagInfoQueryDao;
import com.postvue.feelogserver.domain.snstags.SnsTag;

import org.springframework.data.repository.query.Param;

@Repository
public interface SnsTagRepository
	extends JpaRepository<SnsTag, Long>,JpaSpecificationExecutor<SnsTag> {
	List<SnsTag> findAllByTagNameIn(List<String> tagNameList);

	List<SnsTag> findAllByIdIn(List<Long> tagIdList);

	@Query(
		value = ""
			+ "SELECT SNS_T.tag_name AS search_query_name FROM sns_tags_tb AS SNS_T WHERE SNS_T.tag_name LIKE CONCAT(:searchQuery, '%') "
			+ "UNION "
			+ "SELECT SNS_SB.scrap_name AS search_query_name FROM sns_scrap_boards_tb AS SNS_SB WHERE SNS_SB.scrap_name LIKE CONCAT(:searchQuery, '%') "
			+ "UNION "
			+ "SELECT SNS_P.address AS search_query_name FROM sns_posts_tb AS SNS_P WHERE SNS_P.address LIKE CONCAT(:searchQuery, '%') "
		, nativeQuery = true)
	List<SearchQueryDao> findAllBySearchQuery(@Param("searchQuery") String searchQuery);

	@Query(
		value = "SELECT SNS_T.tag_name AS search_query_name FROM sns_tags_tb AS SNS_T WHERE SNS_T.tag_name LIKE CONCAT(:searchQuery, '%') LIMIT :pageSize"
		, nativeQuery = true)
	List<SearchQueryDao> findAllByTagSearch(
		@Param("searchQuery") String searchQuery,
		@Param("pageSize") Integer pageSize);

	@Query(
		value = "SELECT SNS_T.tag_name AS search_query_name, SNS_T.tag_reps_batch_content AS tag_reps_batch_content, SNS_T.tag_reps_batch_content_type AS tag_reps_batch_content_type FROM sns_tags_tb AS SNS_T WHERE SNS_T.tag_name LIKE CONCAT(:searchQuery, '%') OFFSET :page LIMIT :pageSize"
		, nativeQuery = true)
	List<SearchTagInfoQueryDao> findAllByTagInfoSearchPageable(
		@Param("searchQuery") String searchQuery,
		@Param("page") Integer page,
		@Param("pageSize") Integer pageSize);
}
