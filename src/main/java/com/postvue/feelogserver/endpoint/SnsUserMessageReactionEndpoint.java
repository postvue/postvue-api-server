package com.postvue.feelogserver.endpoint;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.transaction.annotation.Transactional;

import com.postvue.feelogserver.domain.snsuserfollows.SnsUserFollow;
import com.postvue.feelogserver.domain.snsusermessagereactions.SnsUserMessageReaction;
import com.postvue.feelogserver.domain.snsusermessagereactions.repository.SnsUserMessageReactionRepository;
import com.postvue.feelogserver.domain.snsusermessages.SnsUserMessage;
import com.postvue.feelogserver.endpoint.converter.JpaFilterCustomConverter;
import com.postvue.feelogserver.endpoint.dto.SnsUserMessageReactionEndpointDto;
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
public class SnsUserMessageReactionEndpoint implements CrudService<SnsUserMessageReactionEndpointDto, Long> {
	private final SnsUserMessageReactionRepository snsUserMessageReactionRepository;
	private final JpaFilterCustomConverter jpaFilterCustomConverter;

	@Override
	@Nonnull
	@Transactional
	public List<@Nonnull SnsUserMessageReactionEndpointDto> list(Pageable pageable, @Nullable Filter filter) {
		Specification<SnsUserMessageReaction> spec = filter != null
			? jpaFilterCustomConverter.toSpec(filter, SnsUserMessageReaction.class)
			: Specification.anyOf();
		return snsUserMessageReactionRepository.findAll(spec,pageable).map((SnsUserMessageReactionEndpointDto::fromEntity)).toList();
	}

	@Override
	@Transactional
	public @Nullable SnsUserMessageReactionEndpointDto save(SnsUserMessageReactionEndpointDto value) {
		SnsUserMessageReaction snsUserMessageReaction = value.id() != null && Long.parseLong(value.id()) >0
			? snsUserMessageReactionRepository.getReferenceById(Long.parseLong(value.id()))
			: new SnsUserMessageReaction();

		return SnsUserMessageReactionEndpointDto.fromEntity(snsUserMessageReactionRepository.save(snsUserMessageReaction));
	}

	@Override
	@Transactional
	public void delete(Long id) {
		snsUserMessageReactionRepository.deleteById(id);
	}
}
