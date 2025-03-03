package com.postvue.feelogserver.endpoint;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.transaction.annotation.Transactional;

import com.postvue.feelogserver.domain.snsposts.SnsPost;
import com.postvue.feelogserver.domain.snspostuserreactions.SnsPostUserReaction;
import com.postvue.feelogserver.domain.snspostuserreactions.repository.SnsPostUserReactionRepository;
import com.postvue.feelogserver.domain.snsusers.SnsUser;
import com.postvue.feelogserver.endpoint.converter.JpaFilterCustomConverter;
import com.postvue.feelogserver.endpoint.dto.SnsPostUserReactionEndpointDto;
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
public class SnsPostUserReactionEndpoint implements CrudService<SnsPostUserReactionEndpointDto, Long> {
	private final SnsPostUserReactionRepository snsPostUserReactionRepository;
	private final JpaFilterCustomConverter jpaFilterCustomConverter;

	@Override
	@Nonnull
	@Transactional
	public List<@Nonnull SnsPostUserReactionEndpointDto> list(Pageable pageable, @Nullable Filter filter) {
		Specification<SnsPostUserReaction> spec = filter != null
			? jpaFilterCustomConverter.toSpec(filter, SnsPostUserReaction.class)
			: Specification.anyOf();
		return snsPostUserReactionRepository.findAll(spec,pageable).stream().map((SnsPostUserReactionEndpointDto::fromEntity)).toList();
	}

	@Override
	@Transactional
	public @Nullable SnsPostUserReactionEndpointDto save(SnsPostUserReactionEndpointDto value) {
		SnsPostUserReaction snsPostUserReaction = value.id() != null && Long.parseLong(value.id()) > 0
			? snsPostUserReactionRepository.getReferenceById(Long.parseLong(value.id()))
			: new SnsPostUserReaction();

		snsPostUserReaction.setSnsPost(SnsPost.builder().id(Long.parseLong(value.id())).build());
		snsPostUserReaction.setSnsUser(SnsUser.builder().id(Long.parseLong(value.id())).build());
		snsPostUserReaction.setIsShown(value.isShown());
		snsPostUserReaction.setNotShownAt(value.notShownAt());
		snsPostUserReaction.setIsClipped(value.isClipped());
		snsPostUserReaction.setIsLiked(value.isLiked());

		return SnsPostUserReactionEndpointDto.fromEntity(snsPostUserReactionRepository.save(snsPostUserReaction));
	}

	@Override
	@Transactional
	public void delete(Long id) {
		snsPostUserReactionRepository.deleteById(id);
	}
}
