package com.postvue.feelogserver.endpoint;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import com.postvue.feelogserver.domain.snsusermessagereactions.SnsUserMessageReaction;
import com.postvue.feelogserver.domain.snsusermessagereactions.repository.SnsUserMessageReactionRepository;
import com.postvue.feelogserver.endpoint.dto.SnsUserMessageReactionEndpointDto;
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
public class SnsUserMessageReactionEndpoint implements CrudService<SnsUserMessageReactionEndpointDto, Long> {
	private final SnsUserMessageReactionRepository snsUserMessageReactionRepository;
	private final SnsUserMessageReactionEndpointService snsUserMessageReactionEndpointService;

	@Override
	@Nonnull
	public List<@Nonnull SnsUserMessageReactionEndpointDto> list(Pageable pageable, @Nullable Filter filter) {
		try{
			return snsUserMessageReactionEndpointService.listProcess(pageable,filter).stream().map((SnsUserMessageReactionEndpointDto::fromEntity)).toList();
		}
		catch (Exception e){
			throw new EndpointException("서버 오류 발생", LogTemplateConst.getLogInfoTemplate(e.getMessage() + "_" + e.toString(), LocalDateTime.now().toString()));
		}
	}

	@Override
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
