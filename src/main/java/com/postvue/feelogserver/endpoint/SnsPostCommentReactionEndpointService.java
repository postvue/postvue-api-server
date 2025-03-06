package com.postvue.feelogserver.endpoint;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.postvue.feelogserver.domain.snspostcommentreactions.SnsPostCommentReaction;
import com.postvue.feelogserver.domain.snspostcommentreactions.repository.SnsPostCommentReactionRepository;
import com.postvue.feelogserver.domain.snsposts.SnsPost;
import com.postvue.feelogserver.domain.snsusers.SnsUser;
import com.postvue.feelogserver.endpoint.converter.JpaFilterCustomConverter;
import com.postvue.feelogserver.endpoint.dto.SnsPostCommentReactionEndpointDto;
import com.vaadin.hilla.Nullable;
import com.vaadin.hilla.crud.filter.Filter;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class SnsPostCommentReactionEndpointService {
	private final SnsPostCommentReactionRepository snsPostCommentReactionRepository;
	private final JpaFilterCustomConverter jpaFilterCustomConverter;


	@Transactional
	public List<SnsPostCommentReaction> listProcess(Pageable pageable, @Nullable Filter filter) {
		Specification<SnsPostCommentReaction> spec = filter != null
			? jpaFilterCustomConverter.toSpec(filter, SnsPostCommentReaction.class)
			: Specification.anyOf();

		return snsPostCommentReactionRepository.findAll(spec,pageable).toList();
	}

	@Transactional
	public SnsPostCommentReaction saveProcess(SnsPostCommentReactionEndpointDto value) {
		SnsPostCommentReaction snsPostCommentReaction = value.id() != null && Long.parseLong(value.id()) > 0
			? snsPostCommentReactionRepository.getReferenceById(Long.parseLong(value.id()))
			: new SnsPostCommentReaction();


		snsPostCommentReaction.setSnsPost(SnsPost.builder().id(Long.parseLong(value.snsPost_id())).build());

		// if (value.sourceComment_id() != null){
		// 	snsPostCommentReaction.setSourceComment(
		// 		SnsPostCommentReaction.builder().id(Long.parseLong(value.sourceComment_id())).build());
		// }
		// snsPostCommentReaction.setIsSource(value.isSource());

		// snsPostCommentReaction.setCommentUser(SnsUser.builder().id(Long.parseLong(value.commentUser_id())).build());
		snsPostCommentReaction.setCommentMsg(value.commentMsg());
		snsPostCommentReaction.setCommentMediaType(value.commentMediaType());
		snsPostCommentReaction.setCommentMediaContent(value.commentMediaContent());
		snsPostCommentReaction.setCommentMediaType(value.commentMediaType());
		snsPostCommentReaction.setCommentMediaContent(value.commentMediaContent());
		snsPostCommentReaction.setDeletedAt(value.deletedAt());

		return snsPostCommentReactionRepository.save(snsPostCommentReaction);
	}

}
