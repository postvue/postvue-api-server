package com.postvue.feelogserver.domain.snsuserfavoritetermbookmarks;

import java.io.Serializable;

import com.postvue.feelogserver.core.config.SnowflakeId;
import com.postvue.feelogserver.domain.snsposts.vo.PostContentType;
import com.postvue.feelogserver.domain.snstagfollows.SnsTagFollow;
import com.postvue.feelogserver.domain.snsusers.SnsUser;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "SNS_USER_FAVORITE_TERM_BOOKMARKS_TB", indexes = {
	@Index(name = "IDX__USER_TERM_NAME_BY_SNS_USER_FAVORITE_TERM_BOOKMARKS", columnList = "sns_user_id,favorite_term_name")},
	uniqueConstraints = {
		@UniqueConstraint(columnNames = {"sns_user_id", "favorite_term_name"}, name = "IDX_UNIQUE_USER_ID_FAVORITE_TERM_NAME_BY_SNS_USER_FAVORITE_TERM_BOOKMARKS")
	})
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SnsUserFavoriteTermBookmark implements Serializable {
	@Id
	@SnowflakeId
	@Column(name = "sns_user_favorite_term_bookmark_id", updatable = false)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "sns_user_id", updatable = false)
	private SnsUser snsUser;

	@Column(name = "favorite_term_name", nullable = true)
	private String favoriteTermName;

	@Column(name = "favorite_term_content")
	private String favoriteTermContent;

	@Column(name = "favorite_term_content_type")
	@Enumerated(EnumType.STRING)
	private PostContentType favoriteTermContentType;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "sns_tag_follow_id", nullable = true)
	private SnsTagFollow snsTagFollow;
}
