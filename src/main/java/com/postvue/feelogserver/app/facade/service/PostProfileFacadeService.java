package com.postvue.feelogserver.app.facade.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.postvue.feelogserver.app.posts.dto.common.Location;
import com.postvue.feelogserver.app.posts.dto.common.PostContent;
import com.postvue.feelogserver.app.posts.dto.rsp.get.SnsPostRsp;
import com.postvue.feelogserver.app.profiles.dto.rsp.create.PostToScrapListRsp;
import com.postvue.feelogserver.domain.snsblockusers.repository.SnsBlockUserRepository;
import com.postvue.feelogserver.domain.snsposts.SnsPost;
import com.postvue.feelogserver.domain.snsposts.dto.SnsPostDao;
import com.postvue.feelogserver.domain.snsposts.repository.SnsPostRepository;
import com.postvue.feelogserver.domain.snspostuserreactions.SnsPostUserReaction;
import com.postvue.feelogserver.domain.snspostuserreactions.repository.SnsPostUserReactionRepository;
import com.postvue.feelogserver.domain.snsscrap.SnsScrap;
import com.postvue.feelogserver.domain.snsscrap.repository.SnsScrapJdbcRepository;
import com.postvue.feelogserver.domain.snsscrap.repository.SnsScrapRepository;
import com.postvue.feelogserver.domain.snsscrapboard.SnsScrapBoard;
import com.postvue.feelogserver.domain.snsscrapboard.repository.SnsScrapBoardRepository;
import com.postvue.feelogserver.domain.snstags.dao.PostTagDao;
import com.postvue.feelogserver.domain.snsusers.SnsUser;
import com.postvue.feelogserver.global.constant.PostConst;
import com.postvue.feelogserver.global.exception.BadRequestErrorException;
import com.postvue.feelogserver.global.exception.ForbiddenErrorException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PostProfileFacadeService {
	private final SnsScrapRepository snsScrapRepository;
	private final SnsPostRepository snsPostRepository;
	private final SnsScrapJdbcRepository snsScrapJdbcRepository;
	private final SnsBlockUserRepository snsBlockUserRepository;
	private final SnsPostUserReactionRepository snsPostUserReactionRepository;
	private final SnsScrapBoardRepository snsScrapBoardRepository;


	@Transactional
	public PostToScrapListRsp createPostToScrapList(Long snsUserId,
		Long postId,
		List<String> scrapIdList, // 새로운 보드 추가
		Boolean isDeleteScrapByNotInclude
	) {
		// List<Long> scrapBoardList = scrapIdList.stream().map(Long::valueOf).toList();
		// 내 보드인지 체크
		List<Long> scrapBoardIdList = snsScrapBoardRepository.findBySnsUserAndSnsScrapBoardIn(snsUserId, scrapIdList.stream().map(Long::valueOf).toList())
			.stream().map(SnsScrapBoard::getId).toList();


		if (scrapBoardIdList.isEmpty()) {
			throw new BadRequestErrorException("스크랩 목록이 없습니다.");
		}

		List<SnsScrap> myScrapList = new ArrayList<>();
		if (isDeleteScrapByNotInclude){
			List<SnsScrap> deleteScrap = new ArrayList<>();

			snsScrapRepository.findBySnsUserAndSnsPost(snsUserId, postId).forEach(snsScrap -> {
				// 추가할려는 보드에 포함되어 있지 않은 보드
				if (!scrapBoardIdList.contains(snsScrap.getSnsScrapBoard().getId())){
					deleteScrap.add(snsScrap);
				}
				else{
					myScrapList.add(snsScrap);
				}
			});

			snsScrapRepository.deleteAllByScrapId(deleteScrap.stream().map(SnsScrap::getId).toList());
		}
		else{
			myScrapList.addAll(snsScrapRepository.findBySnsUserAndSnsPostAndSnsScrapBoardIn(snsUserId, postId,
				scrapBoardIdList));
		}

		List<Long> myScrapBoardIdList = myScrapList.stream()
			.map((snsScrap -> snsScrap.getSnsScrapBoard().getId()))
			.toList();

		List<SnsScrap> notExistedScrapBoradList = new ArrayList<>();

		SnsPost snsPost = snsPostRepository.findById(postId).orElseThrow(
			() -> new BadRequestErrorException("해당 게시물은 없습니다.")
		);

		boolean isBlocked = snsBlockUserRepository.findIsBlockUser(snsUserId, snsPost.getSnsUser().getId());
		if (isBlocked){
			throw new ForbiddenErrorException("비공개 계정에 대해서 포스트를 스크랩 할 수 없습니다.");
		}

		for (Long scrapBoardId : scrapBoardIdList) {
			int myScrapIndex = myScrapBoardIdList.indexOf(scrapBoardId);
			if (myScrapIndex == -1) {
				notExistedScrapBoradList.add(
					SnsScrap.builder()
					.snsScrapBoard(SnsScrapBoard.builder().id(scrapBoardId).build())
					.snsPost(snsPost)
					.snsUser(SnsUser.builder().id(snsUserId).build())
					.build());
			}
		}

		if (!notExistedScrapBoradList.isEmpty()){
			snsScrapJdbcRepository.saveAll(notExistedScrapBoradList);
			// 리액션 반응
			snsPost.setReactionCount((snsPost.getReactionCount() != null ? snsPost.getReactionCount() : 0) + PostConst.POST_REACTION_SCRAP_SCORE);
		}


		SnsPostUserReaction snsPostUserReaction = snsPostUserReactionRepository.findBySnsPostAndSnsUser(postId,
			snsUserId).orElse(
			SnsPostUserReaction.builder()
				.snsPost(SnsPost.builder().id(postId).build())
				.snsUser(SnsUser.builder().id(snsUserId).build())
				.build()
		);
		snsPostUserReaction.setIsClipped(true);
		snsPostUserReaction.setIsClippedAt(LocalDateTime.now());

		snsPostUserReactionRepository.save(snsPostUserReaction);

		return PostToScrapListRsp.builder()
			.scrapIdList(scrapBoardIdList.stream().map(String::valueOf).toList())
			.isClipped(true)
			.build();
	}

	public List<SnsPostRsp> getPostGetRspList(List<SnsPostDao> snsPostDaoList) {
		return snsPostDaoList.stream().map((this::convertToPostGetRsp)).toList();
	}

	public SnsPostRsp convertToPostGetRsp(SnsPostDao snsPostDao) {
		return SnsPostRsp.builder()
			.postId(snsPostDao.getPostId().toString())
			.userId(snsPostDao.getSnsUserId().toString())
			.username(snsPostDao.getUsername())
			.profilePath(snsPostDao.getProfilePath())
			.location(new Location(
				snsPostDao.getLatitude(), snsPostDao.getLongitude(),
				snsPostDao.getAddress(), snsPostDao.getBuildName()))
			.tags(snsPostDao.getStringToTags().stream().map(PostTagDao::getTagName).toList())
			.isFollowed(snsPostDao.getFollowingId() != null)
			.isLiked(snsPostDao.getIsLiked())
			.isClipped(snsPostDao.getIsClipped())
			.isReposted(snsPostDao.getIsReposted())
			.postTitle(snsPostDao.getPostTitle())
			.postBodyText(snsPostDao.getPostBodyText())
			// @REFER: 제거
			// .postCategory(snsPostDao.getPostCategory())
			.followable(snsPostDao.getFollowable())
			.postContents(snsPostDao.getStringToSnsPostContents()
				.stream()
				.map((snsPostContent -> new PostContent(snsPostContent.getPostContentType(),
					Objects.requireNonNullElse(snsPostContent.getBucketUrl(),"") + Objects.requireNonNullElse(snsPostContent.getContent(),""),
					snsPostContent.getAscSortNum(),
					Objects.requireNonNullElse(snsPostContent.getBucketUrl(),"") + Objects.requireNonNullElse(snsPostContent.getPreviewImg(),""),
					snsPostContent.getIsUploaded(),
					snsPostContent.getVideoDuration()
				)))
				.toList())
			.postedAt(snsPostDao.getPostedAt())
			.build();
	}
}
