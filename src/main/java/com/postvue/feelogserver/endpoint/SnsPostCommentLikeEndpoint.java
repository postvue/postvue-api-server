package com.postvue.feelogserver.endpoint;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import com.postvue.feelogserver.domain.snspostcommentlikes.repository.SnsPostCommentLikeRepository;
import com.postvue.feelogserver.endpoint.dto.SnsPostCommentLikeEndpointDto;
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
public class SnsPostCommentLikeEndpoint implements CrudService<SnsPostCommentLikeEndpointDto, Long> {
	private final SnsPostCommentLikeRepository snsPostCommentLikeRepository;
	private final SnsPostCommentLikeEndpointService snsPostCommentLikeEndpointService;

	@Override
	@Nonnull
	public List<@Nonnull SnsPostCommentLikeEndpointDto> list(Pageable pageable, @Nullable Filter filter) {
		try {
			return snsPostCommentLikeEndpointService.listProcess(pageable,filter);
		}
		catch (Exception e){
			throw new EndpointException("서버 오류 발생", LogTemplateConst.getLogInfoTemplate(e.getMessage() + "_" + e.toString(), LocalDateTime.now().toString()));
		}
	}


	@Override
	public @Nullable SnsPostCommentLikeEndpointDto save(SnsPostCommentLikeEndpointDto value) {
		try{
			return SnsPostCommentLikeEndpointDto.fromEntity(snsPostCommentLikeEndpointService.saveProcess(value));
		}
		catch (Exception e){
			throw new EndpointException("서버 오류 발생", LogTemplateConst.getLogInfoTemplate(e.getMessage() + "_" + e.toString(), LocalDateTime.now().toString()));
		}
	}

	@Override
	@Transactional
	public void delete(Long id) {
		snsPostCommentLikeRepository.deleteById(id);
	}
}
