package com.postvue.feelogserver.app.recomm.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.postvue.feelogserver.app.recomm.dto.GetPostContent;
import com.postvue.feelogserver.app.recomm.dto.rsp.GetRecommFollowRsp;
import com.postvue.feelogserver.app.recomm.dto.rsp.GetRecommTagRsp;
import com.postvue.feelogserver.app.recomm.dto.rsp.SnsRecommTagDaoImpl;
import com.postvue.feelogserver.domain.adminserviceadjustments.AdminServiceAdjustment;
import com.postvue.feelogserver.domain.adminserviceadjustments.repository.AdminServiceAdjustmentRepository;
import com.postvue.feelogserver.domain.snsposts.dto.SnsPostContentDao;
import com.postvue.feelogserver.domain.snsposts.vo.PostContentType;
import com.postvue.feelogserver.domain.snstagposts.dao.SnsRecommTagDao;
import com.postvue.feelogserver.domain.snstagposts.respository.SnsTagPostRepository;
import com.postvue.feelogserver.domain.snstags.repository.SnsTagRepository;
import com.postvue.feelogserver.domain.snsuserfollows.dao.FollowRecommInfoDao;
import com.postvue.feelogserver.domain.snsuserfollows.repository.SnsUserFollowRepository;
import com.postvue.feelogserver.global.admin.service.recommfavoritetag.RecommFavoriteTagServiceInfo;
import com.postvue.feelogserver.global.admin.service.recommfollow.RecommFollowServiceInfo;
import com.postvue.feelogserver.global.admin.service.recommtag.RecommTagServiceInfo;
import com.postvue.feelogserver.global.constant.PageConfigConst;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Service
@RequiredArgsConstructor
public class RecommService {
	private final SnsUserFollowRepository snsUserFollowRepository;
	private final SnsTagPostRepository snsTagPostRepository;
	private final AdminServiceAdjustmentRepository adminServiceAdjustmentRepository;
	private final SnsTagRepository snsTagRepository;

	@Transactional
	public List<GetRecommFollowRsp> findRecommFollowList(Long userId) {
		List<FollowRecommInfoDao> followRecommInfoDaos = snsUserFollowRepository.selectRecommendFollowPostList(userId, PageConfigConst.PAGE_NUM_BY_FOLLOW_RECOMM);

		// 조정 팔로우 리스트
		Set<Long> ids = followRecommInfoDaos.stream()
			.map(FollowRecommInfoDao::getSnsUserId)
			.collect(Collectors.toSet());

		// 이미 가져온 ID와 겹치지 않는 요소 필터링
		List<Long> recommFollowListByAdmin = adminServiceAdjustmentRepository.findAllByServiceType(
				RecommFollowServiceInfo.SERVICE_TYPE_NAME).stream().map(AdminServiceAdjustment::getPropLong1id)
			.filter(id -> !ids.contains(id))
			.toList();


		if(!recommFollowListByAdmin.isEmpty()){
			followRecommInfoDaos.addAll(snsUserFollowRepository.selectRecommendFollowListByAdmin(recommFollowListByAdmin, userId));
		}

		return followRecommInfoDaos.stream().filter(followRecommInfoDao -> !Objects.equals(
			followRecommInfoDao.getSnsUserId(), userId)).map((followRecommInfoDao -> {
			List<GetPostContent> getPostContents = followRecommInfoDao.getPostIdContents()
				.stream()
				.map((followPostIdContentsDao -> {
					SnsPostContentDao snsPostContentDao = followPostIdContentsDao.getPostContents().get(0);

					String content =  snsPostContentDao.getPostContentType() == PostContentType.VIDEO
						? snsPostContentDao.getPreviewImg() : snsPostContentDao.getContent();
					return GetPostContent.builder()
						.postId(followPostIdContentsDao.getPostId().toString())
						.content(Objects.requireNonNullElse(snsPostContentDao.getBucketUrl(),"") + content)
						.postContentType(snsPostContentDao.getPostContentType())
						.build();
				}))
				.toList();

			return GetRecommFollowRsp.builder()
				.followId(followRecommInfoDao.getSnsUserId().toString())
				.username(followRecommInfoDao.getUsername())
				.followerNum(followRecommInfoDao.getFollowerNum())
				.followingNum(followRecommInfoDao.getFollowingNum())
				.profilePath(followRecommInfoDao.getProfilePath())
				.postPreviewImgUrlList(getPostContents)
				.build();
		})).toList();
	}

	@Transactional
	public List<GetRecommFollowRsp> findRecommFollowListV1(Long userId, Integer page) {
		List<FollowRecommInfoDao> followRecommInfoDaos = snsUserFollowRepository.selectRecommendFollowPostListV1(
			userId, page * PageConfigConst.PAGE_NUM_BY_FOLLOW_RECOMM,
			PageConfigConst.PAGE_NUM_BY_FOLLOW_RECOMM);

		// 조정 팔로우 리스트
		Set<Long> ids = followRecommInfoDaos.stream()
			.map(FollowRecommInfoDao::getSnsUserId)
			.collect(Collectors.toSet());

		// 이미 가져온 ID와 겹치지 않는 요소 필터링
		List<Long> recommFollowListByAdmin = adminServiceAdjustmentRepository.findAllByServiceType(
				RecommFollowServiceInfo.SERVICE_TYPE_NAME).stream().map(AdminServiceAdjustment::getPropLong1id)
			.filter(id -> !ids.contains(id))
			.toList();


		if(!recommFollowListByAdmin.isEmpty()){
			followRecommInfoDaos.addAll(snsUserFollowRepository.selectRecommendFollowListByAdmin(recommFollowListByAdmin, userId));
		}

		return followRecommInfoDaos.stream().filter(followRecommInfoDao -> !Objects.equals(
			followRecommInfoDao.getSnsUserId(), userId)).map((followRecommInfoDao -> {
			List<GetPostContent> getPostContents = followRecommInfoDao.getPostIdContents()
				.stream()
				.map((followPostIdContentsDao -> {
					SnsPostContentDao snsPostContentDao = followPostIdContentsDao.getPostContents().get(0);

					String content =  snsPostContentDao.getPostContentType() == PostContentType.VIDEO
						? snsPostContentDao.getPreviewImg() : snsPostContentDao.getContent();
					return GetPostContent.builder()
						.postId(followPostIdContentsDao.getPostId().toString())
						.content(Objects.requireNonNullElse(snsPostContentDao.getBucketUrl(),"") + content)
						.postContentType(snsPostContentDao.getPostContentType())
						.build();
				}))
				.toList();

			return GetRecommFollowRsp.builder()
				.followId(followRecommInfoDao.getSnsUserId().toString())
				.username(followRecommInfoDao.getUsername())
				.followerNum(followRecommInfoDao.getFollowerNum())
				.followingNum(followRecommInfoDao.getFollowingNum())
				.profilePath(followRecommInfoDao.getProfilePath())
				.postPreviewImgUrlList(getPostContents)
				.build();
		})).toList();
	}

	@Transactional
	public List<GetRecommTagRsp> findRecommTagList(Long userId) {
		List<SnsRecommTagDaoImpl> snsRecommTagDaoList = new ArrayList<>(
			snsTagPostRepository.findRecommTagList(userId, LocalDateTime.now(),
					PageConfigConst.PAGE_NUM_BY_POPULAR,
					PageConfigConst.PAGE_NUM_BY_INTEREST).stream().map((snsRecommTagDao ->
					SnsRecommTagDaoImpl
						.builder()
						.tagId(snsRecommTagDao.getTagId())
						.tagName(snsRecommTagDao.getTagName())
						.tagRepsBatchContent(snsRecommTagDao.getTagRepsBatchContent())
						.tagRepsBatchContentType(snsRecommTagDao.getTagRepsBatchContentType())
						.build()))
				.toList());


		// 조정 태그 리스트
		Set<Long> tagIds = snsRecommTagDaoList.stream()
			.map(SnsRecommTagDao::getTagId)
			.collect(Collectors.toSet());

		// 이미 가져온 ID와 겹치지 않는 요소 필터링
		List<Long> recommTagListByAdmin = adminServiceAdjustmentRepository.findAllByServiceType(
				RecommTagServiceInfo.SERVICE_TYPE_NAME).stream().map(AdminServiceAdjustment::getPropLong1id)
			.filter(id -> !tagIds.contains(id))
			.toList();


		if(!recommTagListByAdmin.isEmpty()){
			snsRecommTagDaoList.addAll(snsTagRepository.findAllByIdIn(recommTagListByAdmin).stream().map(snsTag ->
				SnsRecommTagDaoImpl
					.builder()
					.tagId(snsTag.getId())
					.tagName(snsTag.getTagName())
					.tagRepsBatchContent(snsTag.getTagRepsBatchContent())
					.tagRepsBatchContentType(snsTag.getTagRepsBatchContentType().toString())
					.build()
			).toList());
		}

		Collections.shuffle(snsRecommTagDaoList);


		return snsRecommTagDaoList.subList(0, Math.min(PageConfigConst.PAGE_NUM_BY_TAG_RECOMM, snsRecommTagDaoList.size()))
			.stream().map((snsRecommTagDao ->
				GetRecommTagRsp.builder()
					.tagId(snsRecommTagDao.getTagId().toString())
					.tagName(snsRecommTagDao.getTagName())
					.tagBkgdContent(snsRecommTagDao.getTagRepsBatchContent())
					.tagBkgdContentType(snsRecommTagDao.getTagRepsBatchContentType())
					.build()))
			.toList();
	}

	@Transactional
	public List<GetRecommTagRsp> findRecommTagListV2(Long userId) {
		List<SnsRecommTagDaoImpl> snsRecommTagDaoList = new ArrayList<>();

		List<Long> recommTagListByAdmin = adminServiceAdjustmentRepository.findAllByServiceType(
				RecommTagServiceInfo.SERVICE_TYPE_NAME).stream().map(AdminServiceAdjustment::getPropLong1id)
			.toList();

		System.out.println("한텐:"+recommTagListByAdmin.size() + (!recommTagListByAdmin.isEmpty() ? recommTagListByAdmin.get(0) : "킹"));

		if(!recommTagListByAdmin.isEmpty()){
			snsRecommTagDaoList.addAll(snsTagRepository.findAllByIdIn(recommTagListByAdmin).stream().map(snsTag ->
				SnsRecommTagDaoImpl
					.builder()
					.tagId(snsTag.getId())
					.tagName(snsTag.getTagName())
					.tagRepsBatchContent(snsTag.getTagRepsBatchContent())
					.tagRepsBatchContentType(snsTag.getTagRepsBatchContentType().toString())
					.build()
			).toList());
		}

		// 조정 태그 리스트
		Set<Long> tagIds = snsRecommTagDaoList.stream()
			.map(SnsRecommTagDao::getTagId)
			.collect(Collectors.toSet());

		snsRecommTagDaoList.addAll((new ArrayList<>(
			snsTagPostRepository.findRecommTagList(userId, LocalDateTime.now(),
					PageConfigConst.PAGE_NUM_BY_POPULAR,
					PageConfigConst.PAGE_NUM_BY_INTEREST).stream().map((snsRecommTagDao ->
					SnsRecommTagDaoImpl
						.builder()
						.tagId(snsRecommTagDao.getTagId())
						.tagName(snsRecommTagDao.getTagName())
						.tagRepsBatchContent(snsRecommTagDao.getTagRepsBatchContent())
						.tagRepsBatchContentType(snsRecommTagDao.getTagRepsBatchContentType())
						.build()))
				.filter(snsRecommTagDaoImpl -> !tagIds.contains(snsRecommTagDaoImpl.getTagId()))
				.toList())
			));


		return snsRecommTagDaoList.subList(0, Math.min(PageConfigConst.PAGE_NUM_BY_TAG_RECOMM, snsRecommTagDaoList.size()))
			.stream().map((snsRecommTagDao ->
			GetRecommTagRsp.builder()
				.tagId(snsRecommTagDao.getTagId().toString())
				.tagName(snsRecommTagDao.getTagName())
				.tagBkgdContent(snsRecommTagDao.getTagRepsBatchContent())
				.tagBkgdContentType(snsRecommTagDao.getTagRepsBatchContentType())
				.build()))
			.toList();
	}

	@Transactional
	public List<GetRecommTagRsp> findRecommFavoriteTagListV2(Integer page) {
		List<GetRecommTagRsp> recommTagRspList = new ArrayList<>();

		List<Long> recommTagListByAdmin = adminServiceAdjustmentRepository.findAllByServiceType(
				RecommFavoriteTagServiceInfo.SERVICE_TYPE_NAME).stream().map(AdminServiceAdjustment::getPropLong1id)
			.toList();

		if(!recommTagListByAdmin.isEmpty()){
			recommTagRspList.addAll(snsTagRepository.findAllByIdIn(recommTagListByAdmin).stream().map(snsTag ->
				GetRecommTagRsp
					.builder()
					.tagId(snsTag.getId().toString())
					.tagName(snsTag.getTagName())
					.tagBkgdContent(snsTag.getTagRepsBatchContent())
					.tagBkgdContentType(snsTag.getTagRepsBatchContentType().toString())
					.build()
			).toList());
		}

		if (page <= 0){
			return recommTagRspList;
		}
		else{
			List<SnsRecommTagDao> snsRecommTagDaoList = snsTagPostRepository.findPopularTagListByPageable(
				LocalDateTime.now(),
				(page - 1) * PageConfigConst.POPULAR_TAG_PAGE_SIZE,
				PageConfigConst.POPULAR_TAG_PAGE_SIZE);

			return new ArrayList<>(snsRecommTagDaoList.stream()
				.filter(getRecommTagRsp -> !recommTagListByAdmin.contains(getRecommTagRsp.getTagId()))
				.map((snsRecommTagDao ->
				GetRecommTagRsp.builder()
					.tagName(snsRecommTagDao.getTagName())
					.tagId(snsRecommTagDao.getTagId().toString())
					.tagBkgdContent(snsRecommTagDao.getTagRepsBatchContent())
					.tagBkgdContentType(snsRecommTagDao.getTagRepsBatchContentType())
					.build()))
				.toList());
		}
	}


	@Transactional
	public List<GetRecommTagRsp> findRecommFavoriteTagList(Integer page) {
		List<SnsRecommTagDao> snsRecommTagDaoList = snsTagPostRepository.findPopularTagListByPageable(
			LocalDateTime.now(),
			page * PageConfigConst.POPULAR_TAG_PAGE_SIZE,
			PageConfigConst.POPULAR_TAG_PAGE_SIZE);

		List<GetRecommTagRsp> recommTagRspList = new ArrayList<>(snsRecommTagDaoList.stream().map((snsRecommTagDao ->
			GetRecommTagRsp.builder()
				.tagName(snsRecommTagDao.getTagName())
				.tagId(snsRecommTagDao.getTagId().toString())
				.tagBkgdContent(snsRecommTagDao.getTagRepsBatchContent())
				.tagBkgdContentType(snsRecommTagDao.getTagRepsBatchContentType())
				.build())).toList());

		if (page > 0){
			return recommTagRspList;
		}

		// 조정 태그 리스트
		Set<Long> tagIds =  snsRecommTagDaoList.stream()
			.map(SnsRecommTagDao::getTagId)
			.collect(Collectors.toSet());


		// 이미 가져온 ID와 겹치지 않는 요소 필터링
		List<Long> recommTagListByAdmin = adminServiceAdjustmentRepository.findAllByServiceType(
				RecommFavoriteTagServiceInfo.SERVICE_TYPE_NAME).stream().map(AdminServiceAdjustment::getPropLong1id)
			.filter(id -> !tagIds.contains(id))
			.toList();


		if(!recommTagListByAdmin.isEmpty()){
			recommTagRspList.addAll(snsTagRepository.findAllByIdIn(recommTagListByAdmin).stream().map(snsTag ->
				GetRecommTagRsp
					.builder()
					.tagId(snsTag.getId().toString())
					.tagName(snsTag.getTagName())
					.tagBkgdContent(snsTag.getTagRepsBatchContent())
					.tagBkgdContentType(snsTag.getTagRepsBatchContentType().toString())
					.build()
			).toList());
		}

		return recommTagRspList;
	}

}
