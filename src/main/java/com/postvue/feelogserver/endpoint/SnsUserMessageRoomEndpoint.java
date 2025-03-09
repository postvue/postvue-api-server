package com.postvue.feelogserver.endpoint;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.transaction.annotation.Transactional;

import com.postvue.feelogserver.domain.snsusermessagereactions.SnsUserMessageReaction;
import com.postvue.feelogserver.domain.snsusermessagereactions.repository.SnsUserMessageReactionRepository;
import com.postvue.feelogserver.domain.snsusermessagerooms.SnsUserMessageRoom;
import com.postvue.feelogserver.domain.snsusermessagerooms.repository.SnsUserMessageRoomRepository;
import com.postvue.feelogserver.endpoint.converter.JpaFilterCustomConverter;
import com.postvue.feelogserver.endpoint.dto.SnsUserMessageReactionEndpointDto;
import com.postvue.feelogserver.endpoint.dto.SnsUserMessageRoomEndpointDto;
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
public class SnsUserMessageRoomEndpoint implements CrudService<SnsUserMessageRoomEndpointDto, Long> {
	private final SnsUserMessageRoomRepository snsUserMessageRoomRepository;
	private final JpaFilterCustomConverter jpaFilterCustomConverter;

	@Override
	@Nonnull
	public List<@Nonnull SnsUserMessageRoomEndpointDto> list(Pageable pageable, @Nullable Filter filter) {
		Specification<SnsUserMessageRoom> spec = filter != null
			? jpaFilterCustomConverter.toSpec(filter, SnsUserMessageRoom.class)
			: Specification.anyOf();

		return snsUserMessageRoomRepository.findAll(spec,pageable).map((SnsUserMessageRoomEndpointDto::fromEntity)).toList();
	}

	@Override
	@Transactional
	public @Nullable SnsUserMessageRoomEndpointDto save(SnsUserMessageRoomEndpointDto value) {
		SnsUserMessageRoom snsUserMessageRoom = value.id() != null && Long.parseLong(value.id()) > 0
			? snsUserMessageRoomRepository.getReferenceById(Long.parseLong(value.id()))
			: new SnsUserMessageRoom();

		snsUserMessageRoom.setCreatedAt(value.createdAt());
		return SnsUserMessageRoomEndpointDto.fromEntity(snsUserMessageRoomRepository.save(snsUserMessageRoom));
	}

	@Override
	@Transactional
	public void delete(Long id) {
		snsUserMessageRoomRepository.deleteById(id);
	}
}
