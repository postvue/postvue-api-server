package com.postvue.feelogserver.endpoint;

import java.util.Arrays;
import java.util.List;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.jose.shaded.gson.Gson;
import com.postvue.feelogserver.app.admin.service.AdminService;
import com.postvue.feelogserver.app.h3.service.H3Service;
import com.postvue.feelogserver.app.messagequeue.service.consumer.VideoConversionConsumer;
import com.postvue.feelogserver.app.openapis.req.DiscordWebhookRequest;
import com.postvue.feelogserver.app.openapis.service.DiscordService;
import com.postvue.feelogserver.app.posts.service.PostsService;
import com.postvue.feelogserver.domain.snsposts.SnsPost;
import com.postvue.feelogserver.domain.snsposts.repository.SnsPostRepository;
import com.postvue.feelogserver.domain.snsposts.vo.SnsPostContent;
import com.postvue.feelogserver.domain.snstags.repository.SnsTagRepository;
import com.postvue.feelogserver.domain.snstags.vo.PostTag;
import com.postvue.feelogserver.domain.snsusers.SnsUser;
import com.postvue.feelogserver.endpoint.converter.JpaFilterCustomConverter;
import com.postvue.feelogserver.endpoint.dto.SnsPostEndPointDto;
import com.postvue.feelogserver.global.constant.AdminConst;
import com.postvue.feelogserver.global.constant.LogTemplateConst;
import com.postvue.feelogserver.global.constant.RabbitMQConst;
import com.postvue.feelogserver.global.exception.BadRequestErrorException;
import com.postvue.feelogserver.global.util.converter.JsonConverter;
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
public class SnsPostEndpoint implements CrudService<SnsPostEndPointDto, Long> {
	private final SnsPostRepository snsPostRepository;
	private final JpaFilterCustomConverter jpaFilterCustomConverter;
	private final PostsService postsService;
	private final AdminService adminService;
	private final H3Service h3Service;
	private final SnsTagRepository snsTagRepository;

	private final GeometryFactory geometryFactory = new GeometryFactory();

	@Override
	@Nonnull
	@Transactional
	public List<@Nonnull SnsPostEndPointDto> list(Pageable pageable, @Nullable Filter filter) {
		Specification<SnsPost> spec = filter != null
			? jpaFilterCustomConverter.toSpec(filter, SnsPost.class)
			: Specification.anyOf();
		List<SnsPost> snsPosts = snsPostRepository.findAll(spec,pageable).stream().toList();
		return snsPosts.stream().map((SnsPostEndPointDto::fromEntity)).toList();
	}

	@Override
	@Transactional
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
		snsPost.setBuildName(value.buildName());
		if (value.latitude() != null && value.longitude() != null) {
			snsPost.setH3Index(h3Service.getLatLngToH3Cell(value.latitude(),value.longitude()));
			snsPost.setGeom(geometryFactory.createPoint(new Coordinate(value.longitude(), value.latitude())));
		}

		snsPost.setIsShowAddress(value.isShowAddress());
		snsPost.setIsRepost(value.isRepost());
		if(value.repostOrigin_id()!=null){
			snsPost.setRepostOrigin(SnsPost.builder().id(Long.parseLong(value.repostOrigin_id())).build());
		}
		snsPost.setTgtAudType(value.tgtAudType());
		snsPost.setPostContentBusinessType(value.postContentBusinessType());

		snsPost.setSnsPostContents(Arrays.asList(JsonConverter.convertToList(value.snsPostContents(),SnsPostContent[].class)));

		List<PostTag> postTagList = Arrays.asList(JsonConverter.convertToList(value.tags(),PostTag[].class));
		if (snsTagRepository.countByIds(postTagList.stream().map(PostTag::getTagId).toList()) == postTagList.size()){
			snsPost.setTags(postTagList);
		}
		else{
			throw new BadRequestErrorException("개수가 맞지 않습니다.");
		}

		if(value.deletedAt() != null){
			postsService.deletePostBySnsPostIdByAdmin(snsPost);
		}
		else{
			postsService.recoverPostBySnsPostIdByAdmin(snsPost);
		}

		try{
			return SnsPostEndPointDto.fromEntity(snsPostRepository.save(snsPost));
		}
		catch (Exception e){
			adminService.sendSaveAndUpdateErrorMsgToDiscord(e);
			throw e;
		}
	}

	@Override
	@Transactional
	public void delete(Long id) {
		return;
	}
}
