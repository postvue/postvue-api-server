package com.postvue.feelogserver.endpoint;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import com.postvue.feelogserver.domain.snspostcommentreactions.repository.SnsPostCommentReactionRepository;
import com.postvue.feelogserver.endpoint.dto.SnsPostCommentReactionEndpointDto;
import com.postvue.feelogserver.global.constant.LogTemplateConst;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import com.vaadin.hilla.BrowserCallable;
import com.vaadin.hilla.Nonnull;
import com.vaadin.hilla.Nullable;
import com.vaadin.hilla.crud.CrudService;
import com.vaadin.hilla.crud.filter.Filter;
import com.vaadin.hilla.exception.EndpointException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@BrowserCallable
@AnonymousAllowed
@RequiredArgsConstructor
public class SnsPostCommentReactionEndpoint implements CrudService<SnsPostCommentReactionEndpointDto, Long> {
	private final SnsPostCommentReactionRepository snsPostCommentReactionRepository;
	private final SnsPostCommentReactionEndpointService snsPostCommentReactionEndpointService;


	@Override
	@Nonnull
	public List<@Nonnull SnsPostCommentReactionEndpointDto> list(Pageable pageable, @Nullable Filter filter) {
		try {
			return snsPostCommentReactionEndpointService.listProcess(pageable,filter).stream().map((SnsPostCommentReactionEndpointDto::fromEntity)).toList();
		}
		catch (Exception e){
			throw new EndpointException("서버 오류 발생", LogTemplateConst.getLogInfoTemplate(e.getMessage() + "_" + e.toString(), LocalDateTime.now().toString()));
		}
	}

	@Override
	@Nonnull
	public @Nullable SnsPostCommentReactionEndpointDto save(SnsPostCommentReactionEndpointDto value) {
		try {
			return SnsPostCommentReactionEndpointDto.fromEntity(
				snsPostCommentReactionEndpointService.saveProcess(value));
		}
		catch (Exception e){
			log.error(e.getMessage());
			throw new EndpointException("서버 오류 발생", LogTemplateConst.getLogInfoTemplate(e.getMessage() + "_" + e.toString(), LocalDateTime.now().toString()));
		}
	}

	@Override
	@Transactional
	public void delete(Long id) {
		snsPostCommentReactionRepository.deleteById(id);
	}
}
