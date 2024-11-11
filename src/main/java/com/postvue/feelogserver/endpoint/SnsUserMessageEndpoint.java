package com.postvue.feelogserver.endpoint;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import com.postvue.feelogserver.domain.snsuserfollows.SnsUserFollow;
import com.postvue.feelogserver.domain.snsusermessageroommembers.SnsUserMessageRoomMember;
import com.postvue.feelogserver.domain.snsusermessagerooms.SnsUserMessageRoom;
import com.postvue.feelogserver.domain.snsusermessages.SnsUserMessage;
import com.postvue.feelogserver.domain.snsusermessages.repository.SnsUserMessageRepository;
import com.postvue.feelogserver.domain.snsusers.SnsUser;
import com.postvue.feelogserver.endpoint.converter.JpaFilterCustomConverter;
import com.postvue.feelogserver.endpoint.dto.SnsUserMessageEndpointDto;
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
public class SnsUserMessageEndpoint implements CrudService<SnsUserMessageEndpointDto, Long> {
	private final SnsUserMessageRepository snsUserMessageRepository;
	private final JpaFilterCustomConverter jpaFilterCustomConverter;

	@Override
	@Nonnull
	public List<@Nonnull SnsUserMessageEndpointDto> list(Pageable pageable, @Nullable Filter filter) {
		Specification<SnsUserMessage> spec = filter != null
			? jpaFilterCustomConverter.toSpec(filter, SnsUserMessage.class)
			: Specification.anyOf();
		return snsUserMessageRepository.findAll(spec,pageable).stream().map((SnsUserMessageEndpointDto::fromEntity)).toList();
	}

	@Override
	public @Nullable SnsUserMessageEndpointDto save(SnsUserMessageEndpointDto value) {
		SnsUserMessage snsUserMessage = value.id() != null && Long.parseLong(value.id()) > 0
			? snsUserMessageRepository.getReferenceById(Long.parseLong(value.id()))
			: new SnsUserMessage();

		snsUserMessage.setSourceUser(SnsUser.builder().id(Long.parseLong(value.sourceUser_id())).build());
		snsUserMessage.setSnsUserMessageRoom(SnsUserMessageRoom.builder().id(Long.parseLong(value.id())).build());
		snsUserMessage.setMsgType(value.msgType());
		snsUserMessage.setMsgContent(value.msgContent());

		return SnsUserMessageEndpointDto.fromEntity(snsUserMessageRepository.save(snsUserMessage));
	}

	@Override
	public void delete(Long id) {
		snsUserMessageRepository.deleteById(id);
	}
}
