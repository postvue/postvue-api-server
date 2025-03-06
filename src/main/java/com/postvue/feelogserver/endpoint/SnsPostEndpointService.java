package com.postvue.feelogserver.endpoint;

import java.util.Arrays;
import java.util.List;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.postvue.feelogserver.app.admin.service.AdminService;
import com.postvue.feelogserver.app.h3.service.H3Service;
import com.postvue.feelogserver.app.posts.service.PostsService;
import com.postvue.feelogserver.domain.snsposts.SnsPost;
import com.postvue.feelogserver.domain.snsposts.repository.SnsPostRepository;
import com.postvue.feelogserver.domain.snsposts.vo.SnsPostContent;
import com.postvue.feelogserver.domain.snstagposts.SnsTagPost;
import com.postvue.feelogserver.domain.snstagposts.respository.SnsTagPostJdbcRepository;
import com.postvue.feelogserver.domain.snstags.SnsTag;
import com.postvue.feelogserver.domain.snstags.repository.SnsTagRepository;
import com.postvue.feelogserver.domain.snstags.vo.PostTag;
import com.postvue.feelogserver.domain.snsusers.SnsUser;
import com.postvue.feelogserver.endpoint.converter.JpaFilterCustomConverter;
import com.postvue.feelogserver.endpoint.dto.SnsPostEndPointDto;
import com.postvue.feelogserver.global.exception.BadRequestErrorException;
import com.postvue.feelogserver.global.util.converter.JsonConverter;
import com.vaadin.hilla.Nullable;
import com.vaadin.hilla.crud.filter.Filter;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class SnsPostEndpointService {
	private final SnsPostRepository snsPostRepository;
	private final JpaFilterCustomConverter jpaFilterCustomConverter;
	private final PostsService postsService;
	private final AdminService adminService;
	private final H3Service h3Service;
	private final SnsTagRepository snsTagRepository;
	private final SnsTagPostJdbcRepository snsTagPostJdbcRepository;

	private final GeometryFactory geometryFactory = new GeometryFactory();

	public List<SnsPostEndPointDto> listProcess(Pageable pageable, @Nullable Filter filter) {
		Specification<SnsPost> spec = filter != null
			? jpaFilterCustomConverter.toSpec(filter, SnsPost.class)
			: Specification.anyOf();
		return snsPostRepository.findAll(spec,pageable).map((SnsPostEndPointDto::fromEntity)).toList();
	}

	@Transactional
	public SnsPost saveProcess(SnsPostEndPointDto value) {
		SnsPost snsPost = value.id() != null && Long.parseLong(value.id()) > 0
			? snsPostRepository.getReferenceById(Long.parseLong(value.id()))
			: null;

		if (snsPost == null){
			throw new BadRequestErrorException("현재 어드민에서는 포스트 게시물을 생성할 수 없습니다.");
		}

		snsPost.setSnsUser(SnsUser.builder().id(Long.parseLong(value.snsUser_id())).build());
		snsPost.setIsExposed(value.isExposed());
		snsPost.setPostTitle(value.postTitle());
		snsPost.setPostBodyText(value.postBodyText());
		snsPost.setLatitude(value.latitude());
		snsPost.setReactionCount(value.reactionCount());
		snsPost.setLongitude(value.longitude());
		snsPost.setAddress(value.address());
		snsPost.setBuildName(value.buildName());
		snsPost.setLastUpdatedAt(value.lastUpdatedAt());
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
		snsPost.setCreatedAt(value.createdAt());

		snsPost.setSnsPostContents(Arrays.asList(JsonConverter.convertToList(value.snsPostContents(),SnsPostContent[].class)));

		List<PostTag> postTagList = Arrays.asList(JsonConverter.convertToList(value.tags(),PostTag[].class));
		if (snsTagRepository.countByIds(postTagList.stream().map(PostTag::getTagId).toList()) == postTagList.size()){
			snsPost.setTags(postTagList);

			snsTagPostJdbcRepository.deleteByPostId(snsPost.getId());

			snsTagPostJdbcRepository.saveAll(postTagList.stream().map(postTag -> SnsTagPost.builder()
				.snsTag(SnsTag.builder().id(postTag.getTagId()).build())
				.snsPost(snsPost)
				.build()).toList());
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
			return snsPostRepository.save(snsPost);
		}
		catch (Exception e){
			adminService.sendSaveAndUpdateErrorMsgToDiscord(e);
			throw e;
		}
	}

}
