package com.postvue.feelogserver.endpoint;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import com.postvue.feelogserver.domain.snsposts.SnsPost;
import com.postvue.feelogserver.domain.snsposts.repository.SnsPostRepository;
import com.postvue.feelogserver.domain.snsposts.vo.SnsPostContent;
import com.postvue.feelogserver.domain.snstags.vo.PostTag;
import com.postvue.feelogserver.domain.snsusers.SnsUser;
import com.postvue.feelogserver.endpoint.converter.JpaFilterCustomConverter;
import com.postvue.feelogserver.endpoint.dto.SnsPostEndPointDto;
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
public class SnsPostEndpoint implements CrudService<SnsPostEndPointDto, Long> {
	private final SnsPostRepository snsPostRepository;
	private final JpaFilterCustomConverter jpaFilterCustomConverter;

	@Override
	@Nonnull
	public List<@Nonnull SnsPostEndPointDto> list(Pageable pageable, @Nullable Filter filter) {
		Specification<SnsPost> spec = filter != null
			? jpaFilterCustomConverter.toSpec(filter, SnsPost.class)
			: Specification.anyOf();
		List<SnsPost> snsPosts = snsPostRepository.findAll(spec,pageable).stream().toList();
		return snsPosts.stream().map((SnsPostEndPointDto::fromEntity)).toList();
	}

	@Override
	public @Nullable SnsPostEndPointDto save(SnsPostEndPointDto value) {
		SnsPost snsPost = value.id() != null && Long.parseLong(value.id()) > 0
			? snsPostRepository.getReferenceById(Long.parseLong(value.id()))
			: new SnsPost();

		snsPost.setSnsUser(SnsUser.builder().id(Long.parseLong(value.snsUser_id())).build());
		snsPost.setIsExposed(value.isExposed());
		snsPost.setPostTitle(value.postTitle());
		snsPost.setPostBodyText(value.postBodyText());
		snsPost.setPostBodyText(value.postBodyText());
		snsPost.setPostCaptionContent(value.postCaptionContent());
		snsPost.setLatitude(value.latitude());
		snsPost.setLongitude(value.longitude());
		snsPost.setAddress(value.address());
		snsPost.setIsShowAddress(value.isShowAddress());
		snsPost.setIsRepost(value.isRepost());
		if(value.repostOrigin_id()!=null){
			snsPost.setRepostOrigin(SnsPost.builder().id(Long.parseLong(value.repostOrigin_id())).build());
		}
		snsPost.setTgtAudType(value.tgtAudType());
		snsPost.setPostContentBusinessType(value.postContentBusinessType());

		snsPost.setSnsPostContents(JsonConverter.convertToList(value.snsPostContents(),SnsPostContent.class));
		snsPost.setTags(JsonConverter.convertToList(value.tags(),PostTag.class));

		try{
			return SnsPostEndPointDto.fromEntity(snsPostRepository.save(snsPost));
		}
		catch (Exception e){
			throw e;
		}

	}

	@Override
	public void delete(Long id) {

		snsPostRepository.deleteById(id);
	}
}
