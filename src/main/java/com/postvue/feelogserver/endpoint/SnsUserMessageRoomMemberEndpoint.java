package com.postvue.feelogserver.endpoint;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.transaction.annotation.Transactional;

import com.postvue.feelogserver.domain.snsusermessagereactions.SnsUserMessageReaction;
import com.postvue.feelogserver.domain.snsusermessagereactions.repository.SnsUserMessageReactionRepository;
import com.postvue.feelogserver.domain.snsusermessageroommembers.SnsUserMessageRoomMember;
import com.postvue.feelogserver.domain.snsusermessageroommembers.repository.SnsUserMessageRoomMemberRepository;
import com.postvue.feelogserver.domain.snsusermessagerooms.SnsUserMessageRoom;
import com.postvue.feelogserver.endpoint.converter.JpaFilterCustomConverter;
import com.postvue.feelogserver.endpoint.dto.SnsUserMessageRoomMemberEndpointDto;
import com.postvue.feelogserver.global.exception.BadRequestErrorException;
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
public class SnsUserMessageRoomMemberEndpoint implements CrudService<SnsUserMessageRoomMemberEndpointDto, Long> {
	private final SnsUserMessageRoomMemberRepository snsUserMessageRoomMemberRepository;
	private final JpaFilterCustomConverter jpaFilterCustomConverter;

	@Override
	@Nonnull
	public List<@Nonnull SnsUserMessageRoomMemberEndpointDto> list(Pageable pageable, @Nullable Filter filter) {
		Specification<SnsUserMessageRoomMember> spec = filter != null
			? jpaFilterCustomConverter.toSpec(filter, SnsUserMessageRoomMember.class)
			: Specification.anyOf();
		return snsUserMessageRoomMemberRepository.findAll(spec,pageable).stream().map((SnsUserMessageRoomMemberEndpointDto::fromEntity)).toList();
	}

	@Override
	@Transactional
	public @Nullable SnsUserMessageRoomMemberEndpointDto save(SnsUserMessageRoomMemberEndpointDto value) {
		SnsUserMessageRoomMember snsUserMessageRoomMember = value.id() != null && Long.parseLong(value.id()) > 0
			? snsUserMessageRoomMemberRepository.getReferenceById(Long.parseLong(value.id()))
			: new SnsUserMessageRoomMember();
		snsUserMessageRoomMember.setReadAt(value.readAt());
		snsUserMessageRoomMember.setIsHidden(value.isHidden());
		snsUserMessageRoomMember.setIsBlocked(value.isBlocked());
		return SnsUserMessageRoomMemberEndpointDto.fromEntity(snsUserMessageRoomMemberRepository.save(snsUserMessageRoomMember));
	}

	@Override
	@Transactional
	public void delete(Long id) {
		snsUserMessageRoomMemberRepository.deleteById(id);
	}
}
