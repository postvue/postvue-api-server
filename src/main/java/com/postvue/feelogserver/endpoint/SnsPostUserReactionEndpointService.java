package com.postvue.feelogserver.endpoint;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.postvue.feelogserver.domain.snsposts.SnsPost;
import com.postvue.feelogserver.domain.snspostuserreactions.SnsPostUserReaction;
import com.postvue.feelogserver.domain.snspostuserreactions.repository.SnsPostUserReactionRepository;
import com.postvue.feelogserver.domain.snsusers.SnsUser;
import com.postvue.feelogserver.endpoint.converter.JpaFilterCustomConverter;
import com.postvue.feelogserver.endpoint.dto.SnsPostUserReactionEndpointDto;
import com.vaadin.hilla.Nullable;
import com.vaadin.hilla.crud.filter.Filter;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SnsPostUserReactionEndpointService {
	private final SnsPostUserReactionRepository snsPostUserReactionRepository;
	private final JpaFilterCustomConverter jpaFilterCustomConverter;

	@Transactional
	public List<SnsPostUserReaction> listProcess(Pageable pageable, @Nullable Filter filter) {
		Specification<SnsPostUserReaction> spec = filter != null
			? jpaFilterCustomConverter.toSpec(filter, SnsPostUserReaction.class)
			: Specification.anyOf();
		return snsPostUserReactionRepository.findAll(spec,pageable).toList();
	}

	@Transactional
	public SnsPostUserReaction saveProcess(SnsPostUserReactionEndpointDto value) {
		SnsPostUserReaction snsPostUserReaction = value.id() != null && Long.parseLong(value.id()) > 0
			? snsPostUserReactionRepository.getReferenceById(Long.parseLong(value.id()))
			: new SnsPostUserReaction();

		snsPostUserReaction.setSnsPost(SnsPost.builder().id(Long.parseLong(value.id())).build());
		snsPostUserReaction.setSnsUser(SnsUser.builder().id(Long.parseLong(value.id())).build());
		snsPostUserReaction.setIsShown(value.isShown());
		snsPostUserReaction.setNotShownAt(value.notShownAt());

		snsPostUserReaction.setIsClipped(value.isClipped());
		snsPostUserReaction.setIsClippedAt(value.isClippedAt());

		snsPostUserReaction.setIsLiked(value.isLiked());
		snsPostUserReaction.setIsLikedAt(value.isLikedAt());

		return snsPostUserReactionRepository.save(snsPostUserReaction);
	}
}
