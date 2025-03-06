package com.postvue.feelogserver.endpoint;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import com.postvue.feelogserver.domain.snsuserfavoritetermbookmarks.respository.SnsUserFavoriteTermBookmarkRepository;
import com.postvue.feelogserver.endpoint.dto.SnsUserFavoriteTermBookmarkEndpointDto;
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
public class SnsUserFavoriteTermBookmarkEndpoint implements CrudService<SnsUserFavoriteTermBookmarkEndpointDto, Long> {
	private final SnsUserFavoriteTermBookmarkRepository snsUserFavoriteTermBookmarkRepository;
	private final SnsUserFavoriteTermBookmarkEndpointService snsUserFavoriteTermBookmarkEndpointService;

	@Override
	@Nonnull
	public List<@Nonnull SnsUserFavoriteTermBookmarkEndpointDto> list(Pageable pageable, @Nullable Filter filter) {
		try{
			return snsUserFavoriteTermBookmarkEndpointService.listProcess(pageable,filter).stream().map((SnsUserFavoriteTermBookmarkEndpointDto::fromEntity)).toList();
		}
		catch (Exception e){
			throw new EndpointException("서버 오류 발생", LogTemplateConst.getLogInfoTemplate(e.getMessage() + "_" + e.toString(), LocalDateTime.now().toString()));
		}
	}

	@Override
	public @Nullable SnsUserFavoriteTermBookmarkEndpointDto save(SnsUserFavoriteTermBookmarkEndpointDto value) {
		try{
			return SnsUserFavoriteTermBookmarkEndpointDto.fromEntity(snsUserFavoriteTermBookmarkEndpointService.saveProcess(value));
		}
		catch (Exception e){
			throw new EndpointException("서버 오류 발생", LogTemplateConst.getLogInfoTemplate(e.getMessage() + "_" + e.toString(), LocalDateTime.now().toString()));
		}
	}

	@Override
	@Transactional
	public void delete(Long id) {
		try {
			snsUserFavoriteTermBookmarkEndpointService.deleteProcess(id);
		}
		catch (Exception e){
			throw new EndpointException("서버 오류 발생", LogTemplateConst.getLogInfoTemplate(e.getMessage() + "_" + e.toString(), LocalDateTime.now().toString()));
		}
	}
}
