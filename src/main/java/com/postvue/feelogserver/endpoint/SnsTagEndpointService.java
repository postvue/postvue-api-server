package com.postvue.feelogserver.endpoint;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.transaction.annotation.Transactional;

import com.postvue.feelogserver.domain.snstags.SnsTag;
import com.postvue.feelogserver.domain.snstags.repository.SnsTagJdbcRepository;
import com.postvue.feelogserver.domain.snstags.repository.SnsTagRepository;
import com.postvue.feelogserver.endpoint.converter.JpaFilterCustomConverter;
import com.postvue.feelogserver.endpoint.dto.SnsTagEndpointDto;
import com.postvue.feelogserver.global.exception.BadRequestErrorException;
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
public class SnsTagEndpointService {
	private final SnsTagRepository snsTagRepository;
	private final JpaFilterCustomConverter jpaFilterCustomConverter;
	private final SnsTagJdbcRepository snsTagJdbcRepository;

	@Transactional
	public List<SnsTag> listProcess(Pageable pageable, @Nullable Filter filter) {
		// snsTagJdbcRepository.saveAll(newSnsTagEntityList);
		Specification<SnsTag> spec = filter != null
			? jpaFilterCustomConverter.toSpec(filter, SnsTag.class)
			: Specification.anyOf();
		return snsTagRepository.findAll(spec,pageable).toList();
	}

	@Transactional
	public SnsTag saveProcess(SnsTagEndpointDto value) {
		SnsTag snsTag = value.id() != null && Long.parseLong(value.id()) > 0
			? snsTagRepository.getReferenceById(Long.parseLong(value.id()))
			: new SnsTag();

		if (snsTag.getId() == null){
			Optional<SnsTag> snsTagOpt = snsTagRepository.findByTagName(value.tagName());

			if (snsTagOpt.isPresent()) throw new RuntimeException("해당 태그는 있습니다.");

			snsTag.setIsExposed(true);
			snsTag.setTagRepsBatchContent(value.tagRepsBatchContent());
			snsTag.setTagRepsBatchContentType(value.tagRepsBatchContentType());
			snsTag.setCreatedAt(value.createdAt());
			snsTag.setTagName(value.tagName());

			snsTagJdbcRepository.saveAll(List.of(snsTag));

			return snsTagRepository.findByTagName(value.tagName()).orElseThrow(
				() -> new RuntimeException("오류로 인해 태그가 생성되지 않았습니다.")
			);
		}
		else{
			// snsTag.setTagName(value.tagName());
			snsTag.setIsExposed(value.isExposed());
			snsTag.setTagRepsBatchContent(value.tagRepsBatchContent());
			snsTag.setTagRepsBatchContentType(value.tagRepsBatchContentType());
			snsTag.setCreatedAt(value.createdAt());
			snsTag.setDeletedAt(value.deletedAt());
			snsTag.setLastUpdatedAt(LocalDateTime.now());

			return snsTagRepository.save(snsTag);
		}
	}
}
