package com.postvue.feelogserver.endpoint;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.transaction.annotation.Transactional;

import com.postvue.feelogserver.domain.snsusermessagereactions.SnsUserMessageReaction;
import com.postvue.feelogserver.domain.snsusermessagereactions.repository.SnsUserMessageReactionRepository;
import com.postvue.feelogserver.endpoint.converter.JpaFilterCustomConverter;
import com.postvue.feelogserver.endpoint.dto.SnsUserMessageReactionEndpointDto;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import com.vaadin.hilla.BrowserCallable;
import com.vaadin.hilla.Nullable;
import com.vaadin.hilla.crud.filter.Filter;

import lombok.RequiredArgsConstructor;

@BrowserCallable
@AnonymousAllowed
@RequiredArgsConstructor
public class SnsUserMessageReactionEndpointService {
	private final SnsUserMessageReactionRepository snsUserMessageReactionRepository;
	private final JpaFilterCustomConverter jpaFilterCustomConverter;


	@Transactional
	public List<SnsUserMessageReaction> listProcess(Pageable pageable, @Nullable Filter filter) {
		Specification<SnsUserMessageReaction> spec = filter != null
			? jpaFilterCustomConverter.toSpec(filter, SnsUserMessageReaction.class)
			: Specification.anyOf();
		return snsUserMessageReactionRepository.findAll(spec,pageable).toList();
	}

	@Transactional
	public SnsUserMessageReaction saveProcess(SnsUserMessageReactionEndpointDto value) {
		SnsUserMessageReaction snsUserMessageReaction = value.id() != null && Long.parseLong(value.id()) >0
			? snsUserMessageReactionRepository.getReferenceById(Long.parseLong(value.id()))
			: new SnsUserMessageReaction();

		return snsUserMessageReactionRepository.save(snsUserMessageReaction);
	}

}
