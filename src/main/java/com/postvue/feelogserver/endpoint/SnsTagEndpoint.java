package com.postvue.feelogserver.endpoint;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.transaction.annotation.Transactional;

import com.postvue.feelogserver.domain.snsscrap.SnsScrap;
import com.postvue.feelogserver.domain.snstagposts.SnsTagPost;
import com.postvue.feelogserver.domain.snstagposts.respository.SnsTagPostRepository;
import com.postvue.feelogserver.domain.snstags.SnsTag;
import com.postvue.feelogserver.domain.snstags.repository.SnsTagRepository;
import com.postvue.feelogserver.endpoint.converter.JpaFilterCustomConverter;
import com.postvue.feelogserver.endpoint.dto.SnsTagEndpointDto;
import com.postvue.feelogserver.endpoint.dto.SnsTagPostEndpointDto;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import com.vaadin.hilla.BrowserCallable;
import com.vaadin.hilla.Nonnull;
import com.vaadin.hilla.Nullable;
import com.vaadin.hilla.crud.CrudService;
import com.vaadin.hilla.crud.filter.Filter;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@BrowserCallable
@AnonymousAllowed
@RequiredArgsConstructor
public class SnsTagEndpoint implements CrudService<SnsTagEndpointDto, Long> {
	private final SnsTagRepository snsTagRepository;
	private final JpaFilterCustomConverter jpaFilterCustomConverter;

	@Override
	@Nonnull
	@Transactional
	public List<@Nonnull SnsTagEndpointDto> list(Pageable pageable, @Nullable Filter filter) {
		Specification<SnsTag> spec = filter != null
			? jpaFilterCustomConverter.toSpec(filter, SnsTag.class)
			: Specification.anyOf();
		return snsTagRepository.findAll(spec,pageable).map((SnsTagEndpointDto::fromEntity)).toList();
	}

	@Override
	@Transactional
	public @Nullable SnsTagEndpointDto save(SnsTagEndpointDto value) {
		SnsTag snsTag = value.id() != null && Long.parseLong(value.id()) > 0
			? snsTagRepository.getReferenceById(Long.parseLong(value.id()))
			: new SnsTag();

		try {
			String a = value.tagName();
			snsTag.setTagName(a);
			snsTag.setIsExposed(value.isExposed());
			snsTag.setTagRepsBatchContent(value.tagRepsBatchContent());
			snsTag.setTagRepsBatchContentType(value.tagRepsBatchContentType());
			snsTag.setCreatedAt(value.createdAt());
			snsTag.setLastUpdatedAt(LocalDateTime.now());
		}
		catch (Exception e){
			log.error(e.getMessage());
		}


		return SnsTagEndpointDto.fromEntity(snsTagRepository.save(snsTag));
	}

	@Override
	@Transactional
	public void delete(Long id) {
		snsTagRepository.deleteById(id);
	}
}
