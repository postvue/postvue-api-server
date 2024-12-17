package com.postvue.feelogserver.endpoint;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.transaction.annotation.Transactional;

import com.postvue.feelogserver.domain.snsscrap.SnsScrap;
import com.postvue.feelogserver.domain.snsscrap.repository.SnsScrapRepository;
import com.postvue.feelogserver.domain.snsscrapboard.SnsScrapBoard;
import com.postvue.feelogserver.domain.snsscrapboard.repository.SnsScrapBoardRepository;
import com.postvue.feelogserver.domain.snsusers.SnsUser;
import com.postvue.feelogserver.endpoint.converter.JpaFilterCustomConverter;
import com.postvue.feelogserver.endpoint.dto.SnsScrapBoardEndpointDto;
import com.postvue.feelogserver.endpoint.dto.SnsScrapEndpointDto;
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
public class SnsScrapBoardEndpoint implements CrudService<SnsScrapBoardEndpointDto, Long> {
	private final SnsScrapBoardRepository snsScrapBoardRepository;
	private final JpaFilterCustomConverter jpaFilterCustomConverter;

	@Override
	@Nonnull
	public List<@Nonnull SnsScrapBoardEndpointDto> list(Pageable pageable, @Nullable Filter filter) {
		Specification<SnsScrapBoard> spec = filter != null
			? jpaFilterCustomConverter.toSpec(filter, SnsScrapBoard.class)
			: Specification.anyOf();
		return snsScrapBoardRepository.findAll(spec,pageable).map((SnsScrapBoardEndpointDto::fromEntity)).toList();
	}

	@Override
	@Transactional
	public @Nullable SnsScrapBoardEndpointDto save(SnsScrapBoardEndpointDto value) {
		SnsScrapBoard snsScrapBoard = value.id() != null && Long.parseLong(value.id()) > 0
			? snsScrapBoardRepository.getReferenceById(Long.parseLong(value.id()))
			: new SnsScrapBoard();

		snsScrapBoard.setSnsUser(SnsUser.builder().id(Long.parseLong(value.snsUser_id())).build());
		snsScrapBoard.setScrapName(value.scrapName());
		snsScrapBoard.setTargetAudience(value.targetAudience());

		return SnsScrapBoardEndpointDto.fromEntity(snsScrapBoardRepository.save(snsScrapBoard));
	}

	@Override
	@Transactional
	public void delete(Long id) {
		snsScrapBoardRepository.deleteById(id);
	}
}
