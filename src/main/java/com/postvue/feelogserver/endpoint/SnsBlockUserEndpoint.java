package com.postvue.feelogserver.endpoint;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.transaction.annotation.Transactional;

import com.postvue.feelogserver.domain.snsblockusers.SnsBlockUser;
import com.postvue.feelogserver.domain.snsblockusers.repository.SnsBlockUserRepository;
import com.postvue.feelogserver.endpoint.converter.JpaFilterCustomConverter;
import com.postvue.feelogserver.endpoint.dto.SnsBlockUserEndpointDto;
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
public class SnsBlockUserEndpoint implements CrudService<SnsBlockUserEndpointDto, Long> {
	private final SnsBlockUserRepository snsBlockUserRepository;
	private final JpaFilterCustomConverter jpaFilterCustomConverter;

	@Override
	@Nonnull
	@Transactional
	public List<@Nonnull SnsBlockUserEndpointDto> list(Pageable pageable, @Nullable Filter filter) {
		Specification<SnsBlockUser> spec = filter != null
			? jpaFilterCustomConverter.toSpec(filter, SnsBlockUser.class)
			: Specification.anyOf();
		return snsBlockUserRepository.findAll(spec,pageable).stream().map((SnsBlockUserEndpointDto::fromEntity)).toList();
	}


	@Override
	@Transactional
	public @Nullable SnsBlockUserEndpointDto save(SnsBlockUserEndpointDto value) {
		SnsBlockUser snsBlockUser = value.id() != null && Long.parseLong(value.id()) > 0
			? snsBlockUserRepository.getReferenceById(Long.parseLong(value.id()))
			: new SnsBlockUser();

		snsBlockUserRepository.save(snsBlockUser);
		return SnsBlockUserEndpointDto.fromEntity(snsBlockUser);
	}

	@Override
	@Transactional
	public void delete(Long id) {
		snsBlockUserRepository.deleteById(id);
	}
}
