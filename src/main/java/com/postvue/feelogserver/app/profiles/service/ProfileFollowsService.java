package com.postvue.feelogserver.app.profiles.service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.postvue.feelogserver.app.common.rsp.SnsPostFollowsGetRsp;
import com.postvue.feelogserver.app.notifications.service.NotificationService;
import com.postvue.feelogserver.domain.snsblockusers.repository.SnsBlockUserRepository;
import com.postvue.feelogserver.domain.snsuserfollows.SnsUserFollow;
import com.postvue.feelogserver.domain.snsuserfollows.dao.ProfileFollowDao;
import com.postvue.feelogserver.domain.snsuserfollows.repository.SnsUserFollowJdbcRepository;
import com.postvue.feelogserver.domain.snsuserfollows.repository.SnsUserFollowRepository;
import com.postvue.feelogserver.domain.snsuserfollowstatistics.SnsUserFollowStatistic;
import com.postvue.feelogserver.domain.snsuserfollowstatistics.repository.SnsUserFollowStatisticJdbcRepository;
import com.postvue.feelogserver.domain.snsuserfollowstatistics.repository.SnsUserFollowStatisticRepository;
import com.postvue.feelogserver.domain.snsusers.SnsUser;
import com.postvue.feelogserver.domain.snsusers.repository.SnsUserRepository;
import com.postvue.feelogserver.global.constant.PageConfigConst;
import com.postvue.feelogserver.global.constant.SystemPhraseConst;
import com.postvue.feelogserver.global.exception.BadRequestErrorException;
import com.postvue.feelogserver.global.exception.ForbiddenErrorException;
import com.postvue.feelogserver.global.exception.UnauthorizedErrorException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProfileFollowsService {
	private final SnsUserFollowRepository snsUserFollowRepository;
	private final SnsUserFollowStatisticRepository snsUserFollowStatisticRepository;
	private final SnsUserFollowJdbcRepository snsUserFollowJdbcRepository;
	private final SnsUserRepository snsUserRepository;
	private final SnsBlockUserRepository snsBlockUserRepository;
	private final NotificationService notificationService;
	private final SnsUserFollowStatisticJdbcRepository snsUserFollowStatisticJdbcRepository;

	@Transactional
	public List<SnsPostFollowsGetRsp> getMyProfileFollowing(Long myUserId, Integer page) {
		if (myUserId == null){
			throw new UnauthorizedErrorException(SystemPhraseConst.UNAUTHORIZED_EXCEPTION_PHRASE);
		}
		SnsUser snsUser = snsUserRepository.findById(myUserId).orElseThrow(
			() -> new BadRequestErrorException("해당 계정은 없습니다.")
		);
		return getProfileFollowing(myUserId, snsUser.getUsername(), page);
	}

	@Transactional
	public List<SnsPostFollowsGetRsp> getProfileFollowing(Long myUserId, String username, Integer page) {
		List<ProfileFollowDao> profileFollowerDaoList = snsUserFollowRepository.selectAllFollowingListByPageable(
			myUserId,
			username, page * PageConfigConst.MY_PROFILE_FOLLOW_PAGE_SIZE, PageConfigConst.MY_PROFILE_FOLLOW_PAGE_SIZE);

		return getPostFollowList(profileFollowerDaoList);
	}

	@Transactional
	public List<SnsPostFollowsGetRsp> getProfileFollower(Long myUserId, String username, Integer page) {

		// userId => 차단 또는 숨긴 유저는 안 보여주도록
		List<ProfileFollowDao> profileFollowerDaoList = snsUserFollowRepository.selectAllFollowerListByPageable(
			myUserId, username,
			page * PageConfigConst.MY_PROFILE_FOLLOW_PAGE_SIZE, PageConfigConst.MY_PROFILE_FOLLOW_PAGE_SIZE);

		return getPostFollowList(profileFollowerDaoList);
	}

	@Transactional
	public Boolean createFollow(Long userId, Long followId) {
		if (Objects.equals(followId, userId)) {
			throw new BadRequestErrorException("나 자신을 팔로우 할 수 없습니다.");
		}
		SnsUser myUser = snsUserRepository.findById(userId).orElseThrow(
			() -> new BadRequestErrorException("해당 유저는 없습니다.")
		);

		Optional<SnsUserFollow> snsUserFollowOpt = snsUserFollowRepository.findByFollowerUseAndFollowingUser(userId,followId);

		if (snsUserFollowOpt.isPresent()){
			return true;
		}

		SnsUserFollow newSnsUserFollow = SnsUserFollow.builder()
			.followerUser(myUser)
			.followingUser(snsUserRepository.findById(followId).orElseThrow(
				() -> new BadRequestErrorException("팔로우할 유저가 없습니다.")
			))
			.build();

		if (snsBlockUserRepository.findIsBlock(followId, userId)) {
			throw new ForbiddenErrorException("현재 이 계정은 팔로우할 수 없습니다.");
		}

		SnsUserFollow snsUserFollow = snsUserFollowRepository.save(newSnsUserFollow);

		//알림, 알림 설정 했을 경우
		if (snsUserFollow.getFollowingUser().getHasFollowerNotification()) {
			notificationService.processFollowerNotification(snsUserFollow);
		}

		SnsUserFollowStatistic snsUserFollowStatisticByMe = snsUserFollowStatisticRepository.findBySnsUser_id(userId).orElse(
			SnsUserFollowStatistic.builder()
				.snsUser(myUser)
				.followerNum(0)
				.followingNum(0)
				.build()
		);

		SnsUserFollowStatistic snsUserFollowStatisticByFollow = snsUserFollowStatisticRepository.findBySnsUser_id(followId).orElse(
			SnsUserFollowStatistic.builder()
				.snsUser(SnsUser.builder().id(followId).build())
				.followerNum(0)
				.followingNum(0)
				.build()
		);

		snsUserFollowStatisticByMe.setFollowingNum(snsUserFollowStatisticByMe.getFollowingNum() + 1);
		snsUserFollowStatisticRepository.save(snsUserFollowStatisticByMe);

		snsUserFollowStatisticByFollow.setFollowerNum(snsUserFollowStatisticByFollow.getFollowerNum() + 1);
		snsUserFollowStatisticRepository.save(snsUserFollowStatisticByFollow);

		return true;
	}


	@Transactional
	public Boolean deleteFollow(Long userId, Long followId) {
		SnsUserFollow snsUserFollow = snsUserFollowRepository.findByFollowerUseAndFollowingUser(userId, followId)
			.orElseThrow(
				() -> new BadRequestErrorException("해당 계정은 없습니다.")
			);

		snsUserFollowRepository.delete(snsUserFollow);

		SnsUserFollowStatistic snsUserFollowStatisticByMe = snsUserFollowStatisticRepository.findBySnsUser_id(userId).orElse(
			SnsUserFollowStatistic.builder()
				.snsUser(SnsUser.builder().id(userId).build())
				.followerNum(0)
				.followingNum(0)
				.build()
		);

		SnsUserFollowStatistic snsUserFollowStatisticByFollow = snsUserFollowStatisticRepository.findBySnsUser_id(followId).orElse(
			SnsUserFollowStatistic.builder()
				.snsUser(SnsUser.builder().id(followId).build())
				.followerNum(0)
				.followingNum(0)
				.build()
		);

		int followingNum = Math.max(snsUserFollowStatisticByMe.getFollowingNum() - 1, 0);
		snsUserFollowStatisticByMe.setFollowingNum(followingNum);
		snsUserFollowStatisticRepository.save(snsUserFollowStatisticByMe);

		int followerNum = Math.max(snsUserFollowStatisticByFollow.getFollowerNum() - 1, 0);
		snsUserFollowStatisticByFollow.setFollowerNum(followerNum);
		snsUserFollowStatisticRepository.save(snsUserFollowStatisticByFollow);

		return false;
	}

	@Transactional
	public Boolean deleteFollowAll(Long userId) {
		List<SnsUserFollow> snsUserFollowList = snsUserFollowRepository.findAllByMyFollow(userId);

		snsUserFollowJdbcRepository.deleteAll(snsUserFollowList);
		Optional<SnsUserFollowStatistic> snsUserFollowStatisticByMeOpt = snsUserFollowStatisticRepository.findBySnsUser_id(userId);
		snsUserFollowStatisticByMeOpt.ifPresent(snsUserFollowStatisticRepository::delete);

		return true;
	}

	@Transactional
	public Boolean deleteFollowAllByUserIdList(List<Long> snsUserIdList) {

		List<SnsUserFollow> snsUserFollowList = snsUserFollowRepository.findAllByMyFollowByUserList(snsUserIdList);

		snsUserFollowJdbcRepository.deleteAll(snsUserFollowList);
		List<SnsUserFollowStatistic> snsUserFollowStatisticList = snsUserFollowStatisticRepository.findBySnsUserInIdList(snsUserIdList);
		snsUserFollowStatisticJdbcRepository.deleteAll(snsUserFollowStatisticList);

		return true;
	}

	private List<SnsPostFollowsGetRsp> getPostFollowList(List<ProfileFollowDao> profileFollowDaoList
	) {
		return profileFollowDaoList.stream()
			.map((this::convertToFollowGetRsp))
			.toList();
	}

	private SnsPostFollowsGetRsp convertToFollowGetRsp(ProfileFollowDao profileFollowDao) {
		return SnsPostFollowsGetRsp.builder()
			.userId(profileFollowDao.getSnsUserId().toString())
			.username(profileFollowDao.getUsername())
			.profilePath(profileFollowDao.getProfilePath())
			.nickname(profileFollowDao.getNickname())
			.isFollowed(profileFollowDao.getIsFollowed())
			.isMe(profileFollowDao.getIsMe())
			.build();
	}
}
