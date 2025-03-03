package com.postvue.feelogserver.app.profiles.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.postvue.feelogserver.app.auth.dto.TokenResponse;
import com.postvue.feelogserver.app.auth.service.AuthService;
import com.postvue.feelogserver.app.cloud.service.R2CloudService;
import com.postvue.feelogserver.app.common.rsp.SnsPostFollowsGetRsp;
import com.postvue.feelogserver.app.facade.service.PostProfileFacadeService;
import com.postvue.feelogserver.app.notifications.service.NotificationService;
import com.postvue.feelogserver.app.posts.dto.common.Location;
import com.postvue.feelogserver.app.posts.dto.common.PostContent;
import com.postvue.feelogserver.app.posts.dto.req.create.SnsPostReportCreateReq;
import com.postvue.feelogserver.app.posts.dto.rsp.get.SnsPostRsp;
import com.postvue.feelogserver.app.profiles.dto.req.create.CreateProfileScrapReq;
import com.postvue.feelogserver.app.profiles.dto.req.create.SnsUserReportCreateReq;
import com.postvue.feelogserver.app.profiles.dto.req.put.PutMyPrivateProfileInfoReq;
import com.postvue.feelogserver.app.profiles.dto.req.put.PutMyProfileBirthdateInfoReq;
import com.postvue.feelogserver.app.profiles.dto.req.put.PutMyProfileEmailInfoReq;
import com.postvue.feelogserver.app.profiles.dto.req.put.PutMyProfileGenderInfoReq;
import com.postvue.feelogserver.app.profiles.dto.req.put.PutMyProfileInfoReq;
import com.postvue.feelogserver.app.profiles.dto.req.put.PutMyProfilePasswordInfoReq;
import com.postvue.feelogserver.app.profiles.dto.rsp.common.ScrapThumbnailRsp;
import com.postvue.feelogserver.app.profiles.dto.rsp.create.PostToScrapRsp;
import com.postvue.feelogserver.app.profiles.dto.rsp.get.GetExistenceByEmailRsp;
import com.postvue.feelogserver.app.profiles.dto.rsp.get.GetExistenceByUsernameRsp;
import com.postvue.feelogserver.app.profiles.dto.rsp.get.GetMyProfileInfoRsp;
import com.postvue.feelogserver.app.profiles.dto.rsp.get.GetProfilePostListRsp;
import com.postvue.feelogserver.app.profiles.dto.rsp.get.GetProfileBlockedUserRsp;
import com.postvue.feelogserver.app.profiles.dto.rsp.get.GetProfileInfoRsp;
import com.postvue.feelogserver.app.profiles.dto.rsp.get.GetProfileMainScrapPostRsp;
import com.postvue.feelogserver.app.profiles.dto.rsp.get.GetProfileUserByUsername;
import com.postvue.feelogserver.app.profiles.dto.rsp.get.GetProfileUserRsp;
import com.postvue.feelogserver.app.profiles.dto.rsp.get.GetScrapInfoRsp;
import com.postvue.feelogserver.app.profiles.dto.rsp.get.GetScrapPreviewRsp;
import com.postvue.feelogserver.app.profiles.dto.rsp.get.GetScrapRsp;
import com.postvue.feelogserver.app.profiles.dto.rsp.put.PutProfilePasswordInfoRsp;
import com.postvue.feelogserver.domain.snsblockusers.SnsBlockUser;
import com.postvue.feelogserver.domain.snsblockusers.repository.SnsBlockUserRepository;
import com.postvue.feelogserver.domain.snspostreports.SnsPostReport;
import com.postvue.feelogserver.domain.snspostreports.vo.PostReportReasonType;
import com.postvue.feelogserver.domain.snspostreports.vo.PostReportStatus;
import com.postvue.feelogserver.domain.snsposts.SnsPost;
import com.postvue.feelogserver.domain.snsposts.dto.SnsPostContentDao;
import com.postvue.feelogserver.domain.snsposts.dto.SnsPostDao;
import com.postvue.feelogserver.domain.snsposts.repository.SnsPostRepository;
import com.postvue.feelogserver.domain.snsposts.vo.PostContentType;
import com.postvue.feelogserver.domain.snspostuserreactions.SnsPostUserReaction;
import com.postvue.feelogserver.domain.snspostuserreactions.dao.ProfilePostScrapDao;
import com.postvue.feelogserver.domain.snspostuserreactions.repository.SnsPostUserReactionJdbcRepository;
import com.postvue.feelogserver.domain.snspostuserreactions.repository.SnsPostUserReactionRepository;
import com.postvue.feelogserver.domain.snsscrap.SnsScrap;
import com.postvue.feelogserver.domain.snsscrap.repository.SnsScrapJdbcRepository;
import com.postvue.feelogserver.domain.snsscrap.repository.SnsScrapRepository;
import com.postvue.feelogserver.domain.snsscrap.repository.dao.ScrapThumbNailDao;
import com.postvue.feelogserver.domain.snsscrapboard.SnsScrapBoard;
import com.postvue.feelogserver.domain.snsscrapboard.dao.ScrapBoardInfoDao;
import com.postvue.feelogserver.domain.snsscrapboard.dao.ScrapPreviewDao;
import com.postvue.feelogserver.domain.snsscrapboard.repository.SnsScrapBoardRepository;
import com.postvue.feelogserver.domain.snsscrapboard.vo.ScrapTargetAudience;
import com.postvue.feelogserver.domain.snstags.vo.PostTag;
import com.postvue.feelogserver.domain.snsuserfollows.SnsUserFollow;
import com.postvue.feelogserver.domain.snsuserfollows.dao.ProfileFollowDao;
import com.postvue.feelogserver.domain.snsuserfollows.repository.SnsUserFollowJdbcRepository;
import com.postvue.feelogserver.domain.snsuserfollows.repository.SnsUserFollowRepository;
import com.postvue.feelogserver.domain.snsuserfollowstatistics.SnsUserFollowStatistic;
import com.postvue.feelogserver.domain.snsuserfollowstatistics.repository.SnsUserFollowStatisticRepository;
import com.postvue.feelogserver.domain.snsuserreport.SnsUserReport;
import com.postvue.feelogserver.domain.snsuserreport.repository.SnsUserReportRepository;
import com.postvue.feelogserver.domain.snsuserreport.vo.UserReportReasonType;
import com.postvue.feelogserver.domain.snsuserreport.vo.UserReportStatus;
import com.postvue.feelogserver.domain.snsusers.SnsUser;
import com.postvue.feelogserver.domain.snsusers.dao.ProfileInfoWithFollowDao;
import com.postvue.feelogserver.domain.snsusers.dao.ProfileUserByUsernameDao;
import com.postvue.feelogserver.domain.snsusers.dao.ProfileUserWithFollowByUsernameDao;
import com.postvue.feelogserver.domain.snsusers.repository.SnsUserRepository;
import com.postvue.feelogserver.domain.snsusers.vo.SnsUserGender;
import com.postvue.feelogserver.global.constant.CookieConst;
import com.postvue.feelogserver.global.constant.MediaConfigConst;
import com.postvue.feelogserver.global.constant.PageConfigConst;
import com.postvue.feelogserver.global.constant.PostConst;
import com.postvue.feelogserver.global.constant.PostReportConst;
import com.postvue.feelogserver.global.constant.SystemPhraseConst;
import com.postvue.feelogserver.global.constant.UserReportConst;
import com.postvue.feelogserver.global.exception.BadRequestErrorException;
import com.postvue.feelogserver.global.exception.ForbiddenErrorException;
import com.postvue.feelogserver.global.exception.NotFoundErrorException;
import com.postvue.feelogserver.global.exception.UnauthorizedErrorException;
import com.postvue.feelogserver.global.util.generator.CookieUtils;
import com.postvue.feelogserver.global.util.validation.UploadFileValidationUtils;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProfilesService {
	private final SnsUserFollowRepository snsUserFollowRepository;
	private final SnsScrapRepository snsScrapRepository;
	private final SnsUserRepository snsUserRepository;
	private final SnsPostUserReactionRepository snsPostUserReactionRepository;
	private final SnsPostUserReactionJdbcRepository snsPostUserReactionJdbcRepository;
	private final SnsScrapBoardRepository snsScrapBoardRepository;
	private final SnsScrapJdbcRepository snsScrapJdbcRepository;
	private final SnsPostRepository snsPostRepository;
	private final SnsBlockUserRepository snsBlockUserRepository;
	private final AuthService authService;
	private final NotificationService notificationService;
	private final R2CloudService r2CloudService;
	private final PostProfileFacadeService postProfileFacadeService;
	private final ProfileFollowsService profileFollowsService;
	private final SnsUserReportRepository snsUserReportRepository;

	@Value("${security.service.auth.COOKIE_MAX_AGE_REFRESH_TOKEN}")
	private Integer COOKIE_MAX_AGE_REFRESH_TOKEN;

	@Value("${cloud.cloudflare.service.contentBucket.bucketPublicUrl}")
	private String cloudflareBucketUrl;

	@Value("${cloud.cloudflare.service.contentBucket.imageContentStoragePath}")
	private String imageContentStoragePath;

	@Value("${cloud.cloudflare.service.contentBucket.imageProfilePath}")
	private String imageProfilePath;

	@Value("${file.imageSize}")
	private Integer imageFileSize;


	// public GetFollowProfileInfoRsp getFollowProfileInfo(String username, Long myUserId) {
	// 	SnsUserFollow snsUserFollow = snsUserFollowRepository.findByUsernameAndUserId(username, myUserId).orElseThrow();
	//
	// 	SnsUser followUser =
	// 		Objects.equals(
	// 			snsUserFollow.getFollowingUser().getSnsUserId(), myUserId) ? snsUserFollow.getFollowerUser() :
	// 			snsUserFollow.getFollowingUser();
	//
	// 	return GetFollowProfileInfoRsp.builder()
	// 		.profilePath(followUser.getProfilePath())
	// 		.username(followUser.getUsername())
	// 		.targetUserId(followUser.getSnsUserId().toString())
	// 		.msgRoomId(snsUserFollow.getSnsUserFollowId().toString())
	// 		.build();
	// }

	// @VERIFY1
	@Transactional
	public GetMyProfileInfoRsp getMyProfileInfo(Long userId) {
		SnsUser myUser = snsUserRepository.findById(userId).orElseThrow(
			() -> new BadRequestErrorException("해당 계정은 없습니다.")
		);
		return convertUserToMyProfileInfoRsp(myUser);
	}

	@Transactional
	public GetMyProfileInfoRsp putMyProfileInfo(Long userId, PutMyProfileInfoReq putMyProfileInfoReq,
		MultipartFile profileImgFile) {
		SnsUser myUser = snsUserRepository.findById(userId).orElseThrow(
			()->new BadRequestErrorException("해당 계정은 없습니다.")
		);
		myUser.setUserLink(putMyProfileInfoReq.getWebsite());
		myUser.setUserDescription(putMyProfileInfoReq.getIntroduce());
		myUser.setNickname(putMyProfileInfoReq.getNickname());


		if (profileImgFile != null) {
			String contentType = profileImgFile.getContentType();
			boolean isImage = UploadFileValidationUtils.isImage(contentType);
			if (!isImage) {
				throw new BadRequestErrorException("업로드 파일 유형이 아닙니다.");
			}
			if (profileImgFile.getSize() > imageFileSize) {
				throw new BadRequestErrorException("이미지 크기가 너무 큽니다.");
			}

			String originalFilename = profileImgFile.getOriginalFilename();
			// String extension = FilenameUtils.getExtension(originalFilename);
			if (myUser.getProfilePath().startsWith(cloudflareBucketUrl)) {
				r2CloudService.deleteImageFromR2(myUser.getProfilePath().replace(cloudflareBucketUrl, ""));
			}

			String profilePath = imageContentStoragePath + imageProfilePath +
				UUID.randomUUID() + MediaConfigConst.IMAGE_WEBP_FORMAT;

			myUser.setProfilePath(cloudflareBucketUrl + profilePath);
			r2CloudService.uploadImageToR2(profileImgFile, profilePath);
		}

		snsUserRepository.save(myUser);
		return convertUserToMyProfileInfoRsp(myUser);

	}

	@Transactional
	public GetMyProfileInfoRsp putMyProfileEmailInfo(Long userId, PutMyProfileEmailInfoReq putMyProfileEmailInfoReq) {
		SnsUser myUser = snsUserRepository.findById(userId).orElseThrow(
			() -> new BadRequestErrorException("해당 계정은 없습니다.")
		);
		myUser.setEmail(putMyProfileEmailInfoReq.getEmail());
		snsUserRepository.save(myUser);
		return convertUserToMyProfileInfoRsp(myUser);
	}

	@Transactional
	public GetMyProfileInfoRsp putMyProfileBirthdateInfo(Long userId,
		PutMyProfileBirthdateInfoReq putMyProfileBirthdateInfoReq) {
		SnsUser myUser = snsUserRepository.findById(userId).orElseThrow(
			() -> new BadRequestErrorException("해당 계정은 없습니다.")
		);
		myUser.setBirthDate(putMyProfileBirthdateInfoReq.convertBirthDateAsLocalDate());
		snsUserRepository.save(myUser);
		return convertUserToMyProfileInfoRsp(myUser);
	}

	@Transactional
	public GetMyProfileInfoRsp putMyProfileGenderInfo(Long userId,
		PutMyProfileGenderInfoReq putMyProfileGenderInfoReq) {
		SnsUser myUser = snsUserRepository.findById(userId).orElseThrow(
			() -> new BadRequestErrorException("해당 계정은 없습니다.")
		);
		myUser.setSnsUserGender(SnsUserGender.valueOf(putMyProfileGenderInfoReq.getGender()));
		snsUserRepository.save(myUser);
		return convertUserToMyProfileInfoRsp(myUser);
	}

	@Transactional
	public GetMyProfileInfoRsp putMyPrivateProfile(Long userId,
		PutMyPrivateProfileInfoReq putMyPrivateProfileInfoReq) {
		SnsUser myUser = snsUserRepository.findById(userId).orElseThrow(
			() -> new BadRequestErrorException("해당 계정은 없습니다.")
		);
		myUser.setIsPrivateProfile(putMyPrivateProfileInfoReq.getIsPrivateProfile());
		snsUserRepository.save(myUser);
		return convertUserToMyProfileInfoRsp(myUser);
	}

	@Transactional
	public PutProfilePasswordInfoRsp putMyProfilePasswordInfo(Long userId,
		PutMyProfilePasswordInfoReq putMyProfilePasswordInfoReq, HttpServletResponse response) {
		SnsUser myUser = snsUserRepository.findById(userId)
			.orElseThrow(() -> new UnauthorizedErrorException("현재 회원은 없습니다."));

		if (!authService.checkPassword(putMyProfilePasswordInfoReq.getCurrentPassword(), myUser.getHashPw())) {
			throw new UnauthorizedErrorException("현재 비밀번호가 틀립니다.");
		}

		myUser.setHashPw(authService.hashPassword(putMyProfilePasswordInfoReq.getPassword()));
		snsUserRepository.save(myUser);

		TokenResponse tokens = authService.createJwtTokens(myUser.getId(), myUser.getSnsAppRole());
		response.addCookie(
			CookieUtils.createCookie(CookieConst.REFRESH_TOKEN, tokens.refreshToken(), COOKIE_MAX_AGE_REFRESH_TOKEN));

		return PutProfilePasswordInfoRsp.builder()
			.accessToken(tokens.accessToken())
			.build();
	}

	@Transactional
	public GetExistenceByUsernameRsp getExistenceByUsername(String username) {
		Optional<SnsUser> snsUserOpt = snsUserRepository.findByUsername(username);
		return GetExistenceByUsernameRsp.builder()
			.username(username)
			.isExisted(snsUserOpt.isPresent())
			.build();
	}

	@Transactional
	public GetExistenceByEmailRsp getExistenceByEmail(String email) {
		// CHECK1
		Optional<SnsUser> snsUserOpt = snsUserRepository.findBySignupEmail(email);
		return GetExistenceByEmailRsp.builder()
			.email(email)
			.isExisted(snsUserOpt.isPresent())
			.build();
	}

	@Transactional
	public GetProfileInfoRsp getProfileInfo(String username, Long myUserId) {
		ProfileInfoWithFollowDao profileInfoWithFollowDao = snsUserRepository.findByUsernameWithFollowInfo(username,
			myUserId).orElseThrow(
			() -> new NotFoundErrorException("해당 계정은 없습니다.")
		);

		return GetProfileInfoRsp.builder()
			.userId(profileInfoWithFollowDao.getSnsUserId().toString())
			.username(profileInfoWithFollowDao.getUsername())
			.nickname(profileInfoWithFollowDao.getNickname())
			.website(profileInfoWithFollowDao.getUserLink())
			.profilePath(profileInfoWithFollowDao.getProfilePath())
			.introduce(profileInfoWithFollowDao.getUserDescription())
			.isMe(Objects.equals(myUserId, profileInfoWithFollowDao.getSnsUserId()))
			.isFollowed(profileInfoWithFollowDao.getIsFollowed())
			.isBlocked(profileInfoWithFollowDao.getIsBlocked())
			.isBlockerUser(profileInfoWithFollowDao.getIsBlockerUser())
			.isPrivate(profileInfoWithFollowDao.getIsPrivate())
			.followerNum(profileInfoWithFollowDao.getFollowerNum())
			.followingNum(profileInfoWithFollowDao.getFollowingNum())
			.build();
	}

	@Transactional
	public GetProfileUserRsp getProfileUserListByUsername(String username, Long myUserId, Long cursorId, Boolean hasFollowInfo) {
		List<GetProfileUserByUsername> getProfileUserByUsernameList;

		if (hasFollowInfo){
			List<ProfileUserWithFollowByUsernameDao> profileUserWithFollowByUsernameDaoList = snsUserRepository.findAllUserWithFollowByUsername(username,
				myUserId, cursorId,
				PageRequest.of(PageConfigConst.PAGE_INIT_NUM, PageConfigConst.DEFAULT_PAGE_SIZE));
			profileUserWithFollowByUsernameDaoList = profileUserWithFollowByUsernameDaoList.stream()
				.filter((profileUserByUsernameDao -> !Objects.equals(profileUserByUsernameDao.getSnsUserId(), myUserId)))
				.toList();

			getProfileUserByUsernameList = profileUserWithFollowByUsernameDaoList.stream()
				.map((profileUserByUsernameDao ->
					GetProfileUserByUsername
						.builder()
						.userId(profileUserByUsernameDao.getSnsUserId().toString())
						.username(profileUserByUsernameDao.getUsername())
						.nickname(profileUserByUsernameDao.getNickname())
						.profilePath(profileUserByUsernameDao.getProfilePath())
						.isFollowed(profileUserByUsernameDao.getIsFollowed())
						.build()))
				.toList();
		}
		else{
			List<ProfileUserByUsernameDao> profileUserByUsernameDaoList = snsUserRepository.findAllUserByUsername(username,
				myUserId, cursorId,
				PageRequest.of(PageConfigConst.PAGE_INIT_NUM, PageConfigConst.DEFAULT_PAGE_SIZE));
			profileUserByUsernameDaoList = profileUserByUsernameDaoList.stream()
				.filter((profileUserByUsernameDao -> !Objects.equals(profileUserByUsernameDao.getSnsUserId(), myUserId)))
				.toList();

			getProfileUserByUsernameList = profileUserByUsernameDaoList.stream()
				.map((profileUserByUsernameDao ->
					GetProfileUserByUsername
						.builder()
						.userId(profileUserByUsernameDao.getSnsUserId().toString())
						.username(profileUserByUsernameDao.getUsername())
						.nickname(profileUserByUsernameDao.getNickname())
						.profilePath(profileUserByUsernameDao.getProfilePath())
						.build()))
				.toList();
		}

		return GetProfileUserRsp.builder()
			.cursorId(
				getProfileUserByUsernameList.size() != PageConfigConst.PAGE_INIT_NUM ? getProfileUserByUsernameList.get(
					getProfileUserByUsernameList.size() - 1).getUserId() :
					PageConfigConst.ZERO_ID)
			.getProfileUserByUsernameList(getProfileUserByUsernameList)
			.build();

	}

	@Transactional
	public List<ScrapThumbnailRsp> getScrapLists(Long userId, Integer page) {
		List<ScrapThumbNailDao> scrapListDaoThumbNail = snsScrapRepository.selectScrapBoard(userId,
			page * PageConfigConst.PROFILE_SCRAP_PAGE_NUM,
			PageConfigConst.PROFILE_SCRAP_PAGE_NUM);

		return getScrapThumbnailListRsps(scrapListDaoThumbNail);
	}

	@Transactional
	public List<ScrapThumbnailRsp> getScrapListsBySearchQuery(Long userId, String searchQuery, Integer page) {
		if (userId == null){
			throw new UnauthorizedErrorException("인증된 계정이 아닙니다.");
		}
		List<ScrapThumbNailDao> scrapListDaoList = snsScrapRepository.selectScrapBoardBySearchQuery(
			userId, searchQuery, page * PageConfigConst.PROFILE_SCRAP_PAGE_NUM,
			PageConfigConst.PROFILE_SCRAP_PAGE_NUM);

		return getScrapThumbnailListRsps(scrapListDaoList);

	}

	private List<ScrapThumbnailRsp> getScrapThumbnailListRsps(List<ScrapThumbNailDao> scrapListDaoThumbNail) {
		Map<Long, List<ScrapThumbNailDao>> scrapListHashMap  = scrapListDaoThumbNail.stream()
			.collect(Collectors.groupingBy(ScrapThumbNailDao::getSnsScrapBoardId));

		return scrapListHashMap.entrySet().stream()
			.sorted(Map.Entry.comparingByKey(Comparator.reverseOrder()))
			.map(entry -> {
				Long myScrapListId = entry.getKey();
				List<ScrapThumbNailDao> scrapThumbNailDaos = entry.getValue();

				List<GetProfileMainScrapPostRsp> getProfileMainScrapPostRspList = new ArrayList<>();
				for (ScrapThumbNailDao scrapThumbNailDao : scrapThumbNailDaos) {
					if (!scrapThumbNailDao.getStringToSnsPostContents().isEmpty()) {
						SnsPostContentDao postContentDao = scrapThumbNailDao.getStringToSnsPostContents().get(0);

						PostContentType contentType = postContentDao.getPostContentType();
						getProfileMainScrapPostRspList.add(
							GetProfileMainScrapPostRsp
								.builder()
								.postThumbnailContent(contentType == PostContentType.VIDEO
									? Objects.requireNonNullElse(postContentDao.getBucketUrl(),"") + postContentDao.getPreviewImg()
									: Objects.requireNonNullElse(postContentDao.getBucketUrl(),"") + postContentDao.getContent())
								.postThumbnailContentType(PostContentType.IMAGE.toString())
								.build()
						);
					}
				}

				return ScrapThumbnailRsp.builder()
					.scrapId(myScrapListId.toString())
					.scrapName(scrapThumbNailDaos.get(0).getScrapName())
					.scrapNum(scrapThumbNailDaos.get(0).getScrapNum())
					.lastPostedAt(scrapThumbNailDaos.get(0).getRecentlyPostedAt())
					.postScrapPreviewList(getProfileMainScrapPostRspList)
					.isMe(scrapThumbNailDaos.get(0).getIsMe())
					.build();
			}).sorted(Comparator.comparing(ScrapThumbnailRsp::getLastPostedAt).reversed()) // lastPostedAt 기준으로 내림차순 정렬
			.toList();
	}

	@Transactional
	public List<GetScrapPreviewRsp> getScrapPreviewList(Long myUserId, Long postId) {
		Pageable pageable = PageRequest.of(PageConfigConst.PAGE_INIT_NUM, PageConfigConst.SCRAP_PREVIEW_MAX_PAGE_NUM);
		// List<SnsScrapBoard> snsScrapBoardLists = snsScrapBoardRepository.findBySnsUser_IdOrderByIdDesc(
		// 	myUserId, pageable);

		// List<SnsScrap> snsScrapList = snsScrapRepository.findBySnsUser_IdAndSnsPost_IdOrderByCreatedAtDesc(myUserId,
		// 	postId);

		// Map<Long, SnsScrap> snsScrapMap = new HashMap<>();
		// snsScrapList.forEach(snsScrap -> {
		// 	snsScrapMap.put(snsScrap.getSnsScrapBoard().getId(), snsScrap);
		// });

		List<ScrapPreviewDao> scrapPreviewDaoList = snsScrapBoardRepository.findPreviewScrapBoardList(myUserId, postId, pageable);
		List<ScrapPreviewDao> scrapPreviewDaoMyList = snsScrapBoardRepository.findPreviewScrapBoardListByHasPost(myUserId, postId);

		// 중복 방지를 위한 Set 사용
		Set<Long> scrapBoardIdSet = new HashSet<>();

		// 최종 결과 리스트
		List<ScrapPreviewDao> mergedList = new ArrayList<>();

		// 첫 번째 리스트 추가 (중복 방지)
		for (ScrapPreviewDao item : scrapPreviewDaoMyList) {
			if (scrapBoardIdSet.add(item.getScrapBoardId())) { // 중복된 ID가 아닐 경우 추가
				mergedList.add(item);
			}
		}

		// 두 번째 리스트 추가 (중복 방지)
		for (ScrapPreviewDao item : scrapPreviewDaoList) {
			if (scrapBoardIdSet.add(item.getScrapBoardId())) { // 중복된 ID가 아닐 경우 추가
				mergedList.add(item);
			}
		}



		return mergedList.stream().map(scrapPreviewDao ->
				GetScrapPreviewRsp
					.builder()
					.scrapBoardId(scrapPreviewDao.getScrapBoardId().toString())
					.scrapBoardName(scrapPreviewDao.getScrapBoardName())
					.isScraped(scrapPreviewDao.getIsScraped())
					.build())
			.toList();

	}

	@Transactional
	public GetProfilePostListRsp getMyClipListRsp(Long myUserId, Long cursorId) {
		List<SnsPostDao> profilePostListDaoList =
			snsPostRepository.findAllMyClipList(
				myUserId, cursorId, PageConfigConst.PROFILE_CLIP_PAGE_NUM);

		List<SnsPostRsp> snsPostRspList = postProfileFacadeService.getPostGetRspList(profilePostListDaoList);

		if (snsPostRspList.isEmpty()) {
			return GetProfilePostListRsp.builder()
				.cursorId(PageConfigConst.ZERO_ID)
				.snsPostRspList(new ArrayList<>())
				.build();
		} else {
			return GetProfilePostListRsp.builder()
				.cursorId(profilePostListDaoList.get(profilePostListDaoList.size() - 1).getCursorId().toString())
				.snsPostRspList(snsPostRspList)
				.build();
		}
	}

	@Transactional
	public GetScrapInfoRsp getScrapInfo(Long myUserId, Long scrapId) {
		ScrapBoardInfoDao scrapBoardInfoDao = snsScrapBoardRepository.findScrapInfoByMyUserId(
				scrapId, myUserId)
			.orElseThrow(() -> new BadRequestErrorException("접근 권한이 없습니다."));

		return GetScrapInfoRsp.builder()
			.scrapId(scrapBoardInfoDao.getScrapBoardId().toString())
			.scrapNum(scrapBoardInfoDao.getScrapNum())
			.scrapName(scrapBoardInfoDao.getScrapName())
			.lastPostedAt(scrapBoardInfoDao.getRecentlyPostedAt())
			.isMe(scrapBoardInfoDao.getIsMe())
			.targetAudience(scrapBoardInfoDao.getTargetAudience().toString())
			.userId(scrapBoardInfoDao.getUserId().toString())
			.username(scrapBoardInfoDao.getUsername())
			.nickname(scrapBoardInfoDao.getNickname())
			.profilePath(scrapBoardInfoDao.getProfilePath())
			.build();
	}

	@Transactional
	public GetScrapRsp getScrapRsp(Long myUserId, Long scrapId, Long cursorId) {
		Pageable pageable = PageRequest.of(PageConfigConst.PAGE_INIT_NUM, PageConfigConst.PROFILE_SCRAP_PAGE_NUM);
		List<ProfilePostScrapDao> profilePostScrapDaoList = snsScrapRepository.findScrapPostList(myUserId, scrapId,
			cursorId,
			pageable);

		List<SnsPostRsp> getProfilePostRspList = profilePostScrapDaoList.stream().map(
			this::convertProfileScrapDaoToPostRsp).toList();

		if (getProfilePostRspList.isEmpty()) {
			return GetScrapRsp.builder()
				.cursorId(PageConfigConst.ZERO_ID)
				.snsPostRspList(new ArrayList<>())
				.build();
		} else {
			return GetScrapRsp.builder()
				.cursorId(profilePostScrapDaoList.get(profilePostScrapDaoList.size() - 1).getCursorId().toString())
				.snsPostRspList(getProfilePostRspList)
				.build();
		}
	}

	@Transactional
	public List<GetProfileBlockedUserRsp> getProfileBlockedUserList(Long userId, Integer page) {
		Pageable pageable = PageRequest.of(page * PageConfigConst.PROFILE_BLOCKED_USER_LIST_PAGE_SIZE,
			PageConfigConst.PROFILE_BLOCKED_USER_LIST_PAGE_SIZE);
		List<SnsUser> blockedUserList = snsBlockUserRepository.findBlockedUserListByPageable(userId, pageable);

		return blockedUserList.stream().map((blockedUser) -> GetProfileBlockedUserRsp.builder()
			.blockedUserId(blockedUser.getId().toString())
			.blockedNickname(blockedUser.getNickname())
			.blockedUsername(blockedUser.getUsername())
			.blockedUserProfilePath(blockedUser.getProfilePath())
			.build()).toList();

	}

	@Transactional
	public ScrapThumbnailRsp createProfileScrap(Long snsUserId, CreateProfileScrapReq createProfileScrapReq, Long postId) {
		SnsUser snsUser = SnsUser.builder()
			.id(snsUserId)
			.build();

		ScrapTargetAudience targetAudience;
		if (Objects.equals(createProfileScrapReq.getTargetAudienceValue(), ScrapTargetAudience.PROTECTED_AUDIENCE.label())) {
			targetAudience = ScrapTargetAudience.PROTECTED_AUDIENCE;
		} else if (Objects.equals(createProfileScrapReq.getTargetAudienceValue(), ScrapTargetAudience.PRIVATE_AUDIENCE.label())) {
			targetAudience = ScrapTargetAudience.PRIVATE_AUDIENCE;
		} else {
			targetAudience = ScrapTargetAudience.PUBLIC_AUDIENCE;
		}

		SnsScrapBoard newScrapList = SnsScrapBoard.builder()
			.snsUser(snsUser)
			.scrapName(createProfileScrapReq.getScrapName())
			.targetAudience(targetAudience)
			.build();
		newScrapList = snsScrapBoardRepository.save(newScrapList);

		List<GetProfileMainScrapPostRsp> getProfileMainScrapPostRspList = new ArrayList<>();

		// 스크랩 생성 할 떄, 포스트도 같이 저장
		if (postId != null) {
			SnsPost snsPost = snsPostRepository.findById(postId).orElseThrow(
				() -> new BadRequestErrorException("해당 게시물은 없습니다.")
			);

			// clip 없을 시, 추가
			Optional<SnsPostUserReaction> snsPostUserReactionOpt = snsPostUserReactionRepository
				.findBySnsPostAndSnsUser(
					postId, snsUserId);

			// // 리액션(clip) 아예 없을 시
			if (snsPostUserReactionOpt.isEmpty()) {
				snsPostUserReactionRepository.save(SnsPostUserReaction.builder()
					.snsPost(snsPost)
					.snsUser((SnsUser.builder().id(snsUserId).build()))
					.isClipped(true)
					.isClippedAt(LocalDateTime.now())
					.build());
			} else {
				SnsPostUserReaction snsPostUserReaction = snsPostUserReactionOpt.get();
				snsPostUserReaction.setIsClipped(true);
				snsPostUserReaction.setIsClippedAt(LocalDateTime.now());
				snsPostUserReactionRepository.save(snsPostUserReaction);
			}

			// 새로운 스크랩 생성
			SnsScrap newSnsScrap = SnsScrap.builder()
				.snsScrapBoard(newScrapList)
				.snsPost(snsPost)
				.snsUser(snsUser)
				.build();
			snsScrapRepository.save(newSnsScrap);

			// 리액션 반응
			snsPost.setReactionCount((snsPost.getReactionCount() != null ? snsPost.getReactionCount() : 0) + PostConst.POST_REACTION_SCRAP_SCORE);

			if (snsPost.getSnsPostContents().isEmpty()) {
				getProfileMainScrapPostRspList.add(GetProfileMainScrapPostRsp.builder()
					.postThumbnailContent(snsPost.getSnsPostContents().get(0).getContent())
					.postThumbnailContentType(snsPost.getSnsPostContents().get(0).getPostContentType().toString())
					.build());
			}
		}

		// 스크랩 보드 생성
		return ScrapThumbnailRsp.builder()
			.scrapName(newScrapList.getScrapName())
			.scrapId(newScrapList.getId().toString())
			.postScrapPreviewList(getProfileMainScrapPostRspList)
			.build();
	}

	@Transactional
	public ScrapThumbnailRsp updateProfileScrap(
		Long scrapId,
		Long snsUserId,
		CreateProfileScrapReq createProfileScrapReq) {
		ScrapTargetAudience targetAudience = getTargetAudienceValue(createProfileScrapReq.getTargetAudienceValue());
		SnsScrapBoard scrapBoard = snsScrapBoardRepository.findBySnsUser_IdAndId(snsUserId,scrapId).orElseThrow(
			() -> new BadRequestErrorException("해당 스크랩은 없습니다.")
		);

		scrapBoard.setScrapName(createProfileScrapReq.getScrapName());
		scrapBoard.setTargetAudience(targetAudience);
		snsScrapBoardRepository.save(scrapBoard);

		List<GetProfileMainScrapPostRsp> getProfileMainScrapPostRspList = new ArrayList<>();

		// 스크랩 보드 생성
		return ScrapThumbnailRsp.builder()
			.scrapName(scrapBoard.getScrapName())
			.scrapId(scrapBoard.getId().toString())
			.postScrapPreviewList(getProfileMainScrapPostRspList)
			.build();
	}

	@Transactional
	public PostToScrapRsp createPostToScrap(Long snsUserId, Long scrapBoardId, Long postId) {
		Optional<SnsScrap> snsScrapOptByPostId = snsScrapRepository.findBySnsUser_IdAndSnsPost_IdAndSnsScrapBoard_Id(
			snsUserId, postId,
			scrapBoardId);

		SnsPost snsPost = snsPostRepository.findById(postId)
			.orElseThrow(() -> new BadRequestErrorException("해당 게시물은 없습니다."));

		boolean isBlocked = snsBlockUserRepository.findIsBlockUser(snsUserId, snsPost.getSnsUser().getId());
		if (isBlocked){
			throw new ForbiddenErrorException("비공개 계정에 대해서 포스트를 클립할 수 없습니다.");
		}

		SnsPostUserReaction snsPostUserReaction = snsPostUserReactionRepository.findBySnsPostAndSnsUser(postId,
			snsUserId).orElse(
			SnsPostUserReaction.builder()
				.snsPost(snsPost)
				.snsUser(snsUserRepository.findById(snsUserId).orElseThrow(
					() -> new BadRequestErrorException("해당 계정은 없습니다.")
				))
				.build());
		snsPostUserReaction.setIsClipped(true);
		snsPostUserReaction.setIsClippedAt(LocalDateTime.now());
		snsPostUserReactionRepository.save(snsPostUserReaction);

		if (snsScrapOptByPostId.isPresent()) {
			return PostToScrapRsp.builder()
				.scrapId(scrapBoardId.toString())
				.isScraped(true)
				.isClipped(true)
				.build();
		} else {
			snsPost.setReactionCount((snsPost.getReactionCount() != null ? snsPost.getReactionCount() : 0) + PostConst.POST_REACTION_SCRAP_SCORE);
			SnsScrap newScrap = SnsScrap.builder()
				.snsScrapBoard(SnsScrapBoard.builder().id(scrapBoardId).build())
				.snsUser(SnsUser.builder().id(snsUserId).build())
				.snsPost(snsPostRepository.findById(postId).orElseThrow(
					() -> new BadRequestErrorException("해당 게시글은 없습니다.")
				))
				.build();

			snsScrapRepository.save(newScrap);

			//알림
			if (!snsUserId.equals(snsPostUserReaction.getSnsPost()
				.getSnsUser()
				.getId())) {
				notificationService.processPostClipNotification(newScrap.getSnsPost(), snsPostUserReaction);
			}

			return PostToScrapRsp.builder()
				.scrapId(scrapBoardId.toString())
				.isScraped(true)
				.isClipped(true)
				.build();
		}
	}

	// @Transactional
	// public PostToScrapListRsp createPostToScrapList(Long snsUserId,
	// 	Long postId,
	// 	List<String> scrapIdList) {
	// 	List<Long> scrapBoardList = scrapIdList.stream().map(Long::valueOf).toList();
	//
	// 	if (scrapBoardList.isEmpty()) {
	// 		throw new BadRequestErrorException("스크랩 목록이 없습니다.");
	// 	}
	// 	List<SnsScrap> myScrapList = snsScrapRepository.findBySnsUserAndSnsPostAndSnsScrapBoardIn(snsUserId, postId,
	// 		scrapBoardList);
	//
	// 	List<Long> myScrapBoardIdList = myScrapList.stream()
	// 		.map((snsScrap -> snsScrap.getSnsScrapBoard().getId()))
	// 		.toList();
	//
	// 	List<SnsScrap> notExistedScrapBoradList = new ArrayList<>();
	//
	// 	SnsPost snsPost = snsPostRepository.findById(postId).orElseThrow(
	// 		() -> new BadRequestErrorException("해당 게시물은 없습니다.")
	// 	);
	//
	// 	boolean isBlocked = snsBlockUserRepository.findIsBlockUser(snsUserId, snsPost.getSnsUser().getId());
	// 	if (isBlocked){
	// 		throw new ForbiddenErrorException("비공개 계정에 대해서 포스트를 스크랩 할 수 없습니다.");
	// 	}
	//
	// 	// 리액션 반응
	// 	snsPost.setReactionCount((snsPost.getReactionCount() != null ? snsPost.getReactionCount() : 0) + 1);
	//
	// 	for (Long scrapBoardId : scrapBoardList) {
	// 		int myScrapIndex = myScrapBoardIdList.indexOf(scrapBoardId);
	// 		if (myScrapIndex == -1) {
	// 			notExistedScrapBoradList.add(SnsScrap.builder()
	// 				.snsScrapBoard(SnsScrapBoard.builder().id(scrapBoardId).build())
	// 				.snsPost(snsPost)
	// 				.snsUser(SnsUser.builder().id(snsUserId).build())
	// 				.build());
	// 		}
	// 	}
	//
	// 	snsScrapJdbcRepository.saveAll(notExistedScrapBoradList);
	//
	// 	SnsPostUserReaction snsPostUserReaction = snsPostUserReactionRepository.findBySnsPostAndSnsUser(postId,
	// 		snsUserId).orElse(
	// 		SnsPostUserReaction.builder()
	// 			.snsPost(SnsPost.builder().id(postId).build())
	// 			.snsUser(SnsUser.builder().id(snsUserId).build())
	// 			.build());
	// 	snsPostUserReaction.setIsClipped(true);
	// 	snsPostUserReaction.setIsClippedAt(LocalDateTime.now());
	// 	snsPostUserReactionRepository.save(snsPostUserReaction);
	//
	// 	return PostToScrapListRsp.builder()
	// 		.scrapIdList(scrapIdList)
	// 		.isClipped(true)
	// 		.build();
	// }

	@Transactional
	public Boolean createUserToBlockList(Long userId, Long blockedUserId) {
		if (Objects.equals(userId, blockedUserId)) {
			throw new BadRequestErrorException("내 계정을 차단할 수 없습니다.");
		}

		Optional<SnsUserFollow> snsUserFollowOpt = snsUserFollowRepository.findByFollowerUseAndFollowingUser(userId,
			blockedUserId);

		// 만약 팔로우 했으면, 팔로우 제거
		if(snsUserFollowOpt.isPresent()){
			profileFollowsService.deleteFollow(userId, blockedUserId);
		}

		SnsBlockUser snsBlockUser = SnsBlockUser.builder()
			.snsBlockerUser(SnsUser.builder().id(userId).build())
			.snsBlockedUser(SnsUser.builder().id(blockedUserId).build())
			.isBlockedAt(LocalDateTime.now())
			.build();
		snsBlockUserRepository.save(snsBlockUser);

		return true;
	}

	@Transactional
	public PostToScrapRsp deletePostToScrap(Long snsUserId, Long scrapBoardId, Long postId) {
		SnsScrap snsScrap = snsScrapRepository.findBySnsUser_IdAndSnsPost_IdAndSnsScrapBoard_Id(
			snsUserId, postId,
			scrapBoardId).orElseThrow(
			() -> new BadRequestErrorException("해당 게시물을 저장하지 않았습니다.")
		);

		SnsPost snsPost = snsPostRepository.findById(postId).orElseThrow(
			() -> new BadRequestErrorException("해당 게시물은 없습니다.")
		);

		snsScrapRepository.delete(snsScrap);
		// 바로 반영되도록
		snsScrapRepository.flush();

		List<SnsScrap> snsScrapList = snsScrapRepository.findBySnsUser_IdAndSnsPost_Id(snsUserId,
			postId);

		if (snsScrapList.isEmpty()) {
			SnsPostUserReaction snsPostUserReaction = snsPostUserReactionRepository.findBySnsPostAndSnsUser(postId,
				snsUserId).orElse(
					SnsPostUserReaction.builder()
						.snsPost(snsPost)
						.snsUser((SnsUser.builder().id(snsUserId).build()))
						.build()
				);
			snsPostUserReaction.setIsClipped(false);
		}

		int score = PostConst.POST_REACTION_SCRAP_SCORE;
		snsPost.setReactionCount(snsPost.getReactionCount() != null ? snsPost.getReactionCount() - score : 0);

		return PostToScrapRsp.builder()
			.scrapId(scrapBoardId.toString())
			.isScraped(false)
			.isClipped(!snsScrapList.isEmpty())
			.build();
	}

	@Transactional
	public Boolean deleteScrapBoard(Long snsUserId, Long scrapBoardId) {
		Long scrapNum =snsScrapRepository.countBySnsUser_idAndSnsScrapBoard_id(snsUserId, scrapBoardId);
		SnsScrapBoard snsScrapBoard = snsScrapBoardRepository.findBySnsUser_IdAndId(
			snsUserId,scrapBoardId
		).orElseThrow(
			() -> new BadRequestErrorException("해당 스크랩은 없습니다.")
		);

		// 스크랩안에 클립이 0개 이상 있을 경우 경우가 있는 지?
		if (scrapNum > 0 ){
			// 클립한 거 false, 이떄, 두 스크랩에 대해 동일한 포스트가 클립 되어 있을 시, 삭제 되지 않도록
			List<SnsPostUserReaction> snsPostUserReactionList = snsPostUserReactionRepository.findAllByDistinctScrapAndClipTrue(snsUserId, scrapBoardId);
			List<SnsPostUserReaction> snsPostUserReactionByIsClippedFalse = snsPostUserReactionList.stream().peek((snsPostUserReaction -> snsPostUserReaction.setIsClipped(false))).toList();

			snsPostUserReactionJdbcRepository.updateAll(snsPostUserReactionByIsClippedFalse);

			// 스크랩 삭제
			snsScrapJdbcRepository.deleteScrapDeletedByScrapBoard(snsScrapBoard.getId());
		}
		// board 삭제
		snsScrapBoardRepository.delete(snsScrapBoard);


		return false;

	}

	@Transactional
	public Boolean deleteBlockUser(Long userId, Long blockedUserId) {
		snsBlockUserRepository.deleteBySnsBlockerUser_IdAndSnsBlockedUser_Id(userId, blockedUserId);

		return true;
	}

	@Transactional
	public Boolean createUserReport(Long myUserId, Long reportedUserId, SnsUserReportCreateReq snsUserReportCreateReq){
		SnsUserReport snsPostReport = SnsUserReport.builder()
			.reporterUser(SnsUser.builder().id(myUserId).build())
			.reportedUser(SnsUser.builder().id(reportedUserId).build())
			.userReportStatus(UserReportStatus.PENDING)
			.build();

		registerReportType(snsPostReport, snsUserReportCreateReq);

		snsUserReportRepository.save(snsPostReport);
		return true;
	}


	private SnsPostRsp convertProfileScrapDaoToPostRsp(ProfilePostScrapDao profilePostScrapDao) {
		return SnsPostRsp.builder()
			.postId(profilePostScrapDao.getPostId().toString())
			.userId(profilePostScrapDao.getSnsUserId().toString())
			.username(profilePostScrapDao.getUsername())
			.profilePath(profilePostScrapDao.getProfilePath())
			.location(new Location(
				profilePostScrapDao.getLatitude(), profilePostScrapDao.getLongitude(),
				profilePostScrapDao.getAddress(), profilePostScrapDao.getBuildName()))
			.tags(profilePostScrapDao.getTags().stream().map(PostTag::getTagName).toList())
			.isFollowed(profilePostScrapDao.getFollowingId() != null)
			.isLiked(profilePostScrapDao.getIsLiked())
			.isClipped(profilePostScrapDao.getIsClipped())
			.isReposted(profilePostScrapDao.getIsReposted())
			.postTitle(profilePostScrapDao.getPostTitle())
			.postBodyText(profilePostScrapDao.getPostBodyText())
			.followable(profilePostScrapDao.getFollowable())
			.postContents(profilePostScrapDao.getSnsPostContents()
				.stream()
				.map((snsPostContent -> new PostContent(snsPostContent.getPostContentType(),
					Objects.requireNonNullElse(snsPostContent.getBucketUrl(),"") + Objects.requireNonNullElse(snsPostContent.getContent(),""),
					snsPostContent.getAscSortNum(),
					Objects.requireNonNullElse(snsPostContent.getBucketUrl(),"") + Objects.requireNonNullElse(snsPostContent.getPreviewImg(),""),
					snsPostContent.getIsUploaded(),
					snsPostContent.getVideoDuration()
				)))
				.toList())
			.postedAt(profilePostScrapDao.getPostedAt())
			.build();
	}


	private GetMyProfileInfoRsp convertUserToMyProfileInfoRsp(SnsUser snsUser) {
		return GetMyProfileInfoRsp.builder()
			.userId(snsUser.getId().toString())
			.profilePath(snsUser.getProfilePath())
			.username(snsUser.getUsername())
			.nickname(snsUser.getNickname())
			.introduce(snsUser.getUserDescription())
			.website(snsUser.getUserLink())
			.email(snsUser.getEmail())
			.birthdate(snsUser.getBirthDate())
			.gender(snsUser.getSnsUserGender().toString())
			.isPrivateProfile(snsUser.getIsPrivateProfile())
			.build();
	}

	private ScrapTargetAudience getTargetAudienceValue (String targetAudienceValue) {
		ScrapTargetAudience targetAudience;
		if (Objects.equals(targetAudienceValue, ScrapTargetAudience.PROTECTED_AUDIENCE.label())) {
			targetAudience = ScrapTargetAudience.PROTECTED_AUDIENCE;
		} else if (Objects.equals(targetAudienceValue, ScrapTargetAudience.PRIVATE_AUDIENCE.label())) {
			targetAudience = ScrapTargetAudience.PRIVATE_AUDIENCE;
		} else {
			targetAudience = ScrapTargetAudience.PUBLIC_AUDIENCE;
		}
		return targetAudience;
	}

	private void registerReportType (SnsUserReport snsUserReport, SnsUserReportCreateReq snsUserReportCreateReq) {
		switch (snsUserReportCreateReq.getUserReportReasonType()) {
			case UserReportConst.USER_INAPPROPRIATE_CONTENT_TYPE -> {
				snsUserReport.setUserReportReasonType(UserReportReasonType.INAPPROPRIATE_CONTENT);
				snsUserReport.setReportReason(UserReportConst.USER_INAPPROPRIATE_CONTENT_REASON);
			}
			case UserReportConst.USER_SPAM_OR_PROMOTIONAL_CONTENT_TYPE -> {
				snsUserReport.setUserReportReasonType(UserReportReasonType.SPAM_OR_PROMOTIONAL_CONTENT);
				snsUserReport.setReportReason(UserReportConst.USER_SPAM_OR_PROMOTIONAL_CONTENT_REASON);
			}
			case UserReportConst.USER_FALSE_INFORMATION_FRAUD_TYPE -> {
				snsUserReport.setUserReportReasonType(UserReportReasonType.FALSE_INFORMATION_FRAUD);
				snsUserReport.setReportReason(UserReportConst.USER_FALSE_INFORMATION_FRAUD_REASON);
			}
			case UserReportConst.USER_PRIVACY_VIOLATION_TYPE -> {
				snsUserReport.setUserReportReasonType(UserReportReasonType.PRIVACY_VIOLATION);
				snsUserReport.setReportReason(UserReportConst.USER_PRIVACY_VIOLATION_REASON);
			}
			case UserReportConst.USER_COPYRIGHT_INFRINGEMENT_TYPE -> {
				snsUserReport.setUserReportReasonType(UserReportReasonType.COPYRIGHT_INFRINGEMENT);
				snsUserReport.setReportReason(UserReportConst.USER_COPYRIGHT_INFRINGEMENT_REASON);
			}
			case UserReportConst.USER_HARASSMENT_OR_BULLYING_TYPE -> {
				snsUserReport.setUserReportReasonType(UserReportReasonType.HARASSMENT_OR_BULLYING);
				snsUserReport.setReportReason(UserReportConst.USER_HARASSMENT_OR_BULLYING_REASON);
			}
			case UserReportConst.POST_OTHER_REASON_TYPE -> {
				snsUserReport.setUserReportReasonType(UserReportReasonType.OTHER);
				if (snsUserReportCreateReq.getUserReportReason() == null) {
					throw new BadRequestErrorException("신고 이유를 보내 주어야 됩니다.");
				}
				snsUserReport.setReportReason(snsUserReportCreateReq.getUserReportReason());
			}
			default -> throw new BadRequestErrorException("맞지 않는 신고입니다.");
		}
	}

}
