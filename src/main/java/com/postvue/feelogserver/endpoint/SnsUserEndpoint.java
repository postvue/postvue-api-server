package com.postvue.feelogserver.endpoint;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import com.postvue.feelogserver.domain.snstagposts.SnsTagPost;
import com.postvue.feelogserver.domain.snstags.SnsTag;
import com.postvue.feelogserver.domain.snsusers.SnsUser;
import com.postvue.feelogserver.domain.snsusers.repository.SnsUserRepository;
import com.postvue.feelogserver.endpoint.converter.JpaFilterCustomConverter;
import com.postvue.feelogserver.endpoint.dto.SnsTagEndpointDto;
import com.postvue.feelogserver.endpoint.dto.SnsUserEndpointDto;
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
public class SnsUserEndpoint implements CrudService<SnsUserEndpointDto, Long> {
	private final SnsUserRepository snsUserRepository;
	private final JpaFilterCustomConverter jpaFilterCustomConverter;

	@Override
	@Nonnull
	public List<@Nonnull SnsUserEndpointDto> list(Pageable pageable, @Nullable Filter filter) {
		Specification<SnsUser> spec = filter != null
			? jpaFilterCustomConverter.toSpec(filter, SnsUser.class)
			: Specification.anyOf();
		return snsUserRepository.findAll(spec,pageable).map((SnsUserEndpointDto::fromEntity)).toList();
	}

	@Override
	public @Nullable SnsUserEndpointDto save(SnsUserEndpointDto value) {
		SnsUser snsUser = value.id() != null && Long.parseLong(value.id()) > 0
			? snsUserRepository.getReferenceById(Long.parseLong(value.id()))
			: new SnsUser();

		snsUser.setNickname(value.nickname());
		snsUser.setEmail(value.email());
		snsUser.setUserLink(value.userLink());
		snsUser.setUserDescription(value.userDescription());
		snsUser.setSnsUserGender(value.snsUserGender());
		snsUser.setBirthDate(value.birthDate());
		snsUser.setSnsUserState(value.snsUserState());
		snsUser.setSnsAppRole(value.snsAppRole());
		snsUser.setIsPrivateProfile(value.isPrivateProfile());
		snsUser.setProfilePath(value.profilePath());
		snsUser.setHasFollowerNotification(value.hasFollowerNotification());
		return SnsUserEndpointDto.fromEntity(snsUserRepository.save(snsUser));
	}

	@Override
	public void delete(Long snsUserId) {
		snsUserRepository.deleteById(snsUserId);
	}
}
