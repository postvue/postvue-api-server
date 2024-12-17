package com.postvue.feelogserver.endpoint;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.transaction.annotation.Transactional;

import com.postvue.feelogserver.domain.snsposts.SnsPost;
import com.postvue.feelogserver.domain.snsscrap.SnsScrap;
import com.postvue.feelogserver.domain.snsscrap.repository.SnsScrapRepository;
import com.postvue.feelogserver.domain.snsscrapboard.SnsScrapBoard;
import com.postvue.feelogserver.domain.snsusers.SnsUser;
import com.postvue.feelogserver.endpoint.converter.JpaFilterCustomConverter;
import com.postvue.feelogserver.endpoint.dto.SnsScrapEndpointDto;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import com.vaadin.hilla.BrowserCallable;
import com.vaadin.hilla.Nonnull;
import com.vaadin.hilla.Nullable;
import com.vaadin.hilla.crud.CrudService;
import com.vaadin.hilla.crud.JpaFilterConverter;
import com.vaadin.hilla.crud.filter.Filter;

import lombok.RequiredArgsConstructor;

@BrowserCallable
@AnonymousAllowed
@RequiredArgsConstructor
public class SnsScrapEndpoint implements CrudService<SnsScrapEndpointDto, Long> {
	private final SnsScrapRepository snsScrapRepository;
	private final JpaFilterCustomConverter jpaFilterCustomConverter;


	@Override
	@Nonnull
	public List<@Nonnull SnsScrapEndpointDto> list(Pageable pageable, @Nullable Filter filter) {
		Specification<SnsScrap> spec = filter != null
			? jpaFilterCustomConverter.toSpec(filter, SnsScrap.class)
			: Specification.anyOf();

		return snsScrapRepository.findAll(spec, pageable).getContent().stream().map((SnsScrapEndpointDto::fromEntity)).toList();
	}

	@Override
	@Transactional
	public @Nullable SnsScrapEndpointDto save(SnsScrapEndpointDto value) {
		SnsScrap snsScrap = value.id() != null && Long.parseLong(value.id()) > 0
			? snsScrapRepository.getReferenceById(Long.parseLong(value.id()))
			: new SnsScrap();

		snsScrap.setSnsUser(SnsUser.builder().id(Long.parseLong(value.snsUser_id())).build());
		snsScrap.setSnsPost(SnsPost.builder().id(Long.parseLong(value.snsPost_id())).build());
		snsScrap.setSnsScrapBoard(SnsScrapBoard.builder().id(Long.parseLong(value.snsScrapBoard_id())).build());



		return SnsScrapEndpointDto.fromEntity(snsScrapRepository.save(snsScrap));
	}

	@Override
	@Transactional
	public void delete(Long id) {
		snsScrapRepository.deleteById(id);
	}
}
