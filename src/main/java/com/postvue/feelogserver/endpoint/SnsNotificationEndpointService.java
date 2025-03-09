package com.postvue.feelogserver.endpoint;

import java.util.Collections;
import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.postvue.feelogserver.domain.snsnotifications.SnsNotification;
import com.postvue.feelogserver.domain.snsnotifications.repository.SnsNotificationRepository;
import com.postvue.feelogserver.domain.snsnotifications.vo.SnsNotificationContent;
import com.postvue.feelogserver.endpoint.converter.JpaFilterCustomConverter;
import com.postvue.feelogserver.endpoint.dto.SnsNotificationEndpointDto;
import com.postvue.feelogserver.global.util.converter.JsonConverter;
import com.vaadin.hilla.Nullable;
import com.vaadin.hilla.crud.filter.Filter;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SnsNotificationEndpointService {
	private final SnsNotificationRepository snsNotificationRepository;
	private final JpaFilterCustomConverter jpaFilterCustomConverter;

	@Transactional
	public List<SnsNotificationEndpointDto> listProcess(Pageable pageable, @Nullable Filter filter) {
		Specification<SnsNotification> spec = filter != null
			? jpaFilterCustomConverter.toSpec(filter, SnsNotification.class)
			: Specification.anyOf();
		return snsNotificationRepository.findAll(spec,pageable).map((SnsNotificationEndpointDto::fromEntity)).toList();
	}

	@Transactional
	public SnsNotification saveProcess(SnsNotificationEndpointDto value) {
		SnsNotification snsNotification = value.id() != null && Long.parseLong(value.id()) > 0
			? snsNotificationRepository.getReferenceById(Long.parseLong(value.id()))
			: new SnsNotification();

		snsNotification.setSnsNotificationType(value.snsNotificationType());
		snsNotification.setSnsNotificationContents(Collections.singletonList(
			JsonConverter.convertToList(value.snsNotificationContents(), SnsNotificationContent.class)));
		snsNotification.setCreatedAt(value.createdAt());
		return snsNotificationRepository.save(snsNotification);
	}

}
