package com.postvue.feelogserver.endpoint;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.postvue.feelogserver.domain.snspostcommentlikes.SnsPostCommentLike;
import com.postvue.feelogserver.domain.snspostcommentlikes.repository.SnsPostCommentLikeRepository;
import com.postvue.feelogserver.domain.snspostcommentreactions.SnsPostCommentReaction;
import com.postvue.feelogserver.domain.snsposts.SnsPost;
import com.postvue.feelogserver.domain.snsusers.SnsUser;
import com.postvue.feelogserver.endpoint.converter.JpaFilterCustomConverter;
import com.postvue.feelogserver.endpoint.dto.SnsPostCommentLikeEndpointDto;
import com.vaadin.hilla.Nullable;
import com.vaadin.hilla.crud.filter.Filter;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SnsPostCommentLikeEndpointService {
	private final SnsPostCommentLikeRepository snsPostCommentLikeRepository;
	private final JpaFilterCustomConverter jpaFilterCustomConverter;

	@Transactional
	public List<SnsPostCommentLikeEndpointDto> listProcess(Pageable pageable, @Nullable Filter filter) {
		Specification<SnsPostCommentLike> spec = filter != null
			? jpaFilterCustomConverter.toSpec(filter, SnsPostCommentLike.class)
			: Specification.anyOf();
		return snsPostCommentLikeRepository.findAll(spec,pageable).map((SnsPostCommentLikeEndpointDto::fromEntity)).toList();
	}


	@Transactional
	public SnsPostCommentLike saveProcess(SnsPostCommentLikeEndpointDto value) {
		SnsPostCommentLike snsPostCommentLike = value.id() != null && Long.parseLong(value.id()) > 0
			? snsPostCommentLikeRepository.getReferenceById(Long.parseLong(value.id()))
			: new SnsPostCommentLike();

		snsPostCommentLike.setSnsPost(SnsPost.builder().id(Long.parseLong(value.snsPost_id())).build());
		snsPostCommentLike.setSnsUser(SnsUser.builder().id(Long.parseLong(value.snsUser_id())).build());
		snsPostCommentLike.setSnsPostCommentReaction(SnsPostCommentReaction.builder().id(Long.parseLong(value.snsPostCommentReaction_id())).build());
		snsPostCommentLike.setIsLiked(value.isLiked());
		snsPostCommentLike.setIsLikedAt(value.isLikedAt());
		snsPostCommentLike.setCreatedAt(value.createdAt());

		return snsPostCommentLikeRepository.save(snsPostCommentLike);
	}

}
