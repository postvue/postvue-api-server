package com.postvue.feelogserver.domain.snspostcommentlikes.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.postvue.feelogserver.domain.snspostcommentlikes.SnsPostCommentLike;

import org.springframework.data.repository.query.Param;

@Repository
public interface SnsPostCommentLikeRepository extends JpaRepository<SnsPostCommentLike, Long>,
	JpaSpecificationExecutor<SnsPostCommentLike> {
	Optional<SnsPostCommentLike> findBySnsPostCommentReaction_IdAndSnsUser_Id(
		@Param("snsPostCommentReactionId") Long snsPostCommentReactionId,
		@Param("snsUserId") Long snsUserId);

}
