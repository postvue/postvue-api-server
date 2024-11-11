package com.postvue.feelogserver.domain.snsuserfavoritetermbookmarks.respository;

import java.sql.PreparedStatement;
import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.postvue.feelogserver.core.config.SnowflakeComponent;
import com.postvue.feelogserver.domain.snsuserfavoritetermbookmarks.SnsUserFavoriteTermBookmark;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class SnsUserFavoriteTermBookmarkJdbcRepository {
	private final JdbcTemplate jdbcTemplate;
	private final SnowflakeComponent snowflakeComponent;

	@Transactional(propagation = Propagation.REQUIRED)
	public void saveAllWithTag(List<SnsUserFavoriteTermBookmark> snsUserFavoriteTermBookmarks) {
		String sql = "INSERT INTO sns_user_favorite_term_bookmarks_tb ("
			+ "sns_user_favorite_term_bookmark_id, "
			+ "sns_user_id, "
			+ "favorite_term_name, "
			+ "favorite_term_content,"
			+ "favorite_term_content_type, "
			+ "sns_tag_follow_id) VALUES (?, ?, ?, ?, ?, ?)";

		jdbcTemplate.batchUpdate(sql,
			snsUserFavoriteTermBookmarks,
			snsUserFavoriteTermBookmarks.size(),
			(PreparedStatement ps, SnsUserFavoriteTermBookmark snsUserFavoriteTermBookmark) -> {
				snsUserFavoriteTermBookmark.setId(snowflakeComponent.nextId());
				ps.setLong(1, snsUserFavoriteTermBookmark.getId());
				ps.setLong(2, snsUserFavoriteTermBookmark.getSnsUser().getId());
				ps.setString(3, snsUserFavoriteTermBookmark.getFavoriteTermName());
				ps.setString(4, snsUserFavoriteTermBookmark.getFavoriteTermContent());
				ps.setString(5, snsUserFavoriteTermBookmark.getFavoriteTermContentType() != null ?
					snsUserFavoriteTermBookmark.getFavoriteTermContentType().toString() : null);
				ps.setLong(6, snsUserFavoriteTermBookmark.getSnsTagFollow().getId());
			});
	}
}
