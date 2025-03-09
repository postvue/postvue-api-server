package com.postvue.feelogserver.endpoint;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.transaction.annotation.Transactional;

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
	@Transactional
	public @Nullable SnsPostCommentReactionEndpointDto save(SnsPostCommentReactionEndpointDto value) {
		System.out.println("호잇1");
		SnsPostCommentReaction snsPostCommentReaction = value.id() != null && Long.parseLong(value.id()) > 0
			? snsPostCommentReactionRepository.getReferenceById(Long.parseLong(value.id()))
			: new SnsPostCommentReaction();

		System.out.println("호잇2");
		try {
			System.out.println("호잇3");
			snsPostCommentReaction.setSnsPost(SnsPost.builder().id(Long.parseLong(value.snsPost_id())).build());
			System.out.println("호잇3.1");

			if (value.sourceComment_id() != null){
				snsPostCommentReaction.setSourceComment(
					SnsPostCommentReaction.builder().id(Long.parseLong(value.sourceComment_id())).build());
			}
			System.out.println("호잇3.2");
			snsPostCommentReaction.setCommentUser(SnsUser.builder().id(Long.parseLong(value.commentUser_id())).build());
			System.out.println("호잇3.4");
			snsPostCommentReaction.setCommentMsg(value.commentMsg());
			System.out.println("호잇4");
			snsPostCommentReaction.setCommentMediaType(value.commentMediaType());
			snsPostCommentReaction.setCommentMediaContent(value.commentMediaContent());
			snsPostCommentReaction.setIsSource(value.isSource());
			System.out.println("호잇5");
			snsPostCommentReaction.setCommentMediaType(value.commentMediaType());
			snsPostCommentReaction.setCommentMediaContent(value.commentMediaContent());

			System.out.println("호잇6");
			return SnsPostCommentReactionEndpointDto.fromEntity(
				snsPostCommentReactionRepository.save(snsPostCommentReaction));
		}
		catch (Exception e){
			System.out.println(e);
			throw e;
		}
	}

	@Override
	@Transactional
	public void delete(Long id) {
		snsPostCommentReactionRepository.deleteById(id);
	}
}
