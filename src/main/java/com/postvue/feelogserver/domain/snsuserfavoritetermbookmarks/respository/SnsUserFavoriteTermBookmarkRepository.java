package com.postvue.feelogserver.domain.snsuserfavoritetermbookmarks.respository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.postvue.feelogserver.domain.snsuserfavoritetermbookmarks.SnsUserFavoriteTermBookmark;

@Repository
public interface SnsUserFavoriteTermBookmarkRepository extends JpaRepository<SnsUserFavoriteTermBookmark, Long>,
	JpaSpecificationExecutor<SnsUserFavoriteTermBookmark> {

	List<SnsUserFavoriteTermBookmark> findAllBySnsUser_IdOrderByIdDesc(
		Long userId,
		Pageable pageable
	);

	Optional<SnsUserFavoriteTermBookmark> findBySnsUser_IdAndFavoriteTermName(Long userId,
		String favoriteTermName);

	Optional<SnsUserFavoriteTermBookmark> findByFavoriteTermNameAndSnsUser_id(String favoriteTermName, Long snsUserId);
}
