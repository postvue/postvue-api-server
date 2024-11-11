package com.postvue.feelogserver.endpoint;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import com.postvue.feelogserver.domain.snspostcommentlikes.SnsPostCommentLike;
import com.postvue.feelogserver.domain.snspostcommentlikes.repository.SnsPostCommentLikeRepository;
import com.postvue.feelogserver.domain.snspostcommentreactions.SnsPostCommentReaction;
import com.postvue.feelogserver.domain.snspostcommentreactions.repository.SnsPostCommentReactionRepository;
import com.postvue.feelogserver.domain.snsposts.SnsPost;
import com.postvue.feelogserver.domain.snsusers.SnsUser;
import com.postvue.feelogserver.endpoint.converter.JpaFilterCustomConverter;
import com.postvue.feelogserver.endpoint.dto.SnsPostCommentLikeEndpointDto;
import com.postvue.feelogserver.endpoint.dto.SnsPostCommentReactionEndpointDto;
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
public class SnsPostCommentReactionEndpoint implements CrudService<SnsPostCommentReactionEndpointDto, Long> {
	private final SnsPostCommentReactionRepository snsPostCommentReactionRepository;
	private final JpaFilterCustomConverter jpaFilterCustomConverter;

	@Override
	@Nonnull
	public List<@Nonnull SnsPostCommentReactionEndpointDto> list(Pageable pageable, @Nullable Filter filter) {
		Specification<SnsPostCommentReaction> spec = filter != null
			? jpaFilterCustomConverter.toSpec(filter, SnsPostCommentReaction.class)
			: Specification.anyOf();
		return snsPostCommentReactionRepository.findAll(spec,pageable).stream().map((SnsPostCommentReactionEndpointDto::fromEntity)).toList();
	}

	@Override
	public @Nullable SnsPostCommentReactionEndpointDto save(SnsPostCommentReactionEndpointDto value) {
		SnsPostCommentReaction snsPostCommentReaction = value.id() != null && Long.parseLong(value.id()) > 0
			? snsPostCommentReactionRepository.getReferenceById(Long.parseLong(value.id()))
			: new SnsPostCommentReaction();

		snsPostCommentReaction.setSnsPost(SnsPost.builder().id(Long.parseLong(value.snsPost_id())).build());
		snsPostCommentReaction.setSourceComment(SnsPostCommentReaction.builder().id(Long.parseLong(value.sourceComment_id())).build());
		snsPostCommentReaction.setCommentUser(SnsUser.builder().id(Long.parseLong(value.commentUser_id())).build());
		snsPostCommentReaction.setCommentMsg(value.commentMsg());
		snsPostCommentReaction.setCommentMediaType(value.commentMediaType());
		snsPostCommentReaction.setCommentMediaContent(value.commentMediaContent());
		snsPostCommentReaction.setIsSource(value.isSource());
		return SnsPostCommentReactionEndpointDto.fromEntity(snsPostCommentReactionRepository.save(snsPostCommentReaction));
	}

	@Override
	public void delete(Long id) {
		snsPostCommentReactionRepository.deleteById(id);
	}
}
