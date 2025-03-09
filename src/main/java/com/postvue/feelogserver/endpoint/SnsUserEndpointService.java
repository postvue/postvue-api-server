package com.postvue.feelogserver.endpoint;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;

import com.postvue.feelogserver.domain.snsusers.SnsUser;
import com.postvue.feelogserver.domain.snsusers.repository.SnsUserRepository;
import com.postvue.feelogserver.domain.snsusers.vo.SnsUserState;
import com.postvue.feelogserver.endpoint.converter.JpaFilterCustomConverter;
import com.postvue.feelogserver.endpoint.dto.SnsUserEndpointDto;
import com.postvue.feelogserver.global.util.validation.StringValidUtil;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import com.vaadin.hilla.BrowserCallable;
import com.vaadin.hilla.Nullable;
import com.vaadin.hilla.crud.filter.Filter;

import lombok.RequiredArgsConstructor;

@Controller
@BrowserCallable
@AnonymousAllowed
@RequiredArgsConstructor
public class SnsUserEndpointService {
	private final SnsUserRepository snsUserRepository;
	private final JpaFilterCustomConverter jpaFilterCustomConverter;

	@Transactional
	public List<SnsUser> listProcess(Pageable pageable, @Nullable Filter filter) {
		Specification<SnsUser> spec = filter != null
			? jpaFilterCustomConverter.toSpec(filter, SnsUser.class)
			: Specification.anyOf();
		return snsUserRepository.findAll(spec,pageable).toList();
	}

	@Transactional
	public SnsUser saveProcess(SnsUserEndpointDto value) {
		SnsUser snsUser = value.id() != null && Long.parseLong(value.id()) > 0
			? snsUserRepository.getReferenceById(Long.parseLong(value.id()))
			: new SnsUser();

		snsUser.setNickname(StringValidUtil.nullIfEmpty(value.nickname()));
		snsUser.setEmail(StringValidUtil.nullIfEmpty(value.email()));
		snsUser.setUserLink(StringValidUtil.nullIfEmpty(value.userLink()));
		snsUser.setUserDescription(StringValidUtil.nullIfEmpty(value.userDescription()));
		snsUser.setSnsUserGender(value.snsUserGender());
		snsUser.setBirthDate(value.birthDate());
		snsUser.setSnsAppRole(value.snsAppRole());
		snsUser.setIsPrivateProfile(value.isPrivateProfile());
		snsUser.setProfilePath(StringValidUtil.nullIfEmpty(value.profilePath()));
		snsUser.setHasFollowerNotification(value.hasFollowerNotification());
		if (value.snsUserState() == SnsUserState.DELETED || value.snsUserState() == SnsUserState.FULL_DELETED) {
			snsUser.setDeletedAt(LocalDateTime.now());
			snsUser.setSnsUserState(value.snsUserState());
		} else {
			snsUser.setDeletedAt(null);
			snsUser.setSnsUserState(value.snsUserState());
		}

		snsUser.setCreatedAt(value.createdAt());
		snsUser.setLastUpdatedAt(value.lastUpdatedAt());
		snsUser.setLastUpdatedByid(
			StringValidUtil.isNotBlank(value.lastUpdatedByid()) ? Long.parseLong(value.lastUpdatedByid()) : null);

		snsUserRepository.save(snsUser);

		return snsUser;
	}
}
