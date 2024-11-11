package com.postvue.feelogserver.domain.snstags.repository;

import java.sql.PreparedStatement;
import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.postvue.feelogserver.core.config.SnowflakeComponent;
import com.postvue.feelogserver.domain.snstags.SnsTag;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class SnsTagJdbcRepository {
	private final JdbcTemplate jdbcTemplate;
	private final SnowflakeComponent snowflakeComponent;

	@Transactional(propagation = Propagation.REQUIRED)
	public void saveAll(List<SnsTag> snsTags) {
		String sql = "INSERT INTO sns_tags_tb (sns_tag_id, tag_name) VALUES (?, ?)";
		
		jdbcTemplate.batchUpdate(sql,
			snsTags,
			snsTags.size(),
			(PreparedStatement ps, SnsTag snsTag) -> {
				snsTag.setId(snowflakeComponent.nextId());
				ps.setLong(1, snsTag.getId());
				ps.setString(2, snsTag.getTagName());
			});
	}
}
