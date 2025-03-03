package com.postvue.feelogserver.endpoint;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.transaction.annotation.Transactional;

import com.postvue.feelogserver.domain.snsblockusers.SnsBlockUser;
import com.postvue.feelogserver.domain.snsnotifications.SnsNotification;
import com.postvue.feelogserver.domain.snspostcommentlikes.SnsPostCommentLike;
import com.postvue.feelogserver.domain.snspostcommentlikes.repository.SnsPostCommentLikeRepository;
import com.postvue.feelogserver.domain.snspostcommentreactions.SnsPostCommentReaction;
import com.postvue.feelogserver.domain.snsposts.SnsPost;
import com.postvue.feelogserver.endpoint.converter.JpaFilterCustomConverter;
import com.postvue.feelogserver.endpoint.dto.SnsBlockUserEndpointDto;
import com.postvue.feelogserver.endpoint.dto.SnsNotificationEndpointDto;
import com.postvue.feelogserver.endpoint.dto.SnsPostCommentLikeEndpointDto;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import com.vaadin.hilla.BrowserCallable;
import com.vaadin.hilla.Nonnull;
import com.vaadin.hilla.Nullable;
import com.vaadin.hilla.crud.CrudService;
import com.vaadin.hilla.crud.filter.Filter;

import lombok.RequiredArgsConstructor;

@BrowserCallable
@AnonymousAllowed
@RequiredArgsConstructor
public class SnsPostCommentLikeEndpoint implements CrudService<SnsPostCommentLikeEndpointDto, Long> {
	private final SnsPostCommentLikeRepository snsPostCommentLikeRepository;
	private final JpaFilterCustomConverter jpaFilterCustomConverter;

	@Override
	@Nonnull
	@Transactional
	public List<@Nonnull SnsPostCommentLikeEndpointDto> list(Pageable pageable, @Nullable Filter filter) {
		Specification<SnsPostCommentLike> spec = filter != null
			? jpaFilterCustomConverter.toSpec(filter, SnsPostCommentLike.class)
			: Specification.anyOf();
		return snsPostCommentLikeRepository.findAll(spec,pageable).stream().map((SnsPostCommentLikeEndpointDto::fromEntity)).toList();
	}


	@Override
	@Transactional
	public @Nullable SnsPostCommentLikeEndpointDto save(SnsPostCommentLikeEndpointDto value) {
		SnsPostCommentLike snsPostCommentLike = value.id() != null && Long.parseLong(value.id()) > 0
			? snsPostCommentLikeRepository.getReferenceById(Long.parseLong(value.id()))
			: new SnsPostCommentLike();

		snsPostCommentLike.setSnsPost(SnsPost.builder().id(Long.parseLong(value.id())).build());
		snsPostCommentLike.setSnsPostCommentReaction(SnsPostCommentReaction.builder().id(Long.parseLong(value.snsPostCommentReaction_id())).build());
		snsPostCommentLike.setIsLiked(value.isLiked());
		snsPostCommentLike.setIsLikedAt(value.isLikedAt());

		return SnsPostCommentLikeEndpointDto.fromEntity(snsPostCommentLikeRepository.save(snsPostCommentLike));
	}

	@Override
	@Transactional
	public void delete(Long id) {
		snsPostCommentLikeRepository.deleteById(id);
	}
}
