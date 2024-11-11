package com.postvue.feelogserver.app.recomm.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.postvue.feelogserver.app.recomm.dto.GetPostContent;
import com.postvue.feelogserver.app.recomm.dto.rsp.GetRecommFollowRsp;
import com.postvue.feelogserver.app.recomm.dto.rsp.GetRecommTagRsp;
import com.postvue.feelogserver.domain.snsposts.dto.SnsPostContentDao;
import com.postvue.feelogserver.domain.snstagposts.dao.SnsRecommTagDao;
import com.postvue.feelogserver.domain.snstagposts.respository.SnsTagPostRepository;
import com.postvue.feelogserver.domain.snsuserfollows.dao.FollowRecommInfoDao;
import com.postvue.feelogserver.domain.snsuserfollows.repository.SnsUserFollowRepository;
import com.postvue.feelogserver.global.constant.FollowConst;
import com.postvue.feelogserver.global.constant.PageConfigConst;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RecommService {
	private final SnsUserFollowRepository snsUserFollowRepository;
	private final SnsTagPostRepository snsTagPostRepository;

	@Transactional
	public List<GetRecommFollowRsp> findRecommFollowList(Long userId) {
		List<Long> fixedSuggestFollowList = new ArrayList<>(FollowConst.FIXED_SUGGEST_FOLLOW_LIST).stream().filter(
				(fixedSuggestFollow) -> !fixedSuggestFollow.equals(userId))
			.toList();

		List<FollowRecommInfoDao> followRecommInfoDaos = snsUserFollowRepository.selectRecommendFollowPostList(
			fixedSuggestFollowList, userId);

		return followRecommInfoDaos.stream().map((followRecommInfoDao -> {
			List<GetPostContent> getPostContents = followRecommInfoDao.getPostIdContents()
				.stream()
				.map((followPostIdContentsDao -> {
					SnsPostContentDao snsPostContentDao = followPostIdContentsDao.getPostContents().get(0);
					return GetPostContent.builder()
						.postId(followPostIdContentsDao.getPostId().toString())
						.content(snsPostContentDao.getContent())
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

	public List<GetRecommTagRsp> findRecommTagList(Long userId) {
		List<SnsRecommTagDao> snsRecommTagDaoList = snsTagPostRepository.findRecommTagList(userId, LocalDateTime.now(),
			PageConfigConst.PAGE_NUM_BY_POPULAR,
			PageConfigConst.PAGE_NUM_BY_INTEREST);
		return snsRecommTagDaoList.stream().map((snsRecommTagDao ->
			GetRecommTagRsp.builder()
				.tagName(snsRecommTagDao.getTagName())
				.tagId(snsRecommTagDao.getTagId().toString())
				.tagBkgdContent(snsRecommTagDao.getTagRepsBatchContent())
				.tagBkgdContentType(snsRecommTagDao.getTagRepsBatchContentType())
				.build())).toList();
	}

	//@REFER: 잘 되는 지 확인
	public List<GetRecommTagRsp> findRecommFavoriteTagList(Integer page) {
		List<SnsRecommTagDao> snsRecommTagDaoList = snsTagPostRepository.findPopularTagListByPageable(
			LocalDateTime.now(),
			page * PageConfigConst.POPULAR_TAG_PAGE_SIZE,
			PageConfigConst.POPULAR_TAG_PAGE_SIZE);
		return snsRecommTagDaoList.stream().map((snsRecommTagDao ->
			GetRecommTagRsp.builder()
				.tagName(snsRecommTagDao.getTagName())
				.tagId(snsRecommTagDao.getTagId().toString())
				.tagBkgdContent(snsRecommTagDao.getTagRepsBatchContent())
				.tagBkgdContentType(snsRecommTagDao.getTagRepsBatchContentType())
				.build())).toList();
	}
}
