package com.postvue.feelogserver.endpoint;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import com.postvue.feelogserver.domain.snsscrapboard.repository.SnsScrapBoardRepository;
import com.postvue.feelogserver.endpoint.dto.SnsScrapBoardEndpointDto;
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
public class SnsScrapBoardEndpoint implements CrudService<SnsScrapBoardEndpointDto, Long> {
	private final SnsScrapBoardEndpointSerivce snsScrapBoardEndpointSerivce;
	@Override
	@Nonnull
	public List<@Nonnull SnsScrapBoardEndpointDto> list(Pageable pageable, @Nullable Filter filter) {
		try {
			return snsScrapBoardEndpointSerivce.listProcess(pageable,filter).stream().map((SnsScrapBoardEndpointDto::fromEntity)).toList();
		}
		catch (Exception e){
			throw new EndpointException("서버 오류 발생", LogTemplateConst.getLogInfoTemplate(e.getMessage() + "_" + e.toString(), LocalDateTime.now().toString()));
		}
	}

	@Override
	@Nonnull
	public @Nullable SnsScrapBoardEndpointDto save(SnsScrapBoardEndpointDto value) {
		try{
			return SnsScrapBoardEndpointDto.fromEntity(snsScrapBoardEndpointSerivce.saveProcess(value));
		}
		catch (Exception e){
			throw new EndpointException("서버 오류 발생", LogTemplateConst.getLogInfoTemplate(e.getMessage() + "_" + e.toString(), LocalDateTime.now().toString()));
		}
	}

	@Override
	public void delete(Long id) {
		try{
			snsScrapBoardEndpointSerivce.deleteProcess(id);
		}
		catch (Exception e){
			throw new EndpointException("서버 오류 발생", LogTemplateConst.getLogInfoTemplate(e.getMessage() + "_" + e.toString(), LocalDateTime.now().toString()));
		}
	}
}
