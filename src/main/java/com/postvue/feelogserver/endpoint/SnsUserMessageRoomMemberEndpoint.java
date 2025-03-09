package com.postvue.feelogserver.endpoint;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import com.postvue.feelogserver.domain.snsusermessageroommembers.repository.SnsUserMessageRoomMemberRepository;
import com.postvue.feelogserver.endpoint.dto.SnsUserMessageRoomMemberEndpointDto;
import com.postvue.feelogserver.global.constant.LogTemplateConst;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import com.vaadin.hilla.BrowserCallable;
import com.vaadin.hilla.Nonnull;
import com.vaadin.hilla.Nullable;
import com.vaadin.hilla.crud.CrudService;
import com.vaadin.hilla.crud.filter.Filter;
import com.vaadin.hilla.exception.EndpointException;

import lombok.RequiredArgsConstructor;

@BrowserCallable
@AnonymousAllowed
@RequiredArgsConstructor
public class SnsUserMessageRoomMemberEndpoint implements CrudService<SnsUserMessageRoomMemberEndpointDto, Long> {
	private final SnsUserMessageRoomMemberRepository snsUserMessageRoomMemberRepository;
	private final SnsUserMessageRoomMemberEndpointService snsUserMessageRoomMemberEndpointService;

	@Override
	@Nonnull
	public List<@Nonnull SnsUserMessageRoomMemberEndpointDto> list(Pageable pageable, @Nullable Filter filter) {
		try{
			return snsUserMessageRoomMemberEndpointService.listProcess(pageable,filter).stream().map((SnsUserMessageRoomMemberEndpointDto::fromEntity)).toList();}
		catch (Exception e){
			throw new EndpointException("서버 오류 발생", LogTemplateConst.getLogInfoTemplate(e.getMessage() + "_" + e.toString(), LocalDateTime.now().toString()));
		}
	}

	@Override
	public SnsUserMessageRoomMemberEndpointDto save(SnsUserMessageRoomMemberEndpointDto value) {
		try {
			return SnsUserMessageRoomMemberEndpointDto.fromEntity(
				snsUserMessageRoomMemberEndpointService.saveProcess(value));
		}
		catch (Exception e){
			throw new EndpointException("서버 오류 발생", LogTemplateConst.getLogInfoTemplate(e.getMessage() + "_" + e.toString(), LocalDateTime.now().toString()));
		}
	}

	@Override
	@Transactional
	public void delete(Long id) {
		return;
		// snsUserMessageRoomMemberRepository.deleteById(id);
	}
}
