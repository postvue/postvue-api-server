package com.postvue.feelogserver.endpoint;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.postvue.feelogserver.domain.snsusermessageroommembers.SnsUserMessageRoomMember;
import com.postvue.feelogserver.domain.snsusermessageroommembers.repository.SnsUserMessageRoomMemberRepository;
import com.postvue.feelogserver.endpoint.converter.JpaFilterCustomConverter;
import com.postvue.feelogserver.endpoint.dto.SnsUserMessageRoomMemberEndpointDto;
import com.postvue.feelogserver.global.exception.BadRequestErrorException;
import com.vaadin.hilla.Nullable;
import com.vaadin.hilla.crud.filter.Filter;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SnsUserMessageRoomMemberEndpointService {
	private final SnsUserMessageRoomMemberRepository snsUserMessageRoomMemberRepository;
	private final JpaFilterCustomConverter jpaFilterCustomConverter;

	@Transactional
	public List<SnsUserMessageRoomMember> listProcess(Pageable pageable, @Nullable Filter filter) {
		Specification<SnsUserMessageRoomMember> spec = filter != null
			? jpaFilterCustomConverter.toSpec(filter, SnsUserMessageRoomMember.class)
			: Specification.anyOf();
		return snsUserMessageRoomMemberRepository.findAll(spec,pageable).toList();
	}

	@Transactional
	public SnsUserMessageRoomMember saveProcess(SnsUserMessageRoomMemberEndpointDto value) {
		SnsUserMessageRoomMember snsUserMessageRoomMember = value.id() != null && Long.parseLong(value.id()) > 0
			? snsUserMessageRoomMemberRepository.getReferenceById(Long.parseLong(value.id()))
			: new SnsUserMessageRoomMember();

		if (snsUserMessageRoomMember.getId() == null){
			throw new BadRequestErrorException("해당 유저는 없습니다.");
		}
		snsUserMessageRoomMember.setReadAt(value.readAt());
		snsUserMessageRoomMember.setIsHidden(value.isHidden());
		snsUserMessageRoomMember.setIsBlocked(value.isBlocked());
		return snsUserMessageRoomMemberRepository.save(snsUserMessageRoomMember);
	}
}
