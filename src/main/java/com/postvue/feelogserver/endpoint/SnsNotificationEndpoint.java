package com.postvue.feelogserver.endpoint;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.transaction.annotation.Transactional;

import com.postvue.feelogserver.domain.snsblockusers.SnsBlockUser;
import com.postvue.feelogserver.domain.snsnotifications.SnsNotification;
import com.postvue.feelogserver.domain.snsnotifications.repository.SnsNotificationRepository;
import com.postvue.feelogserver.domain.snsnotifications.vo.SnsNotificationContent;
import com.postvue.feelogserver.endpoint.converter.JpaFilterCustomConverter;
import com.postvue.feelogserver.endpoint.dto.SnsNotificationEndpointDto;
import com.postvue.feelogserver.global.util.converter.JsonConverter;
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
public class SnsNotificationEndpoint implements CrudService<SnsNotificationEndpointDto, Long> {
	private final SnsNotificationRepository snsNotificationRepository;
	private final JpaFilterCustomConverter jpaFilterCustomConverter;

	@Override
	@Nonnull
	@Transactional
	public List<@Nonnull SnsNotificationEndpointDto> list(Pageable pageable, @Nullable Filter filter) {
		Specification<SnsNotification> spec = filter != null
			? jpaFilterCustomConverter.toSpec(filter, SnsNotification.class)
			: Specification.anyOf();
		return snsNotificationRepository.findAll(spec,pageable).stream().map((SnsNotificationEndpointDto::fromEntity)).toList();
	}

	@Override
	@Transactional
	public @Nullable SnsNotificationEndpointDto save(SnsNotificationEndpointDto value) {
		SnsNotification snsNotification = value.id() != null && Long.parseLong(value.id()) > 0
			? snsNotificationRepository.getReferenceById(Long.parseLong(value.id()))
			: new SnsNotification();

		snsNotification.setSnsNotificationType(value.snsNotificationType());
		snsNotification.setSnsNotificationContents(Collections.singletonList(
			JsonConverter.convertToList(value.snsNotificationContents(), SnsNotificationContent.class)));
		return SnsNotificationEndpointDto.fromEntity(snsNotificationRepository.save(snsNotification));
	}

	@Override
	@Transactional
	public void delete(Long id) {
		snsNotificationRepository.deleteById(id);
	}
}
