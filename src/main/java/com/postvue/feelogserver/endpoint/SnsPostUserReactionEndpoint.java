package com.postvue.feelogserver.endpoint;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import com.postvue.feelogserver.domain.snspostuserreactions.repository.SnsPostUserReactionRepository;
import com.postvue.feelogserver.endpoint.dto.SnsPostUserReactionEndpointDto;
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
public class SnsPostUserReactionEndpoint implements CrudService<SnsPostUserReactionEndpointDto, Long> {
	private final SnsPostUserReactionRepository snsPostUserReactionRepository;
	private final SnsPostUserReactionEndpointService snsPostUserReactionEndpointService;

	@Override
	@Nonnull
	public List<@Nonnull SnsPostUserReactionEndpointDto> list(Pageable pageable, @Nullable Filter filter) {
		try {
			return snsPostUserReactionEndpointService.listProcess(pageable,filter).stream().map((SnsPostUserReactionEndpointDto::fromEntity)).toList();
		}
		catch (Exception e){
			throw new EndpointException("서버 오류 발생", LogTemplateConst.getLogInfoTemplate(e.getMessage() + "_" + e.toString(), LocalDateTime.now().toString()));
		}
	}

	@Override
	public @Nullable SnsPostUserReactionEndpointDto save(SnsPostUserReactionEndpointDto value) {
		try{
			return SnsPostUserReactionEndpointDto.fromEntity(snsPostUserReactionEndpointService.saveProcess(value));
		}
		catch (Exception e){
			throw new EndpointException("서버 오류 발생", LogTemplateConst.getLogInfoTemplate(e.getMessage() + "_" + e.toString(), LocalDateTime.now().toString()));
		}
	}

	@Override
	@Transactional
	public void delete(Long id) {
		snsPostUserReactionRepository.deleteById(id);
	}
}
