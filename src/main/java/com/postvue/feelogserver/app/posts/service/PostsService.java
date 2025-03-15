package com.postvue.feelogserver.app.posts.service;

import java.io.File;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import org.apache.commons.io.FilenameUtils;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.jsoup.Jsoup;
import org.jsoup.UnsupportedMimeTypeException;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.postvue.feelogserver.app.cloud.service.MinioCloudService;
import com.postvue.feelogserver.app.cloud.service.R2CloudService;
import com.postvue.feelogserver.app.common.rsp.SnsPostFollowsGetRsp;
import com.postvue.feelogserver.app.externallib.ffmpeg.FfmpegProcessingService;
import com.postvue.feelogserver.app.facade.service.PostProfileFacadeService;
import com.postvue.feelogserver.app.h3.service.H3Service;
import com.postvue.feelogserver.app.maps.dto.GetAddressGeocodeRsp;
import com.postvue.feelogserver.app.maps.service.MapService;
import com.postvue.feelogserver.app.messagequeue.service.producer.VideoConversationProducer;
import com.postvue.feelogserver.app.notifications.service.NotificationService;
import com.postvue.feelogserver.app.posts.dto.common.Location;
import com.postvue.feelogserver.app.posts.dto.common.PostContent;
import com.postvue.feelogserver.app.posts.dto.req.create.SnsPostCmntCreateReq;
import com.postvue.feelogserver.app.posts.dto.req.create.SnsPostCmntUpdateReq;
import com.postvue.feelogserver.app.posts.dto.req.create.SnsPostComposeCreateReq;
import com.postvue.feelogserver.app.posts.dto.req.create.SnsPostComposeUpdateReq;
import com.postvue.feelogserver.app.posts.dto.req.create.SnsPostReportCreateReq;
import com.postvue.feelogserver.app.posts.dto.req.create.SnsRepostCreateReq;
import com.postvue.feelogserver.app.posts.dto.rsp.create.SnsPostCreateRsp;
import com.postvue.feelogserver.app.posts.dto.rsp.delete.DeleteCommentRsp;
import com.postvue.feelogserver.app.posts.dto.rsp.get.GetPostCommentsRsp;
import com.postvue.feelogserver.app.posts.dto.rsp.get.GetPostImageDocResourceRsp;
import com.postvue.feelogserver.app.posts.dto.rsp.get.GetPostLikesRsp;
import com.postvue.feelogserver.app.posts.dto.rsp.get.GetPostRepostsRsp;
import com.postvue.feelogserver.app.posts.dto.rsp.get.GetSearchPostsRsp;
import com.postvue.feelogserver.app.posts.dto.rsp.get.GetTasteForMeRsp;
import com.postvue.feelogserver.app.posts.dto.rsp.get.ScrapBoardInfo;
import com.postvue.feelogserver.app.posts.dto.rsp.get.SnsPostCommentRsp;
import com.postvue.feelogserver.app.posts.dto.rsp.get.SnsPostInfoRsp;
import com.postvue.feelogserver.app.posts.dto.rsp.get.SnsPostRsp;
import com.postvue.feelogserver.app.posts.dto.rsp.put.PostNotInterestedRsp;
import com.postvue.feelogserver.app.posts.dto.rsp.put.SnsPostClipPutRsp;
import com.postvue.feelogserver.app.posts.dto.rsp.put.SnsPostLikePutRsp;
import com.postvue.feelogserver.app.posts.vo.NearFilterType;
import com.postvue.feelogserver.app.profiles.dto.rsp.get.GetProfilePostListRsp;
import com.postvue.feelogserver.app.recomm.dto.req.SearchTypeEnum;
import com.postvue.feelogserver.domain.snsblockusers.repository.SnsBlockUserRepository;
import com.postvue.feelogserver.domain.snspostcommentlikes.SnsPostCommentLike;
import com.postvue.feelogserver.domain.snspostcommentlikes.repository.SnsPostCommentLikeRepository;
import com.postvue.feelogserver.domain.snspostcommentreactions.SnsPostCommentReaction;
import com.postvue.feelogserver.domain.snspostcommentreactions.dao.PostCommentDao;
import com.postvue.feelogserver.domain.snspostcommentreactions.repository.SnsPostCommentReactionRepository;
import com.postvue.feelogserver.domain.snspostcommentreactions.vo.PostCommentMediaType;
import com.postvue.feelogserver.domain.snspostreports.SnsPostReport;
import com.postvue.feelogserver.domain.snspostreports.repository.SnsPostReportRepository;
import com.postvue.feelogserver.domain.snspostreports.vo.PostReportReasonType;
import com.postvue.feelogserver.domain.snspostreports.vo.PostReportStatus;
import com.postvue.feelogserver.domain.snsposts.SnsPost;
import com.postvue.feelogserver.domain.snsposts.dto.SnsPostDao;
import com.postvue.feelogserver.domain.snsposts.dto.SnsPostInfoDao;
import com.postvue.feelogserver.domain.snsposts.repository.SnsPostJdbcRepository;
import com.postvue.feelogserver.domain.snsposts.repository.SnsPostRepository;
import com.postvue.feelogserver.domain.snsposts.vo.PostContentBusinessType;
import com.postvue.feelogserver.domain.snsposts.vo.PostContentType;
import com.postvue.feelogserver.domain.snsposts.vo.SnsPostContent;
import com.postvue.feelogserver.domain.snsposts.vo.TgtAudType;
import com.postvue.feelogserver.domain.snsposts.vo.TgtAudTypeValue;
import com.postvue.feelogserver.domain.snspostuserreactions.SnsPostUserReaction;
import com.postvue.feelogserver.domain.snspostuserreactions.dao.PostIsRepostedDao;
import com.postvue.feelogserver.domain.snspostuserreactions.dao.PostLikeDao;
import com.postvue.feelogserver.domain.snspostuserreactions.repository.SnsPostUserReactionRepository;
import com.postvue.feelogserver.domain.snsscrap.dao.ScrapBoardByPostInfoDao;
import com.postvue.feelogserver.domain.snsscrap.repository.SnsScrapRepository;
import com.postvue.feelogserver.domain.snstagposts.SnsTagPost;
import com.postvue.feelogserver.domain.snstagposts.respository.SnsTagPostJdbcRepository;
import com.postvue.feelogserver.domain.snstagposts.respository.SnsTagPostRepository;
import com.postvue.feelogserver.domain.snstags.SnsTag;
import com.postvue.feelogserver.domain.snstags.dao.PostTagDao;
import com.postvue.feelogserver.domain.snstags.repository.SnsTagJdbcRepository;
import com.postvue.feelogserver.domain.snstags.repository.SnsTagRepository;
import com.postvue.feelogserver.domain.snstags.vo.PostTag;
import com.postvue.feelogserver.domain.snsuserfavoritetermbookmarks.SnsUserFavoriteTermBookmark;
import com.postvue.feelogserver.domain.snsuserfavoritetermbookmarks.respository.SnsUserFavoriteTermBookmarkRepository;
import com.postvue.feelogserver.domain.snsusers.SnsUser;
import com.postvue.feelogserver.domain.snsusers.repository.SnsUserRepository;
import com.postvue.feelogserver.global.constant.HashConst;
import com.postvue.feelogserver.global.constant.MapConst;
import com.postvue.feelogserver.global.constant.PageConfigConst;
import com.postvue.feelogserver.global.constant.PostConst;
import com.postvue.feelogserver.global.constant.PostReportConst;
import com.postvue.feelogserver.global.constant.MediaConfigConst;
import com.postvue.feelogserver.global.exception.BadRequestErrorException;
import com.postvue.feelogserver.global.exception.BaseException;
import com.postvue.feelogserver.global.exception.ForbiddenErrorException;
import com.postvue.feelogserver.global.exception.InternalServerErrorException;
import com.postvue.feelogserver.global.exception.UnauthorizedErrorException;
import com.postvue.feelogserver.global.util.converter.TagConverter;
import com.postvue.feelogserver.global.util.generator.FileUtils;
import com.postvue.feelogserver.global.util.response.ObjectConvertRspUtil;
import com.postvue.feelogserver.global.util.validation.StringValidUtil;
import com.postvue.feelogserver.global.util.validation.UploadFileValidationUtils;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostsService {
	private final SnsPostRepository snsPostRepository;
	private final SnsPostCommentReactionRepository snsPostCommentReactionRepository;
	private final SnsPostUserReactionRepository snsPostUserReactionRepository;
	private final SnsTagRepository snsTagRepository;
	private final SnsTagPostRepository snsTagPostRepository;
	private final SnsTagJdbcRepository snsTagJdbcRepository;
	private final SnsTagPostJdbcRepository snsTagPostJdbcRepository;
	private final SnsPostJdbcRepository snsPostJdbcRepository;
	private final SnsUserRepository snsUserRepository;
	private final SnsPostCommentLikeRepository snsPostCommentLikeRepository;
	private final SnsUserFavoriteTermBookmarkRepository snsUserFavoriteTermBookmarkRepository;
	private final SnsPostReportRepository snsPostReportRepository;
	private final NotificationService notificationService;
	private final MapService mapService;
	private final R2CloudService r2CloudService;
	private final MinioCloudService minioCloudService;
	private final FfmpegProcessingService ffmpegProcessingService;
	private final VideoConversationProducer videoConversationProducer;
	private final SnsBlockUserRepository snsBlockUserRepository;
	private final SnsScrapRepository snsScrapRepository;

	private final PostProfileFacadeService postProfileFacadeService;
	private final H3Service h3Service;
	private final GeometryFactory geometryFactory = new GeometryFactory();


	@Value("${file.imageSize}")
	private Integer imageFileSize;

	@Value("${file.videoSize}")
	private Integer videoFileSize;

	@Value("${cloud.cloudflare.service.contentBucket.bucketPublicUrl}")
	private String imageBucketPublicUrl;

	@Value("${cloud.minio.service.videos.bucketPublicUrl}")
	private String videoBucketPublicUrl;


	@Transactional
	public GetTasteForMeRsp findTasteForMePosts(Long snsUserId, Integer page, Long cursorId) {
		Long cursor = Long.valueOf(PageConfigConst.ZERO_ID);
		List<SnsPostDao> snsPostList = new ArrayList<>();
		List<SnsPostDao> snsPostsByTag = snsPostRepository.selectTasteForMeByTag(snsUserId,
			page * PageConfigConst.STUFF_FOR_ME_BY_TAG_PAGE_SIZE,
			PageConfigConst.STUFF_FOR_ME_BY_TAG_PAGE_SIZE);

		int popularPageNum = PageConfigConst.POPULAR_PAGE_NUM + (snsPostsByTag.isEmpty() || cursorId <= 0 ? PageConfigConst.STUFF_FOR_ME_BY_TAG_PAGE_SIZE : 0);

		List<SnsPostDao> snsPostsByPopular = snsPostRepository.selectTasteForMeByPopular(snsUserId,
			page * popularPageNum, popularPageNum,
			LocalDateTime.now());

		snsPostList.addAll(snsPostsByTag);
		snsPostList.addAll(snsPostsByPopular);

		if (cursorId > 0) {
			int followPageNum = (int)(
				Math.random() * (PageConfigConst.FOLLOW_MAX_PAGE_NUM - PageConfigConst.FOLLOW_MIN_PAGE_NUM) + PageConfigConst.FOLLOW_MIN_PAGE_NUM)
				+ (PageConfigConst.STUFF_FOR_ME_BY_TAG_PAGE_SIZE + PageConfigConst.POPULAR_PAGE_NUM - snsPostList.size());

			List<SnsPostDao> snsPostsByFollow = snsPostRepository.selectTasteForMeByFollow(snsUserId, cursorId,
				followPageNum);
			if (!snsPostsByFollow.isEmpty()) {
				cursor = snsPostsByFollow.get(snsPostsByFollow.size() - 1).getCursorId();
				snsPostList.addAll(snsPostsByFollow);
			}
		}

		List<SnsPostRsp> snsPostRspList = new ArrayList<>(postProfileFacadeService.getPostGetRspList(snsPostList).stream().collect(
			Collectors.toMap(
				SnsPostRsp::getPostId,
				snsPostRsp -> snsPostRsp,
				(existing, replacement) -> existing
			)
		).values().stream().toList());
		Collections.shuffle(snsPostRspList);

		return GetTasteForMeRsp.builder()
			.cursorId(cursor.toString())
			.snsPostRspList(snsPostRspList)
			.build();
	}

	@Transactional
	public GetTasteForMeRsp findFollowForMePosts(Long snsUserId, Long cursorId) {
		Long cursor = Long.valueOf(PageConfigConst.ZERO_ID);
		List<SnsPostDao> snsPostList = new ArrayList<>();

		int followPageNum = PageConfigConst.FOLLOW_FOR_ME_PAGE_SIZE;

		if (cursorId > 0) {
			List<SnsPostDao> snsPostsByFollow = snsPostRepository.selectTasteForMeByFollow(snsUserId, cursorId,
				followPageNum);
			if (!snsPostsByFollow.isEmpty()) {
				cursor = snsPostsByFollow.get(snsPostsByFollow.size() - 1).getCursorId();
				snsPostList.addAll(snsPostsByFollow);
			}
		}

		List<SnsPostRsp> snsPostRspList = new ArrayList<>(postProfileFacadeService.getPostGetRspList(snsPostList).stream().collect(
			Collectors.toMap(
				SnsPostRsp::getPostId,
				snsPostRsp -> snsPostRsp,
				(existing, replacement) -> existing
			)
		).values().stream().toList());

		return GetTasteForMeRsp.builder()
			.cursorId(cursor.toString())
			.snsPostRspList(snsPostRspList)
			.build();
	}

	@Transactional
	public List<SnsPostRsp> findTagForMePosts(Long snsUserId, Long snsPostId) {
		List<SnsPostDao> snsPosts = snsPostRepository.selectTagForMe(snsUserId, snsPostId,
			PageConfigConst.DEFAULT_PAGE_SIZE);

		return postProfileFacadeService.getPostGetRspList(snsPosts);
	}

	@Transactional
	public List<SnsPostRsp> findNearForMePosts(Long snsUserId, Integer page, Float latitude, Float longitude,
		String nearFilter, LocalDateTime startDate, LocalDateTime endDate) {
		PostContentBusinessType postContentBusinessType = getPostContentBusinessType(
			nearFilter);

		LocalDateTime startDateTime = startDate != null ? startDate : LocalDateTime.of(1001,1,1,0,0,0);
		LocalDateTime endDateTime = endDate != null ? endDate : LocalDateTime.now();

		// try {
			if (nearFilter.equals(NearFilterType.NEAR_FILTER_ALL_TYPE)) {
				List<SnsPostDao> snsPosts = snsPostRepository.selectNearForMe(snsUserId,
					page * PageConfigConst.NEAR_FOR_ME_BY_TAG_PAGE_SIZE,
					PageConfigConst.NEAR_FOR_ME_BY_TAG_PAGE_SIZE, latitude, longitude, h3Service.getNearbyH3Cells(latitude, longitude, MapConst.MAX_MAP_POST_DISTANCE_NUM),
					LocalDateTime.now(), startDateTime, endDateTime);

				return postProfileFacadeService.getPostGetRspList(snsPosts);
			} else if (postContentBusinessType != null) {
				List<SnsPostDao> snsPosts = snsPostRepository.selectNearForMeBy(snsUserId,
					page * PageConfigConst.NEAR_FOR_ME_BY_TAG_PAGE_SIZE,
					PageConfigConst.NEAR_FOR_ME_BY_TAG_PAGE_SIZE, latitude, longitude, h3Service.getNearbyH3Cells(latitude, longitude, MapConst.MAX_MAP_POST_DISTANCE_NUM),
					postContentBusinessType.label(),
					LocalDateTime.now(), startDateTime, endDateTime
				);

				return postProfileFacadeService.getPostGetRspList(snsPosts);
			} else {
				throw new BadRequestErrorException("해당 콘텐츠 타입은 없습니다.");
			}
		// }
		// catch (BaseException e){
		// 	throw e;
		// }
		// catch (Exception e){
		// 	log.error(e.getMessage());
		// 	throw new InternalServerErrorException("서버 오류로 인해 처리되지 않았습니다.");
		// }
	}

	@Transactional
	public List<SnsPostRsp> findMapPostsByMe(Long snsUserId, Integer page) {
		List<SnsPostDao> snsPosts = snsPostRepository.selectMapPostByMe(snsUserId,
			page * PageConfigConst.MAP_POST_FOR_ME_SIZE,
			PageConfigConst.MAP_POST_FOR_ME_SIZE);

		return postProfileFacadeService.getPostGetRspList(snsPosts);
	}

	private static PostContentBusinessType getPostContentBusinessType(String nearFilter) {
		return switch (nearFilter) {
			case NearFilterType.NEAR_FILTER_GOOD_PLACE_TYPE -> PostContentBusinessType.BUSINESS_GOOD_PLACE_TYPE;
			case NearFilterType.NEAR_FILTER_CAFE_TYPE -> PostContentBusinessType.BUSINESS_CAFE_TYPE;
			case NearFilterType.NEAR_FILTER_ATTRACTION_TYPE -> PostContentBusinessType.BUSINESS_ATTRACTION_TYPE;
			case NearFilterType.NEAR_FILTER_PARK_TYPE -> PostContentBusinessType.BUSINESS_PARK_TYPE;
			case NearFilterType.NEAR_FILTER_DAILY_TYPE -> PostContentBusinessType.BUSINESS_DAILY_TYPE;
			default -> null;
		};
	}

	@Transactional
	public GetProfilePostListRsp findProfilePosts(Long snsUserId, String username, Long snsPostId) {
		List<SnsPostDao> snsPosts = snsPostRepository.selectProfilePosts(snsUserId, username, snsPostId,
			PageConfigConst.DEFAULT_PAGE_SIZE);

		List<SnsPostRsp> snsPostRspList = postProfileFacadeService.getPostGetRspList(snsPosts);
		if (snsPostRspList.isEmpty()) {
			return GetProfilePostListRsp.builder()
				.cursorId(PageConfigConst.ZERO_ID)
				.snsPostRspList(new ArrayList<>())
				.build();
		} else {
			return GetProfilePostListRsp.builder()
				.cursorId(snsPostRspList.get(snsPostRspList.size() - 1).getPostId())
				.snsPostRspList(snsPostRspList)
				.build();
		}

	}

	@Transactional
	public SnsPostRsp findDetailPost(Long userId, Long postId) {
		Optional<SnsPostDao> snsPostDaoOpt = snsPostRepository.selectDetailPost(userId, postId);

		if (snsPostDaoOpt.isEmpty()){
			throw new BadRequestErrorException("해당 게시물은 없습니다.");
		}
		else{
			SnsPostDao snsPostDao = snsPostDaoOpt.get();


			return postProfileFacadeService.convertToPostGetRsp(snsPostDao);

		}
	}

	@Transactional
	public SnsPostInfoRsp findPostInfo(Long userId, Long postId) {
		SnsPostInfoDao snsPostInfoDao = snsPostRepository.selectPostInfo(postId, userId).orElseThrow(
			() -> new BadRequestErrorException("해당 게시물은 없습니다.")
		);
		List<ScrapBoardByPostInfoDao> scrapBoardIdList = snsScrapRepository.findScrapBoardIdByMyUserIdAndPostId(userId, postId);

		if (!Objects.equals(snsPostInfoDao.getSnsUserId(), userId)) {
			throw new UnauthorizedErrorException("권한이 없습니다.");
		}
		return convertToPostInfoRsp(snsPostInfoDao, scrapBoardIdList);
	}

	@Transactional
	public GetPostCommentsRsp findPostComments(Long snsUserId, Long snsPostId, Long snsPostCommentReactionId) {
		Pageable pageable = PageRequest.of(PageConfigConst.PAGE_INIT_NUM, PageConfigConst.DEFAULT_PAGE_SIZE);
		List<SnsPostCommentRsp> snsPostCommentRspList = snsPostCommentReactionRepository.selectCommentByPostId(
				snsPostId,
				snsUserId,
				snsPostCommentReactionId, pageable)
			.stream()
			.map((this::convertToPostCommentGetRsp))
			.sorted(Comparator.comparingInt(comment -> -(comment.getLikeCount() + comment.getCommentCount())))
			.toList();
		if (!snsPostCommentRspList.isEmpty()) {
			return new GetPostCommentsRsp(
				snsPostCommentRspList.get(snsPostCommentRspList.size() - 1).getPostCommentId(), snsPostCommentRspList);
		} else {
			return new GetPostCommentsRsp(PageConfigConst.ZERO_ID, new ArrayList<>());
		}
	}

	@Transactional
	public GetPostCommentsRsp findPostCommentsByComment(Long snsPostId, Long userId, Long postCommentId,
		Long snsPostCommentReactionId, Boolean isReplyToReply) {
		Pageable pageable = PageRequest.of(PageConfigConst.PAGE_INIT_NUM, PageConfigConst.POST_COMMENT_REPLY_NUM);
		List<SnsPostCommentRsp> getPostCommentsRspList = snsPostCommentReactionRepository.selectCommentByCommentId(
				snsPostId, userId, postCommentId,
				snsPostCommentReactionId, pageable)
			.stream()
			.map((postCommentDao -> convertToPostCommentGetRspByReply(postCommentDao, isReplyToReply)))
			.toList();
		return ObjectConvertRspUtil.GenericObjectListRsp(getPostCommentsRspList, GetPostCommentsRsp::new,
			getPostCommentsRspList.isEmpty() ? "" :
				getPostCommentsRspList.get(getPostCommentsRspList.size() - 1).getPostCommentId());
	}

	@Transactional
	public List<SnsPostCommentRsp> findPostRepliesByReplyCommentId(Long snsPostId, Long userId,
		Long replyCommentId) {
		return snsPostCommentReactionRepository.selectRepliesByReplyCommentId(
				snsPostId, userId, replyCommentId)
			.stream()
			.map((this::convertToPostCommentGetRsp))
			.toList();
	}

	@Transactional
	public GetPostLikesRsp findPostLikesByPost(Long snsPostId,
		Long cursorId, Long userId) {
		Pageable pageable = PageRequest.of(PageConfigConst.PAGE_INIT_NUM, PageConfigConst.DEFAULT_PAGE_SIZE);
		List<PostLikeDao> postLikeDaoList = snsPostUserReactionRepository.selectLikeListByPost(snsPostId, cursorId,
				userId, pageable)
			.stream()
			.toList();

		if (postLikeDaoList.isEmpty()) {
			return new GetPostLikesRsp(PageConfigConst.ZERO_ID, new ArrayList<>());

		} else {
			return new GetPostLikesRsp(
				postLikeDaoList.get(postLikeDaoList.size() - 1).getCursorId().toString(),
				getPostLikeList(postLikeDaoList));
		}
	}


	@Transactional
	public GetPostRepostsRsp findPostIsRepostedListByPost(Long snsPostId,
		Long cursorId, Long userId) {
		Pageable pageable = PageRequest.of(PageConfigConst.PAGE_INIT_NUM, PageConfigConst.DEFAULT_PAGE_SIZE);
		List<PostIsRepostedDao> postLikeDaoList = snsPostUserReactionRepository.selectRepostListByPost(snsPostId,
				cursorId,
				userId, pageable)
			.stream()
			.toList();
		if (!postLikeDaoList.isEmpty()) {
			return new GetPostRepostsRsp(postLikeDaoList.get(postLikeDaoList.size() - 1).getUserId().toString(),
				getPostIsRepostedList(postLikeDaoList));
		} else {
			return new GetPostRepostsRsp(PageConfigConst.ZERO_ID, new ArrayList<>());
		}
	}

	@Transactional
	public GetSearchPostsRsp findPostBySearchQueryByPopular(Long snsUserId, Integer page, String searchQuery,
		Boolean isFetchFavorite) {
		boolean isBookMarkFavoriteTerm = false;
		if (isFetchFavorite || Objects.equals(page, PageConfigConst.PAGE_INIT_NUM)) {
			isFetchFavorite = true;
			Optional<SnsUserFavoriteTermBookmark> favoriteTermBookmarkOptional = snsUserFavoriteTermBookmarkRepository.findBySnsUser_IdAndFavoriteTermName(
				snsUserId, searchQuery);
			isBookMarkFavoriteTerm = favoriteTermBookmarkOptional.isPresent();
		}

		if (searchQuery.startsWith(HashConst.HAST_TAG_PREFIX)){
			List<SnsPostDao> snsPosts = snsPostRepository.selectTagPostBySearchQueryPopular(snsUserId,
				searchQuery.replaceFirst("^"+HashConst.HAST_TAG_PREFIX, ""),
				page * PageConfigConst.DEFAULT_PAGE_SIZE,
				PageConfigConst.DEFAULT_PAGE_SIZE, LocalDateTime.now());

			List<SnsPostRsp> snsPostRspList = postProfileFacadeService.getPostGetRspList(snsPosts);
			return GetSearchPostsRsp.builder()
				.snsPostRspList(snsPostRspList)
				.isFetchFavoriteState(isFetchFavorite)
				.isBookMarkedFavoriteTerm(isBookMarkFavoriteTerm)
				.build();
		}
		else{
			List<SnsPostDao> snsPosts = snsPostRepository.selectPostBySearchQueryPopular(snsUserId,
				searchQuery,
				page * PageConfigConst.DEFAULT_PAGE_SIZE,
				PageConfigConst.DEFAULT_PAGE_SIZE, LocalDateTime.now());

			List<SnsPostRsp> snsPostRspList = postProfileFacadeService.getPostGetRspList(snsPosts);
			return GetSearchPostsRsp.builder()
				.snsPostRspList(snsPostRspList)
				.isFetchFavoriteState(isFetchFavorite)
				.isBookMarkedFavoriteTerm(isBookMarkFavoriteTerm)
				.build();
		}
	}

	@Transactional
	public GetSearchPostsRsp findPostBySearchQueryByRecently(Long snsUserId, Integer page, String searchQuery,
		Boolean isFetchFavorite) {
		boolean isBookMarkFavoriteTerm = false;
		if (isFetchFavorite || Objects.equals(page, PageConfigConst.PAGE_INIT_NUM)) {
			isFetchFavorite = true;
			Optional<SnsUserFavoriteTermBookmark> favoriteTermBookmarkOptional = snsUserFavoriteTermBookmarkRepository
				.findBySnsUser_IdAndFavoriteTermName(
					snsUserId, searchQuery);
			isBookMarkFavoriteTerm = favoriteTermBookmarkOptional.isPresent();
		}

		String searchWord = searchQuery.startsWith(HashConst.HAST_TAG_PREFIX) ? searchQuery.replaceFirst("^"+HashConst.HAST_TAG_PREFIX, "") : searchQuery;

		List<SnsPostDao> snsPosts = snsPostRepository.selectPostBySearchQueryRecently(snsUserId,
			page * PageConfigConst.DEFAULT_PAGE_SIZE,
			searchWord,
			PageConfigConst.DEFAULT_PAGE_SIZE);

		List<SnsPostRsp> snsPostRspList = postProfileFacadeService.getPostGetRspList(snsPosts);
		return GetSearchPostsRsp.builder()
			.snsPostRspList(snsPostRspList)

			.isFetchFavoriteState(isFetchFavorite)
			.isBookMarkedFavoriteTerm(isBookMarkFavoriteTerm)
			.build();
	}

	@Transactional
	public GetSearchPostsRsp findPostBySearchQueryByNear(
		Long snsUserId, Integer page, String searchQuery,
		Float latitude, Float longitude,
		Boolean isFetchFavorite
	) {
		boolean isBookMarkFavoriteTerm = false;
		if (isFetchFavorite || Objects.equals(page, PageConfigConst.PAGE_INIT_NUM)) {
			isFetchFavorite = true;
			Optional<SnsUserFavoriteTermBookmark> favoriteTermBookmarkOptional = snsUserFavoriteTermBookmarkRepository
				.findBySnsUser_IdAndFavoriteTermName(
					snsUserId, searchQuery);
			isBookMarkFavoriteTerm = favoriteTermBookmarkOptional.isPresent();
		}

		String searchWord = searchQuery.startsWith(HashConst.HAST_TAG_PREFIX) ? searchQuery.replaceFirst("^"+HashConst.HAST_TAG_PREFIX, "") : searchQuery;

		List<SnsPostDao> snsPosts = snsPostRepository.selectPostBySearchQueryNear(snsUserId,
			page * PageConfigConst.DEFAULT_PAGE_SIZE,
			searchWord,
			PageConfigConst.DEFAULT_PAGE_SIZE,latitude,longitude,
			h3Service.getNearbyH3Cells(latitude,longitude,MapConst.MAX_MAP_MY_NEAR_POST_DISTANCE_NUM)
		);

		List<SnsPostRsp> snsPostRspList = postProfileFacadeService.getPostGetRspList(snsPosts);
		return GetSearchPostsRsp.builder()
			.snsPostRspList(snsPostRspList)

			.isFetchFavoriteState(isFetchFavorite)
			.isBookMarkedFavoriteTerm(isBookMarkFavoriteTerm)
			.build();
	}

	@Transactional
	public List<SnsPostRsp> findPostRelation(Long postId, Long userId, String searchType, Integer page) {
		String searchTypeValue = searchType != null ? searchType : "";
		SnsPost snsPost = snsPostRepository.findById(postId).orElseThrow(
			() -> new BadRequestErrorException("해당 게시물은 없습니다.")
		);
		List<SnsTagPost> snsTagPostList = snsTagPostRepository.findBySnsPost_Id(postId);

		List<String> tagList = snsTagPostList.stream().map(snsTagPost -> snsTagPost.getSnsTag().getTagName()).toList();
		String relatedTagListString = TagConverter.convertTagListToTagListSqlString(tagList);

		List<SnsPostDao> snsPostDaos;
		List<Long> h3IndexList = new ArrayList<>();
		if (snsPost.getLatitude() != null && snsPost.getLongitude() !=null){
			h3IndexList.addAll(h3Service.getNearbyH3Cells(snsPost.getLatitude(),snsPost.getLongitude(),MapConst.MAX_MAP_MY_NEAR_POST_DISTANCE_NUM));
		}

		if (Objects.equals(searchTypeValue, SearchTypeEnum.recomm.name())) {
			snsPostDaos = snsPostRepository.selectPostRelation(relatedTagListString, userId, postId,
				page * PageConfigConst.POST_RELATION_RECOMM_PAGE_NUM_BY_RECOMM,
				PageConfigConst.POST_RELATION_RECOMM_PAGE_NUM_BY_RECOMM,
				page * PageConfigConst.POST_RELATION_DISTANCE_PAGE_NUM_BY_RECOMM,
				PageConfigConst.POST_RELATION_DISTANCE_PAGE_NUM_BY_RECOMM,
				page * PageConfigConst.POST_RELATION_LIVE_PAGE_NUM_BY_RECOMM,
				PageConfigConst.POST_RELATION_LIVE_PAGE_NUM_BY_RECOMM,
				h3IndexList,
				snsPost.getLatitude(),
				snsPost.getLongitude(),
				LocalDateTime.now()
			);
			Collections.shuffle(snsPostDaos);
		}

		else if (Objects.equals(searchTypeValue, SearchTypeEnum.live.name())){
			snsPostDaos = snsPostRepository.selectPostRelation(relatedTagListString, userId, postId,
				page * PageConfigConst.POST_RELATION_RECOMM_PAGE_NUM_BY_LIVE,
				PageConfigConst.POST_RELATION_RECOMM_PAGE_NUM_BY_LIVE,
				page * PageConfigConst.POST_RELATION_DISTANCE_PAGE_NUM_BY_LIVE,
				PageConfigConst.POST_RELATION_DISTANCE_PAGE_NUM_BY_LIVE,
				page * PageConfigConst.POST_RELATION_LIVE_PAGE_NUM_BY_LIVE,
				PageConfigConst.POST_RELATION_LIVE_PAGE_NUM_BY_LIVE,
				h3IndexList,
				snsPost.getLatitude(),
				snsPost.getLongitude(),
				LocalDateTime.now()
			);
			Collections.shuffle(snsPostDaos);
		}
		else if (Objects.equals(searchTypeValue, SearchTypeEnum.distance.name())){
			snsPostDaos = snsPostRepository.selectPostRelation(relatedTagListString, userId, postId,
				page * PageConfigConst.POST_RELATION_RECOMM_PAGE_NUM_BY_DISTANCE,
				PageConfigConst.POST_RELATION_RECOMM_PAGE_NUM_BY_DISTANCE,
				page * PageConfigConst.POST_RELATION_DISTANCE_PAGE_NUM_BY_DISTANCE,
				PageConfigConst.POST_RELATION_DISTANCE_PAGE_NUM_BY_DISTANCE,
				page * PageConfigConst.POST_RELATION_LIVE_PAGE_NUM_BY_DISTANCE,
				PageConfigConst.POST_RELATION_LIVE_PAGE_NUM_BY_DISTANCE,
				h3IndexList,
				snsPost.getLatitude(),
				snsPost.getLongitude(),
				LocalDateTime.now()
			);
			Collections.shuffle(snsPostDaos);
		}
		else{
			snsPostDaos = snsPostRepository.selectPostRelation(relatedTagListString, userId, postId,
				page * PageConfigConst.POST_RELATION_PAGE_NUM,
				PageConfigConst.POST_RELATION_PAGE_NUM,
				page * PageConfigConst.POST_RELATION_PAGE_NUM,
				PageConfigConst.POST_RELATION_PAGE_NUM,
				page * PageConfigConst.POST_RELATION_PAGE_NUM,
				PageConfigConst.POST_RELATION_PAGE_NUM,
				h3IndexList,
				snsPost.getLatitude(),
				snsPost.getLongitude(),
				LocalDateTime.now()
			);
			Collections.shuffle(snsPostDaos);
		}

		snsPostDaos = snsPostDaos.stream()
			.filter((snsPostDao -> !Objects.equals(snsPostDao.getPostId(), postId)))
			.toList();


		return postProfileFacadeService.getPostGetRspList(snsPostDaos);
	}

	@Transactional
	public SnsPostClipPutRsp modifyPostClip(Long snsPostId, Long snsUserId) {
		SnsPostUserReaction snsPostUserReaction = snsPostUserReactionRepository.findBySnsPostAndSnsUser(snsPostId,
			snsUserId).orElse(
			SnsPostUserReaction.builder()
				.snsPost(SnsPost.builder().id(snsPostId).build())
				.snsUser(SnsUser.builder().id(snsUserId).build())
				.isClipped(false)
				.build()
		);
		snsPostUserReaction.setIsClipped(!snsPostUserReaction.getIsClipped());
		snsPostUserReaction.setIsClippedAt(LocalDateTime.now());
		snsPostUserReactionRepository.save(snsPostUserReaction);

		return new SnsPostClipPutRsp(snsPostUserReaction.getIsClipped());
	}

	@Transactional
	public SnsPostLikePutRsp modifyPostLike(Long snsPostId, Long snsUserId) {
		Optional<SnsPostUserReaction> snsPostUserReactionOpt = snsPostUserReactionRepository.findBySnsPostAndSnsUser(
			snsPostId,
			snsUserId);
		SnsPostUserReaction snsPostUserReaction;
		SnsPost snsPost = snsPostRepository.findById(snsPostId).orElseThrow(
			() -> new BadRequestErrorException("해당 게시물은 없습니다.")
		);
		if (snsPostUserReactionOpt.isEmpty()) {
			snsPostUserReaction = SnsPostUserReaction.builder()
				.snsPost(snsPost)
				.snsUser(snsUserRepository.findById(
					snsUserId
				).orElseThrow(() -> new BadRequestErrorException("해당 계정은 없습니다.")))
				.isLiked(false)
				.build();

		} else {
			snsPostUserReaction = snsPostUserReactionOpt.get();
		}

		boolean isBlocked = snsBlockUserRepository.findIsBlockUser(snsUserId, snsPostUserReaction.getSnsPost().getSnsUser().getId());
		if (isBlocked){
			throw new ForbiddenErrorException("비공개 계정에 대해서 좋아요를 남길 수 없습니다.");
		}
		snsPostUserReaction.setIsLiked(!snsPostUserReaction.getIsLiked());
		snsPostUserReaction.setIsLikedAt(LocalDateTime.now());

		int score = PostConst.POST_REACTION_HEART_SCORE;
		snsPost.setReactionCount(
			snsPostUserReaction.getIsLiked()
				? (snsPost.getReactionCount() != null ? snsPost.getReactionCount() : 0) + score
				: (snsPost.getReactionCount() != null ? snsPost.getReactionCount() -score : 0));

		snsPostUserReactionRepository.save(snsPostUserReaction);

		//알림
		if (snsPostUserReaction.getIsLiked() && !snsUserId.equals(snsPostUserReaction.getSnsPost()
			.getSnsUser()
			.getId())) {
			notificationService.processPostLikeNotification(snsPostUserReaction.getSnsPost(), snsPostUserReaction);
		}

		return new SnsPostLikePutRsp(snsPostUserReaction.getIsLiked());
	}

	// @Transactional
	// public SnsPostCreateRsp savePostByResourceLink(Long userId,
	// 	SnsPostCreateByResourceLinkReq snsPostCreateByResourceLinkReq) throws
	// 	JsonProcessingException {
	// 	// 유저 불러오기
	// 	SnsUser snsUser = new SnsUser();
	// 	snsUser.setId(userId);
	// 	List<String> tagNameList = snsPostCreateByResourceLinkReq.getTagList();
	//
	// 	// QUERY 2: SELECT, EXITING SNS TAG LIST
	// 	List<SnsTag> existingSnsTagEntityList = snsTagRepository.findAllByTagNameIn(tagNameList);
	//
	// 	List<String> newTagNameList = tagNameList.stream()
	// 		.filter(tagName -> !existingSnsTagEntityList.stream().map(SnsTag::getTagName).toList().contains(tagName))
	// 		.toList();
	//
	// 	// QUERY 3: BULK INSERT, NEW TAG LIST
	// 	List<SnsTag> newSnsTagEntityList = newTagNameList.stream().map(tagName -> SnsTag.builder()
	// 		.tagName(tagName)
	// 		.build()).toList();
	//
	// 	snsTagJdbcRepository.saveAll(newSnsTagEntityList);
	//
	// 	List<SnsTag> snsRepostTagEntityList = new ArrayList<>();
	// 	snsRepostTagEntityList.addAll(existingSnsTagEntityList);
	// 	snsRepostTagEntityList.addAll(newSnsTagEntityList);
	//
	// 	// QUERY 4: INSERT, NEW POST
	// 	SnsPost snsNewPost = SnsPost.builder()
	// 		.snsPostContents(snsPostCreateByResourceLinkReq.getPostContents()
	// 			.stream()
	// 			.map((postContent -> new SnsPostContent(
	// 				postContent.getPostContentType(),
	// 				postContent.getAscSortNum(),
	// 				postContent.getContent())))
	// 			.toList())
	// 		.snsUser(snsUser)
	// 		.postTitle(snsPostCreateByResourceLinkReq.getTitle())
	// 		.postBodyText(snsPostCreateByResourceLinkReq.getBodyText())
	// 		.latitude(snsPostCreateByResourceLinkReq.getLatitude())
	// 		.longitude(snsPostCreateByResourceLinkReq.getLongitude())
	// 		.tags(snsRepostTagEntityList.stream().map(snsTag -> PostTag.builder()
	// 			.tagId(snsTag.getId())
	// 			.tagName(snsTag.getTagName())
	// 			.build()).toList())
	// 		.build();
	//
	// 	if (!snsPostCreateByResourceLinkReq.getAddress().trim().isEmpty()) {
	// 		GetAddressGeocodeRsp getAddressGeocodeRsp = mapService.getAddressGeocode(
	// 			snsPostCreateByResourceLinkReq.getAddress());
	//
	// 		snsNewPost.setAddress(snsPostCreateByResourceLinkReq.getAddress());
	// 		snsNewPost.setLatitude(getAddressGeocodeRsp.getLatitude());
	// 		snsNewPost.setLongitude(getAddressGeocodeRsp.getLongitude());
	// 	}
	//
	// 	snsPostJdbcRepository.insertPost(snsNewPost);
	//
	// 	// QUERY 5: INSERT SNS POST USER REACTION
	// 	SnsPostUserReaction snsPostUserReaction = SnsPostUserReaction.builder()
	// 		.snsPost(snsNewPost)
	// 		.snsUser(snsUser)
	// 		.build();
	// 	snsPostUserReactionRepository.save(snsPostUserReaction);
	//
	// 	// QUERY 6: BULK INSERT, NEW SNS TAG POST LIST
	// 	snsTagPostJdbcRepository.saveAll(snsRepostTagEntityList.stream().map(snsTag -> SnsTagPost.builder()
	// 		.snsTag(snsTag)
	// 		.snsPost(snsNewPost)
	// 		.build()).toList());
	//
	// 	return new SnsPostCreateRsp(snsPostUserReaction.getIsReposted(),
	// 		convertToPostGetRspByPostCreated(snsNewPost));
	// }

	// @Transactional
	// public SnsPostCreateRsp savePostByFile(Long userId, SnsPostCreateByFileReq snsPostCreateByFileReq) throws
	// 	JsonProcessingException {
	// 	// 유저 불러오기
	// 	SnsUser snsUser = new SnsUser();
	// 	snsUser.setId(userId);
	// 	List<String> tagNameList = snsPostCreateByFileReq.getTagList();
	//
	// 	// QUERY 2: SELECT, EXITING SNS TAG LIST
	// 	List<SnsTag> existingSnsTagEntityList = snsTagRepository.findAllByTagNameIn(tagNameList);
	//
	// 	List<String> newTagNameList = tagNameList.stream()
	// 		.filter(tagName -> !existingSnsTagEntityList.stream().map(SnsTag::getTagName).toList().contains(tagName))
	// 		.toList();
	//
	// 	// QUERY 3: BULK INSERT, NEW TAG LIST
	// 	List<SnsTag> newSnsTagEntityList = newTagNameList.stream().map(tagName -> SnsTag.builder()
	// 		.tagName(tagName)
	// 		.build()).toList();
	//
	// 	snsTagJdbcRepository.saveAll(newSnsTagEntityList);
	//
	// 	List<SnsTag> snsRepostTagEntityList = new ArrayList<>();
	// 	snsRepostTagEntityList.addAll(existingSnsTagEntityList);
	// 	snsRepostTagEntityList.addAll(newSnsTagEntityList);
	//
	// 	// QUERY 4: INSERT, NEW POST
	// 	SnsPost snsNewPost = SnsPost.builder()
	// 		.snsPostContents(snsPostCreateByFileReq.getPostContents()
	// 			.stream()
	// 			.map((postContent -> new SnsPostContent(
	// 				postContent.getPostContentType(),
	// 				postContent.getAscSortNum(),
	// 				postContent.getContent())))
	// 			.toList())
	// 		.snsUser(snsUser)
	// 		.postTitle(snsPostCreateByFileReq.getTitle())
	// 		.postBodyText(snsPostCreateByFileReq.getBodyText())
	// 		.latitude(snsPostCreateByFileReq.getLatitude())
	// 		.longitude(snsPostCreateByFileReq.getLongitude())
	// 		.tags(snsRepostTagEntityList.stream().map(snsTag -> PostTag.builder()
	// 			.tagId(snsTag.getId())
	// 			.tagName(snsTag.getTagName())
	// 			.build()).toList())
	// 		.build();
	//
	// 	snsPostJdbcRepository.insertPost(snsNewPost);
	//
	// 	// QUERY 5: INSERT SNS POST USER REACTION
	// 	SnsPostUserReaction snsPostUserReaction = SnsPostUserReaction.builder()
	// 		.snsPost(snsNewPost)
	// 		.snsUser(snsUser)
	// 		.build();
	// 	snsPostUserReactionRepository.save(snsPostUserReaction);
	//
	// 	// QUERY 6: BULK INSERT, NEW SNS TAG POST LIST
	// 	snsTagPostJdbcRepository.saveAll(snsRepostTagEntityList.stream().map(snsTag -> SnsTagPost.builder()
	// 		.snsTag(snsTag)
	// 		.snsPost(snsNewPost)
	// 		.build()).toList());
	//
	// 	return new SnsPostCreateRsp(snsPostUserReaction.getIsReposted(),
	// 		convertToPostGetRspByPostCreated(snsNewPost));
	// }

	@Transactional
	public SnsPostCreateRsp savePostRepost(Long snsPostId, Long userId, SnsRepostCreateReq snsRepostCreateReq) throws
		Exception {
		// 유저 불러오기
		SnsUser snsUser = new SnsUser();
		snsUser.setId(userId);
		List<String> tagNameList = snsRepostCreateReq.getTagList();

		// QUERY 1: SELECT, POST
		SnsPost snsOriginPost = snsPostRepository.findById(snsPostId).orElseThrow(
			() -> new BadRequestErrorException("해당 게시글은 없습니다.")
		);

		// QUERY 2: SELECT, EXITING SNS TAG LIST
		List<SnsTag> existingSnsTagEntityList = snsTagRepository.findAllByTagNameIn(tagNameList);

		List<String> newTagNameList = tagNameList.stream()
			.filter(tagName -> !existingSnsTagEntityList.stream().map(SnsTag::getTagName).toList().contains(tagName))
			.toList();

		// QUERY 3: BULK INSERT, NEW TAG LIST
		List<SnsTag> newSnsTagEntityList = newTagNameList.stream().map(tagName -> SnsTag.builder()
			.tagName(tagName)
			.build()).toList();

		// snsTagRepository.saveAll(newSnsTagEntityList);
		snsTagJdbcRepository.saveAll(newSnsTagEntityList);

		List<SnsTag> snsRepostTagEntityList = new ArrayList<>();
		snsRepostTagEntityList.addAll(existingSnsTagEntityList);
		snsRepostTagEntityList.addAll(newSnsTagEntityList);

		// QUERY 5: SELECT, SNS POST USER REACTION
		SnsPostUserReaction snsPostUserReaction = snsPostUserReactionRepository.findBySnsPostAndSnsUser(snsPostId,
			userId).orElse(
			SnsPostUserReaction.builder()
				.snsPost(snsOriginPost)
				.snsUser(snsUser)
				.build()
		);

		if (snsPostUserReaction.getIsReposted()) {
			throw new Exception();
		}

		// QUERY 6: INSERT OR UPDATE, SNS POST USER REACTION
		snsPostUserReaction.setIsReposted(true);
		snsPostUserReaction.setIsRepostedAt(LocalDateTime.now());

		snsPostUserReactionRepository.save(snsPostUserReaction);

		// QUERY 4: INSERT, NEW POST
		SnsPost snsReNewPost = SnsPost.builder()
			.isRepost(true)
			.repostOrigin(snsOriginPost)
			.snsPostContents(snsOriginPost.getSnsPostContents())
			.snsUser(snsUser)
			.postTitle(snsOriginPost.getPostTitle())
			.postTitle(snsOriginPost.getPostTitle())
			.postBodyText(snsOriginPost.getPostBodyText())
			.latitude(snsRepostCreateReq.getLatitude())
			.longitude(snsRepostCreateReq.getLongitude())
			.tags(snsRepostTagEntityList.stream().map(snsTag -> PostTag.builder()
				.tagId(snsTag.getId())
				.tagName(snsTag.getTagName())
				.build()).toList())
			.build();
		snsPostJdbcRepository.insertRepost(snsReNewPost);

		// QUERY 7: BULK INSERT, NEW SNS TAG POST LIST
		snsTagPostJdbcRepository.saveAll(snsRepostTagEntityList.stream().map(snsTag -> SnsTagPost.builder()
			.snsTag(snsTag)
			.snsPost(snsReNewPost)
			.build()).toList());

		// QUERY 8: reaction_count +1
		// snsOriginPost.setReactionCount(snsOriginPost.getReactionCount() + 1);

		return new SnsPostCreateRsp(snsPostUserReaction.getIsReposted(),
			convertToPostGetRspByPostCreated(snsReNewPost));
	}

	@Transactional
	public SnsPostCommentRsp savePostComment(Long snsPostId, Long snsUserId,
		SnsPostCmntCreateReq snsPostCmntCreateReq, MultipartFile file) {

		if (file == null && !StringValidUtil.isNotBlank(snsPostCmntCreateReq.getPostCommentMsg())){
			throw new BadRequestErrorException("문자 또는 이미지를 하나 이상 보내야 됩니다.");
		}


		SnsUser snsUser = snsUserRepository.findById(snsUserId).orElseThrow(
			() -> new BadRequestErrorException("해당 계정은 없습니다.")
		);

		SnsPost snsPost = snsPostRepository.findByIdByNotBlocked(snsPostId, snsUserId)
			.orElseThrow(() -> new BadRequestErrorException("비공개 게시물에 댓글을 달 수 없습니다."));

		SnsPostCommentReaction snsPostCmntReaction = SnsPostCommentReaction.builder()
			.commentUser(snsUser)
			.commentMsg(snsPostCmntCreateReq.getPostCommentMsg())
			.isSource(true)
			.snsPost(snsPost)
			.build();

		SnsPostCommentReaction newSnsPostCommentReaction = saveCmntReaction(file, snsPostCmntReaction);


		// 알릴
		if (!snsUserId.equals(snsPost.getSnsUser().getId())) {
			notificationService.processPostCommentNotification(snsPost, newSnsPostCommentReaction);
		}

		snsPost.setReactionCount((snsPost.getReactionCount() != null ? snsPost.getReactionCount() : 0) + PostConst.POST_REACTION_COMMENT_SCORE);

		return convertToPostCommentGetRsp(newSnsPostCommentReaction);
	}

	@Transactional
	public SnsPostCommentRsp savePostCommentReply(Long snsPostId, Long snsUserId, Long snsPostCommentId,
		SnsPostCmntCreateReq snsPostCmntCreateReq, Boolean isThread, MultipartFile file) {
		SnsUser snsUser = snsUserRepository.findById(snsUserId).orElseThrow(
			() -> new BadRequestErrorException("해당 계정은 없습니다.")
		);

		SnsPost snsPost = SnsPost.builder().id(snsPostId).build();
		SnsPostCommentReaction sourceSnsPostCommentReaction = snsPostCommentReactionRepository.findByIdAndDeletedAtIsNull(snsPostCommentId).orElseThrow(
			() -> new BadRequestErrorException("해당 댓글은 없습니다.")
		);

		SnsPostCommentReaction snsPostCmntReaction = SnsPostCommentReaction.builder()
			.commentUser(snsUser)
			.sourceComment(sourceSnsPostCommentReaction)
			.isSource(false)
			.commentMsg(snsPostCmntCreateReq.getPostCommentMsg())
			.snsPost(snsPost)
			.build();
		saveCmntReaction(file, snsPostCmntReaction);

		snsPost.setReactionCount((snsPost.getReactionCount() != null ? snsPost.getReactionCount() : 0) + PostConst.POST_REACTION_REPLY_SCORE);

		return convertToPostReplyByCommentRsp(snsPostCmntReaction, isThread);
	}

	private SnsPostCommentReaction saveCmntReaction(MultipartFile file, SnsPostCommentReaction snsPostCmntReaction) {
		if (file != null){
			String contentType = file.getContentType();
			String originalFilename = file.getOriginalFilename();

			// 일단 이미지만 저장할 수 잏게
			String extension = FilenameUtils.getExtension(originalFilename);

			boolean isImage = UploadFileValidationUtils.isImage(contentType);


			if (!isImage){
				throw new BadRequestErrorException("업로드 파일 유형이 아닙니다.");
			}

			snsPostCmntReaction.setCommentMediaType(
				PostCommentMediaType.IMAGE
			);



			String contentUrl = r2CloudService.getPostCommentImageContentUrlByR2(UUID.randomUUID() + MediaConfigConst.IMAGE_JPEG_FORMAT);

			snsPostCmntReaction.setCommentMediaContent(r2CloudService.getPublicContentUrlByR2(contentUrl));

			// 파일 저장
			r2CloudService.uploadImageToR2(file, contentUrl);
		}

		return snsPostCommentReactionRepository.save(snsPostCmntReaction);
	}

	@Transactional
	public PostNotInterestedRsp putPostInterested(Long postId, Long userId, Boolean isShown) {
		SnsPostUserReaction snsPostUserReaction = snsPostUserReactionRepository.findBySnsPostAndSnsUser(postId,
			userId).orElse(
			SnsPostUserReaction.builder()
				.snsPost(SnsPost.builder().id(postId).build())
				.snsUser(SnsUser.builder().id(userId).build())
				.build()
		);
		snsPostUserReaction.setIsShown(isShown);
		snsPostUserReaction.setNotShownAt(LocalDateTime.now());
		snsPostUserReactionRepository.save(snsPostUserReaction);

		return PostNotInterestedRsp.builder()
			.postId(postId.toString())
			.isInterested(isShown)
			.build();
	}

	@Transactional
	public Boolean deletePostBySnsPostId(Long snsPostId, Long snsUserId) {
		SnsPost mySnsPost = snsPostRepository.findByIdAndSnsUser_Id(snsPostId, snsUserId)
			.orElseThrow(
				() -> new BadRequestErrorException("해당 게시글은 없습니다.")
			);

		// @ANSWER: 진짜 삭제하진 말고 deleted_at으로 관리
		snsTagPostRepository.deleteAllBySnsPostId(mySnsPost);
		mySnsPost.setDeletedAt(LocalDateTime.now());
		snsPostRepository.save(mySnsPost);
		// snsPostUserReactionRepository.deleteBySnsPostAndSnsUser_Id(mySnsPost, snsUserId);
		// snsPostRepository.delete(mySnsPost);

		return true;
	}

	@Transactional
	public void deletePostBySnsPostIdByAdmin(SnsPost snsPost) {

		// @ANSWER: 진짜 삭제하진 말고 deleted_at으로 관리
		snsTagPostRepository.deleteAllBySnsPostId(snsPost);
		snsPost.setDeletedAt(LocalDateTime.now());
		snsPostRepository.save(snsPost);
	}

	@Transactional
	public void recoverPostBySnsPostIdByAdmin(SnsPost snsPost) {

		List<PostTag> postTagList = snsPost.getTags();

		if (!snsTagPostRepository.findBySnsPost_Id(snsPost.getId()).isEmpty()){
			snsTagPostRepository.deleteAllBySnsPostId(snsPost);
		}

		if (!postTagList.isEmpty()){
			List<SnsTagPost> snsTagPostList = postTagList.stream().map((postTag ->
				SnsTagPost.builder()
					.snsPost(snsPost)
					.snsTag(SnsTag.builder().id(postTag.getTagId()).build())
					.build()))
					.toList();
			snsTagPostJdbcRepository.saveAll(snsTagPostList);
		}

		// deleted_at => null
		snsPost.setDeletedAt(null);
		snsPostRepository.save(snsPost);

	}

	@Transactional
	public DeleteCommentRsp deletePostComment(Long snsCommentId, Long snsUserId) {
		SnsPostCommentReaction snsPostCommentReaction = snsPostCommentReactionRepository
			.findBySnsPostCommentReactionIdAndCommentUser_SnsUserId(snsCommentId, snsUserId).orElseThrow(
				() -> new BadRequestErrorException("해당 계정은 없습니다.")
			);

		// @ANSWER: 삭제하지 말고, deletedAt 적용
		// snsPostCommentReactionRepository.delete(snsPostCommentReaction);

		snsPostCommentReaction.setDeletedAt(LocalDateTime.now());

		return DeleteCommentRsp.builder()
			.postId(snsPostCommentReaction.getSnsPost().getId().toString())
			.commentId(snsCommentId.toString())
			.isDeleted(true)
			.build();
	}

	@Transactional
	public SnsPostCommentRsp updatePostComment(Long snsCommentId, Long snsUserId,
		SnsPostCmntUpdateReq snsPostCmntUpdateReq) {
		SnsPostCommentReaction snsPostCommentReaction = snsPostCommentReactionRepository
			.findBySnsPostCommentReactionIdAndCommentUser_SnsUserId(snsCommentId, snsUserId).orElseThrow(
				() -> new BadRequestErrorException("해당 계정은 없습니다.")
			);
		snsPostCommentReaction.setCommentMsg(snsPostCmntUpdateReq.getPostCommentMsg());
		snsPostCommentReaction.setCommentMediaType(PostCommentMediaType.valueOf(snsPostCmntUpdateReq.getPostCommentMediaType()));
		snsPostCommentReaction.setCommentMediaContent(snsPostCmntUpdateReq.getPostCommentMediaContent());
		snsPostCommentReaction.setLastUpdatedAt(LocalDateTime.now());
		SnsPostCommentReaction snsPostUpdateCommetReaction = snsPostCommentReactionRepository.save(
			snsPostCommentReaction);

		return convertToPostCommentGetRsp(snsPostUpdateCommetReaction);
	}

	@Transactional
	public SnsPostLikePutRsp modifyPostCommentLike(Long postId, Long snsCommentId, Long snsUserId) {
		SnsPost snsPost = snsPostRepository.findById(postId).orElseThrow(
			() -> new BadRequestErrorException("해당 게시물은 없습니다.")
		);
		SnsPostCommentReaction snsPostCommentReaction = snsPostCommentReactionRepository.findByIdAndDeletedAtIsNull(snsCommentId).orElseThrow(
			() -> new BadRequestErrorException("해당 댓글은 없습니다.")
		);

		SnsPostCommentLike snsPostCommentLike = snsPostCommentLikeRepository.findBySnsPostCommentReaction_IdAndSnsUser_Id(
			snsCommentId, snsUserId).orElse(
			SnsPostCommentLike.builder()
				.snsPost(snsPost)
				.snsPostCommentReaction(snsPostCommentReaction)
				.snsUser(SnsUser.builder().id(snsUserId).build())
				.isLiked(false)
				.build()
		);
		snsPostCommentLike.setIsLiked(!snsPostCommentLike.getIsLiked());

		if (snsPostCommentLike.getIsLiked()){
			snsPostCommentLike.setIsLikedAt(LocalDateTime.now());
		}
		int score = PostConst.POST_REACTION_REPLY_HEART_SCORE;
		snsPost.setReactionCount(
			snsPostCommentLike.getIsLiked()
				? (snsPost.getReactionCount() != null ? snsPost.getReactionCount() : 0) + score
				: (snsPost.getReactionCount() != null ? snsPost.getReactionCount() -score : 0));

		snsPostCommentLikeRepository.save(snsPostCommentLike);

		return new SnsPostLikePutRsp(snsPostCommentLike.getIsLiked());
	}

	public List<GetPostImageDocResourceRsp> getHtmlImageListParser(String url) {
		Map<String, GetPostImageDocResourceRsp> imageMap = new HashMap<>();

		try {
			// 1. 웹 페이지를 가져와서 HTML을 파싱
			Document doc = Jsoup.connect(url).get();

			// 2. <img> 태그를 선택
			Elements imgElements = doc.select("img");

			// 3. 각 <img> 태그의 src 속성 값 추출 (SVG 필터링)
			for (var imgElement : imgElements) {
				String imgSrc = imgElement.attr("abs:src"); // 절대 경로로 변환된 이미지 src
				if (!imgSrc.startsWith("data:")) {
					imageMap.put(imgSrc, GetPostImageDocResourceRsp.builder()
						.contentUrl(imgSrc)
						.contentType(PostContentType.IMAGE.toString())
						.build());
				}
				// if (!imgSrc.endsWith(".svg") && !imgSrc.endsWith(".gif")) { // SVG 및 GIF 파일이 아닌 경우에만 추가
				// 	imageMap.put(imgSrc, GetPostImageDocResourceRsp.builder()
				// 		.contentUrl(imgSrc)
				// 		.contentType(PostContentType.IMAGE.toString())
				// 		.build());
				// }
			}

		} catch (UnsupportedMimeTypeException e) {
			try {
				URL imageUrl = new URL(url);
				HttpURLConnection connection = (HttpURLConnection)imageUrl.openConnection();
				connection.setRequestMethod("GET");
				connection.getInputStream();

				String contentType = connection.getContentType();

				if (contentType.startsWith("image/")) {
					return Collections.singletonList(GetPostImageDocResourceRsp.builder()
						.contentUrl(url)
						.contentType(PostContentType.IMAGE.toString())
						.build());
				} else {
					return new ArrayList<>();
				}

			} catch (Exception ex) {
				throw new BadRequestErrorException("유효하지 않은 url 입니다.");
			}

		} catch (Exception e) {
			throw new BadRequestErrorException("유효하지 않은 url 입니다.");
		}

		return new ArrayList<>(imageMap.values());
	}

	// 위치 정보를 받아서, 가장 가까운 순으로 보여주도록
	@Transactional
	public List<SnsPostRsp> getMapPostRelation(
		Long snsUserId,
		String srchQry,
		Integer page,
		Float latitude,
		Float longitude,
		LocalDateTime startDate,
		LocalDateTime endDate
	) {

		LocalDateTime startDateTime = startDate != null ? startDate : LocalDateTime.of(1001,1,1,0,0,0);
		LocalDateTime endDateTime = endDate != null ? endDate : LocalDateTime.now();

		return snsPostRepository.findAllMapPostRelationBySearchQueryByPostGis(snsUserId, srchQry,
				page * PageConfigConst.DEFAULT_PAGE_SIZE, PageConfigConst.DEFAULT_PAGE_SIZE, latitude, longitude, startDateTime, endDateTime)
			.stream()
			.map(postProfileFacadeService::convertToPostGetRsp)
			.toList();
	}

	@Transactional
	public Boolean composePost (
		SnsPostComposeCreateReq snsPostComposeCreateReq,
		List<MultipartFile> files,
		Long userId
	) {
		try {
			composePostProcess(
				new SnsPost(),
				snsPostComposeCreateReq.getTitle(),
				snsPostComposeCreateReq.getBodyText(),
				snsPostComposeCreateReq.getAddress(),
				snsPostComposeCreateReq.getBuildName(),
				snsPostComposeCreateReq.getLatitude(),
				snsPostComposeCreateReq.getLongitude(),
				snsPostComposeCreateReq.getTargetAudienceValue(),
				snsPostComposeCreateReq.getExternalImgLinkList(),
				snsPostComposeCreateReq.getScrapIdList(),
				snsPostComposeCreateReq.getTagList(),
				files, userId, false,
				List.of(),
				null,
				false
			).get();
			return true;
		} catch (ExecutionException | InterruptedException e) {
			throw new RuntimeException(e);
		}
	}

	// @ANSWER: cloudflare 랑 minio랑 구분 되도록 구현됨
	@Transactional
	public Boolean editPost (
		Long snsPostId,
		SnsPostComposeUpdateReq snsPostComposeUpdateReq,
		List<MultipartFile> files,
		Long userId
	) {
		SnsPost snsPost = snsPostRepository.findById(snsPostId).orElseThrow(
			() -> new BadRequestErrorException("해당 게시물은 없습니다.")
		);

		// @ANSWER: 굳이 폴더를 이동할 필요가 있을 까?
		// snsPost.getSnsPostContents().forEach((snsPostContent -> {
		// 	if ( snsPostComposeUpdateReq.getExternalImgLinkList().contains(snsPostContent.getContent())){
		// 		String oldFileUrl = snsPostContent.getContent();
		// 		r2CloudService.renameImageInR2(oldFileUrl, String.format("delete/posts/%d/%s",snsPost.getId(),oldFileUrl));
		// 	}
		// }));

		List<SnsPostContent> registerPostContentList = new ArrayList<>();
		snsPost.getSnsPostContents().forEach((snsPostContent -> {
			if ( snsPostComposeUpdateReq.getExistPostContentList().contains(Objects.requireNonNullElse(snsPostContent.getBucketUrl(),"") + Objects.requireNonNullElse(snsPostContent.getContent(),""))){
				registerPostContentList.add(snsPostContent);
			}
		}));

		try {
			composePostProcess(
				snsPost,
				snsPostComposeUpdateReq.getTitle(),
				snsPostComposeUpdateReq.getBodyText(),
				snsPostComposeUpdateReq.getAddress(),
				snsPostComposeUpdateReq.getBuildName(),
				snsPostComposeUpdateReq.getLatitude(),
				snsPostComposeUpdateReq.getLongitude(),
				snsPostComposeUpdateReq.getTargetAudienceValue(),
				snsPostComposeUpdateReq.getExternalImgLinkList(),
				snsPostComposeUpdateReq.getScrapIdList(),
				snsPostComposeUpdateReq.getTagList(),
				files, userId, true,
				registerPostContentList,
				null,
				false
			).get();

			return true;
		} catch (ExecutionException | InterruptedException e) {
			throw new RuntimeException(e);
		}
	}


	@Async
	@Transactional(rollbackOn = Exception.class)
	public CompletableFuture<Void> composePostProcess(
		SnsPost snsPost,
		String title,
		String bodyText,
		String address,
		String buildName,
		Float latitude,
		Float longitude,
		Integer targetAudienceValue,
		List<String> externalImgLinkList,
		List<String> scrapIdList,
		List<String> tagList,
		List<MultipartFile> multipartFileList_,
		Long userId,
		Boolean isEdit,
		List<SnsPostContent> existPostContentList,
		LocalDateTime createdAt,
		Boolean isApiRequestAddressToGis
	) {
		List<MultipartFile> multipartFileList = multipartFileList_ != null ? new ArrayList<>(multipartFileList_) : new ArrayList<>();

		// 업로드 수가 타당한지? : 기존에 있던 파일, 업로드 파일, 링크 파일


		isValidUploadNum(existPostContentList.size() + multipartFileList.size() + externalImgLinkList.size());

		List<SnsPostContent> snsPostContentList = new ArrayList<>();

		// content url 생성 및 추가 추가
		addContentUrls(multipartFileList, externalImgLinkList, snsPostContentList, existPostContentList);

		SnsUser snsUser = SnsUser.builder().id(userId).build();

		// 태그 생성
		List<SnsTag> snsPostTagEntityList = createPostTagRelation(tagList, snsPostContentList);

		// 링크로 업로드 된 이미지나 비디오
		// QUERY 4: INSERT, NEW POST
		SnsPost _snsPost = updateUploadSnsPost(
			snsPost, title,bodyText, targetAudienceValue, address, buildName, latitude, longitude, snsUser, snsPostContentList, snsPostTagEntityList, createdAt, isApiRequestAddressToGis);

		try {
			if (isEdit) {
				snsTagPostJdbcRepository.deleteByPostId(_snsPost.getId());
				// snsPostJdbcRepository.updatePost(_snsPost);
				snsPostRepository.save(_snsPost);
			}
			else {
				snsPostJdbcRepository.insertPost(_snsPost);
				// QUERY 5: INSERT SNS POST USER REACTION
				// SnsPostUserReaction snsPostUserReaction = SnsPostUserReaction.builder()
				// 	.snsPost(_snsPost)
				// 	.snsUser(snsUser)
				// 	.build();
				// snsPostUserReactionRepository.save(snsPostUserReaction);
			}

			if (scrapIdList != null){
				if (scrapIdList.isEmpty()){
					snsScrapRepository.deleteAllByPostId(_snsPost.getId());
					Optional<SnsPostUserReaction> snsPostUserReactionOpt = snsPostUserReactionRepository.findBySnsPostAndSnsUser(
						_snsPost.getId(), userId);
					if (snsPostUserReactionOpt.isPresent()){
						SnsPostUserReaction snsPostUserReaction = snsPostUserReactionOpt.get();
						snsPostUserReaction.setIsClipped(false);
						snsPostUserReactionRepository.save(snsPostUserReaction);
					}
				}
				else{
					postProfileFacadeService.createPostToScrapList(userId,_snsPost.getId(), scrapIdList, isEdit);
				}
			}

			// QUERY 6: BULK INSERT, NEW SNS TAG POST LIST
			snsTagPostJdbcRepository.saveAll(snsPostTagEntityList.stream().map(snsTag -> SnsTagPost.builder()
				.snsTag(snsTag)
				.snsPost(_snsPost)
				.build()).toList());

		}
		catch (JsonProcessingException e) {
			throw new BadRequestErrorException("Json 객체 변환 과정에서 오류가 났습니다: " + e);
		}
		catch (Exception e){
			log.error(e.getMessage());
			throw new InternalServerErrorException("오류로 인해, 게시글 생성이 되지 않았습니다.");
		}

		if (!multipartFileList.isEmpty()){
			try {
				int initFileIndex = existPostContentList.size();

				for (int i = 0; i < multipartFileList.size(); i++) {

					SnsPostContent _snsPostContent = snsPostContentList.get(i + initFileIndex);
					String contentType = multipartFileList.get(i).getContentType();

					// 이미지 인 경우 => cloudflare r2에 저장
					if(UploadFileValidationUtils.isImage(contentType)){
						r2CloudService.uploadImageToR2(
							multipartFileList.get(i),
							_snsPostContent.getContent());
					}
					// 비디오인 경우 => minio에 저장
					else{
						// 비디오 파일
						// File videoTempFile = File.createTempFile(MediaConfigConst.UPLOAD_TEMP_FILE_PREFIX_NAME,
						// 	files.get(i).getOriginalFilename());
						// files.get(i).transferTo(videoTempFile);
						//
						// String tempFolderName = UUID.randomUUID().toString();
						// Path tempDir = Files.createTempDirectory(tempFolderName);
						// Path outputTempAbsoluteDirPath = tempDir.toAbsolutePath();
						// String outputTempAbsoluteDirPathString = outputTempAbsoluteDirPath.toString();
						// File outputDirFile = outputTempAbsoluteDirPath.toFile();
						// ffmpegProcessingService.convertToHLS(videoTempFile, outputDirFile, minioCloudService.m3u8FileName);
						// minioCloudService.uploadHLSToMinio(outputDirFile, minioCloudService.getBucketKeyContentUrl(snsPostContentList.get(i).getContent()));
						//
						// // poster 이미지
						// File posterImgFile = ffmpegProcessingService.generateVideoPoster(videoTempFile, "output.jpg");
						// minioCloudService.uploadImageJpegToMinio(posterImgFile, minioCloudService.getBucketKeyContentUrl(snsPostContentList.get(i).getPreviewImg()));
						//
						// // 즉시 제거
						// videoTempFile.delete();
						// outputDirFile.delete();
						// tempDir.toFile().delete();
						// posterImgFile.delete();

						File videoTempFile = File.createTempFile(
							MediaConfigConst.UPLOAD_TEMP_FILE_PREFIX_NAME + UUID.randomUUID() + "-",
							multipartFileList.get(i).getOriginalFilename());
						multipartFileList.get(i).transferTo(videoTempFile);


						String posterExtension = FileUtils.getExtension(_snsPostContent.getPreviewImg());
						String outputTempName = UUID.randomUUID() + "." + posterExtension;

						// poster 이미지 생성및 업로드
						File posterImgFile = ffmpegProcessingService.generateVideoPoster(videoTempFile, outputTempName);
						minioCloudService.uploadImageJpegToMinio(posterImgFile, minioCloudService.getBucketKeyContentUrl(
							_snsPostContent.getPreviewImg()));
						posterImgFile.delete();

						// RabbitMQ에 등록
						videoConversationProducer.sendVideoConversionUploadToQueue(
							_snsPost.getId(),
							videoTempFile.getAbsolutePath(),
							_snsPostContent
						);
					}
				}
			} catch (BaseException e) {
				throw e;
			} catch (Exception e) {
				log.error(e.getMessage());
				throw new InternalServerErrorException("서버 오류로 업로드 실패", e);
			}
		}
		return CompletableFuture.completedFuture(null);
	}

	@Transactional
	public Boolean createPostReport(Long snsPostId, Long snsUserId, SnsPostReportCreateReq snsPostReportCreateReq){
		SnsPost snsPost = snsPostRepository.findById(snsPostId).orElseThrow(
			() -> new BadRequestErrorException("해당 게시물은 없습니다.")
		);

		SnsPostReport snsPostReport = SnsPostReport.builder()
			.snsPost(snsPost)
			.reporterUser(SnsUser.builder().id(snsUserId).build())
			.reportedUser(snsPost.getSnsUser())
			.postReportStatus(PostReportStatus.PENDING)
			.build();

		registerReportType(snsPostReport, snsPostReportCreateReq);

		snsPostReportRepository.save(snsPostReport);
		return true;
	}

	@Transactional
	public Boolean createPostCommentReport(
		Long snsPostId, Long snsPostCommentId, Long snsUserId,
		SnsPostReportCreateReq snsPostReportCreateReq){
		SnsPost snsPost = snsPostRepository.findById(snsPostId).orElseThrow(
			() -> new BadRequestErrorException("해당 게시물은 없습니다.")
		);
		SnsPostCommentReaction snsPostCommentReaction = snsPostCommentReactionRepository.findByIdAndDeletedAtIsNull(snsPostCommentId).orElseThrow(
			()-> new BadRequestErrorException("해당 댓글은 없습니다.")
		);
		SnsPostReport snsPostReport = SnsPostReport.builder()
			.snsPost(snsPost)
			.snsPostCommentReaction(snsPostCommentReaction)
			.reporterUser(SnsUser.builder().id(snsUserId).build())
			.reportedUser(snsPost.getSnsUser())
			.postReportStatus(PostReportStatus.PENDING)
			.build();

		registerReportType(snsPostReport, snsPostReportCreateReq);

		snsPostReportRepository.save(snsPostReport);
		return true;
	}

	// public List<SnsPostRsp> getPostGetRspList(List<SnsPostDao> snsPostDaoList) {
	// 	return snsPostDaoList.stream().map((postProfileFacadeService::convertToPostGetRsp)).toList();
	// }

	private List<SnsPostFollowsGetRsp> getPostLikeList(List<PostLikeDao> postLikeDaoList) {
		return postLikeDaoList.stream().map((this::convertToPostLikeGetRsp)).toList();
	}

	private List<SnsPostFollowsGetRsp> getPostIsRepostedList(List<PostIsRepostedDao> postIsRepostedDaoList) {
		return postIsRepostedDaoList.stream().map((this::convertToPostIsRepostedGetRsp)).toList();
	}

	private SnsPostRsp convertToPostGetRspByPostCreated(SnsPost snsPost) {
		return SnsPostRsp.builder()
			.postId(snsPost.getId().toString())
			.userId(snsPost.getSnsUser().getId().toString())
			.profilePath(snsPost.getSnsUser().getProfilePath())
			.location(new Location(snsPost.getLatitude(), snsPost.getLongitude(), snsPost.getAddress(), snsPost.getBuildName()))
			.tags(snsPost.getTags().stream().map((PostTag::getTagName)).toList())
			.isFollowed(false)
			.followable(false)
			.isLiked(false)
			.isClipped(false)
			.postTitle(snsPost.getPostTitle())
			.postBodyText(snsPost.getPostBodyText())
			// @ANSWER: 제거
			// .postCategory(snsPost.getPostCategory().toString())
			.postContents(snsPost.getSnsPostContents()
				.stream()
				.map((snsPostContent -> new PostContent(snsPostContent.getPostContentType(),
					snsPostContent.getContent(),
					snsPostContent.getAscSortNum(),
					snsPostContent.getPreviewImg(),
					snsPostContent.getIsUploaded(),
					snsPostContent.getVideoDuration()
					)))
				.toList())
			.postedAt(snsPost.getCreatedAt())
			.build();
	}

	// private SnsPostRsp convertToPostGetRsp(SnsPostDao snsPostDao) {
	// 	return SnsPostRsp.builder()
	// 		.postId(snsPostDao.getPostId().toString())
	// 		.userId(snsPostDao.getSnsUserId().toString())
	// 		.username(snsPostDao.getUsername())
	// 		.profilePath(snsPostDao.getProfilePath())
	// 		.location(new Location(snsPostDao.getLatitude(), snsPostDao.getLongitude(), snsPostDao.getAddress()))
	// 		.tags(snsPostDao.getStringToTags().stream().map(PostTagDao::getTagName).toList())
	// 		.isFollowed(snsPostDao.getFollowingId() != null)
	// 		.isLiked(snsPostDao.getIsLiked())
	// 		.isClipped(snsPostDao.getIsClipped())
	// 		.isReposted(snsPostDao.getIsReposted())
	// 		.postTitle(snsPostDao.getPostTitle())
	// 		.postBodyText(snsPostDao.getPostBodyText())
	// 		// .postCategory(snsPostDao.getPostCategory())
	// 		.followable(snsPostDao.getFollowable())
	// 		.postContents(snsPostDao.getStringToSnsPostContents()
	// 			.stream()
	// 			.map((snsPostContent -> new PostContent(snsPostContent.getPostContentType(),
	// 				Objects.requireNonNullElse(snsPostContent.getBucketUrl(),"") + Objects.requireNonNullElse(snsPostContent.getContent(),""),
	// 				snsPostContent.getAscSortNum(),
	// 				Objects.requireNonNullElse(snsPostContent.getBucketUrl(),"") + Objects.requireNonNullElse(snsPostContent.getPreviewImg(),""),
	// 				snsPostContent.getIsUploaded(),
	// 				snsPostContent.getVideoDuration()
	// 			)))
	// 			.toList())
	// 		.postedAt(snsPostDao.getPostedAt())
	// 		.build();
	// }

	private SnsPostCommentRsp convertToPostCommentGetRsp(PostCommentDao postCommentDao) {
		return SnsPostCommentRsp.builder()
			.postCommentId(postCommentDao.getPostCommentId().toString())
			.commentUserId(postCommentDao.getCommentUserId().toString())
			.username(postCommentDao.getUsername())
			.profilePath(postCommentDao.getProfilePath())
			.postCommentMsg(postCommentDao.getCommentMsg())
			.commentMediaType(postCommentDao.getPostCommentMediaType() != null ? postCommentDao.getPostCommentMediaType().toString() : null)
			.commentMediaContent(postCommentDao.getPostCommentMediaContent())
			.commentCount(postCommentDao.getCommentCount())
			.likeCount(postCommentDao.getLikeCount())
			.postedAt(postCommentDao.getPostedAt())
			.isLiked(postCommentDao.getIsLiked())
			.isReplyMsg(!postCommentDao.getIsSource())
			.replyTargetCommentId(
				postCommentDao.getCommentSourceId() != null ? postCommentDao.getCommentSourceId().toString() :
					null)
			.build();
	}

	private SnsPostCommentRsp convertToPostCommentGetRspByReply(PostCommentDao postCommentDao, Boolean isReply) {
		return SnsPostCommentRsp.builder()
			.postCommentId(postCommentDao.getPostCommentId().toString())
			.commentUserId(postCommentDao.getCommentUserId().toString())
			.username(postCommentDao.getUsername())
			.profilePath(postCommentDao.getProfilePath())
			.postCommentMsg(postCommentDao.getCommentMsg())
			.commentMediaType(postCommentDao.getPostCommentMediaType() != null ? postCommentDao.getPostCommentMediaType().toString() : null)
			.commentMediaContent(postCommentDao.getPostCommentMediaContent())
			.commentCount(postCommentDao.getCommentCount())
			.likeCount(postCommentDao.getLikeCount())
			.postedAt(postCommentDao.getPostedAt())
			.isLiked(postCommentDao.getIsLiked())
			.isReplyMsg(isReply)
			.replyTargetCommentId(
				isReply ? postCommentDao.getCommentSourceId().toString() :
					null)
			.build();
	}

	private SnsPostCommentRsp convertToPostReplyByCommentRsp(SnsPostCommentReaction snsPostCommentReaction,
		Boolean isThread) {
		SnsUser snsUser = snsPostCommentReaction.getCommentUser();
		return SnsPostCommentRsp.builder()
			.postCommentId(snsPostCommentReaction.getId().toString())
			.commentUserId(snsUser.getId().toString())
			.username(snsUser.getUsername())
			.profilePath(snsUser.getProfilePath())
			.postCommentMsg(snsPostCommentReaction.getCommentMsg())
			.commentMediaType(snsPostCommentReaction.getCommentMediaType() != null ? snsPostCommentReaction.getCommentMediaType().toString() : null)
			.commentMediaContent(snsPostCommentReaction.getCommentMediaContent())
			.commentCount(0)
			.likeCount(0)
			.postedAt(snsPostCommentReaction.getCreatedAt())
			.isLiked(false)
			.isReplyMsg(!isThread && !snsPostCommentReaction.getIsSource())
			.replyTargetCommentId(
				isThread ? null : (
					snsPostCommentReaction.getSourceComment() != null ? snsPostCommentReaction.getSourceComment()
						.getId().toString() : null))
			.build();
	}

	private SnsPostCommentRsp convertToPostCommentGetRsp(SnsPostCommentReaction snsPostCommentReaction) {
		SnsUser snsUser = snsPostCommentReaction.getCommentUser();
		return SnsPostCommentRsp.builder()
			.postCommentId(snsPostCommentReaction.getId().toString())
			.commentUserId(snsUser.getId().toString())
			.username(snsUser.getUsername())
			.profilePath(snsUser.getProfilePath())
			.postCommentMsg(snsPostCommentReaction.getCommentMsg())
			.commentMediaType(snsPostCommentReaction.getCommentMediaType() != null ? snsPostCommentReaction.getCommentMediaType().toString() : null)
			.commentMediaContent(snsPostCommentReaction.getCommentMediaContent())
			.commentCount(0)
			.likeCount(0)
			.postedAt(snsPostCommentReaction.getCreatedAt())
			.isLiked(false)
			.isReplyMsg(!snsPostCommentReaction.getIsSource())
			.replyTargetCommentId(
				snsPostCommentReaction.getSourceComment() != null ? snsPostCommentReaction.getSourceComment()
					.getId().toString() : null)
			.build();
	}

	private SnsPostFollowsGetRsp convertToPostLikeGetRsp(PostLikeDao postLikeDao) {
		return SnsPostFollowsGetRsp.builder()
			.userId(postLikeDao.getUserId().toString())
			.username(postLikeDao.getUsername())
			.profilePath(postLikeDao.getProfilePath())
			.nickname(postLikeDao.getNickname())
			.isFollowed(postLikeDao.getIsFollowed())
			.isMe(postLikeDao.getIsMe())
			.build();
	}

	private SnsPostFollowsGetRsp convertToPostIsRepostedGetRsp(PostIsRepostedDao postIsRepostedDao) {
		return SnsPostFollowsGetRsp.builder()
			.userId(postIsRepostedDao.getUserId().toString())
			.username(postIsRepostedDao.getUsername())
			.profilePath(postIsRepostedDao.getProfilePath())
			.nickname(postIsRepostedDao.getNickname())
			.isFollowed(postIsRepostedDao.getIsFollowed())
			.isMe(postIsRepostedDao.getIsMe())
			.build();
	}

	private SnsPostInfoRsp convertToPostInfoRsp(SnsPostInfoDao snsPostInfoDao, List<ScrapBoardByPostInfoDao> scrapBoardIdList) {
		return SnsPostInfoRsp.builder()
			.postId(snsPostInfoDao.getPostId().toString())
			.userId(snsPostInfoDao.getSnsUserId().toString())
			.username(snsPostInfoDao.getUsername())
			.location(
				new Location(
					snsPostInfoDao.getLatitude(),
					snsPostInfoDao.getLongitude(),
					snsPostInfoDao.getAddress(),
					snsPostInfoDao.getBuildName()))
			.tags(snsPostInfoDao.getStringToTags().stream().map(PostTagDao::getTagName).toList())
			.postTitle(snsPostInfoDao.getPostTitle())
			.postBodyText(snsPostInfoDao.getPostBodyText())
			.postContents(snsPostInfoDao.getStringToSnsPostContents()
				.stream()
				.map((snsPostContent -> new PostContent(snsPostContent.getPostContentType(),
					Objects.requireNonNullElse(snsPostContent.getBucketUrl(),"") +  snsPostContent.getContent(),
					snsPostContent.getAscSortNum(),
					snsPostContent.getPreviewImg(),
					snsPostContent.getIsUploaded(),
					snsPostContent.getVideoDuration()
				)))
				.toList())
			.postedAt(snsPostInfoDao.getPostedAt())
			.targetAudTypeId(
				switch (snsPostInfoDao.getTgtAudType()) {
					case PUBLIC_SCOPE:
						yield TgtAudTypeValue.PUBLIC_SCOPE_ID_VALUE;
					case FOLLOWERS_SCOPE:
						yield TgtAudTypeValue.FOLLOWERS_SCOPE_ID_VALUE;
					case PRIVATE_SCOPE:
						yield TgtAudTypeValue.PRIVATE_SCOPE_ID_VALUE;
				}
			)
			.scrapBoardIdList(scrapBoardIdList.stream().map(scrapBoardByPostInfoDao -> new ScrapBoardInfo(
				scrapBoardByPostInfoDao.getScrapId().toString(),scrapBoardByPostInfoDao.getScrapName())).toList())
			.build();
	}


	private void isValidUploadNum (Integer uploadNum){
		if (uploadNum > MediaConfigConst.MAX_UPLOAD_FILE_NUM) {
			throw new BadRequestErrorException(
				String.format("업로드 파일은 쵀대 %d개까지 입니다.", MediaConfigConst.MAX_UPLOAD_FILE_NUM));
		}
		if (uploadNum <= 0){
			throw new BadRequestErrorException(
				"업로드 파일은 최소 1개 이상을 보내주셔야 됩니다.");
		}
	}

	private void addContentUrls (
		List<MultipartFile> multipartFileList,
		List<String> postContentLinkList,
		List<SnsPostContent> snsPostContentList,
		List<SnsPostContent> existPostContentList
	){

		AtomicInteger index = new AtomicInteger();

		// 기존 게시물 넣기
		existPostContentList.forEach(postContent -> {
			snsPostContentList.add(SnsPostContent.builder()
				.postContentType(postContent.getPostContentType())
				.content(postContent.getContent())
				.bucketUrl(postContent.getBucketUrl())
				.ascSortNum(index.incrementAndGet())
				.isLink(postContent.getIsLink())
				.fileType(postContent.getFileType())
				.isUploaded(postContent.getIsUploaded())
				.previewImg(postContent.getPreviewImg())
				.videoDuration(postContent.getVideoDuration())
				.build());

		});


		// 업로드 파일
		multipartFileList.forEach((multipartFile -> {
			// 이미지 또는 비디오 파일인지 확인
			String contentType = multipartFile.getContentType();

			boolean isImage = UploadFileValidationUtils.isImage(contentType);
			boolean isVideo = UploadFileValidationUtils.isVideo(contentType);
			boolean isValidType = isImage || isVideo;

			if(!isValidType){
				throw new BadRequestErrorException("업로드 파일 유형이 아닙니다.");
			}

			if((isImage && multipartFile.getSize() > imageFileSize) || (isVideo && multipartFile.getSize() > videoFileSize)){
				throw new BadRequestErrorException("파일 크기가 너무 큽니다.");
			}

			boolean isLink = false;
			SnsPostContent newSnsPostContent;
			// 이미지의 경우 cloudflare r2에 저장
			if (isImage){
				String contentUrl = r2CloudService.getPostImageContentUrlByR2(UUID.randomUUID() + MediaConfigConst.IMAGE_WEBP_FORMAT);
				String buckUrl = imageBucketPublicUrl;

				newSnsPostContent = SnsPostContent.builder()
					.postContentType(
						PostContentType.IMAGE
					)
					.content(contentUrl)
					.bucketUrl(buckUrl)
					.ascSortNum(index.incrementAndGet())
					.isLink(isLink)
					.fileType(MediaConfigConst.IMAGE_WEBP_TYPE)
					.isUploaded(true)
					.build();
			}
			// 영상의 경우 minio에 저장 ex) videos/
			else{
				String randomName = UUID.randomUUID().toString();

				// 출력 ex) 버킷 주소/비디오 폴더 경로/g3839md93589383-38392/output.m3u8
				String videoContentPath = minioCloudService.getHlsContentUrlByMinio(randomName + "/" + minioCloudService.m3u8FileName);
				// 출력 ex) 버킷 주소/포스터 폴더 경로/g3839md93589383-38392.jpg
				String posterPath = minioCloudService.getVideoPosterContentUrlByMinio(randomName + MediaConfigConst.IMAGE_JPEG_FORMAT);
				String buckUrl = videoBucketPublicUrl;
				newSnsPostContent = SnsPostContent.builder()
					.postContentType(
						PostContentType.VIDEO
					)
					.content(videoContentPath)
					.previewImg(posterPath)
					.bucketUrl(buckUrl)
					.fileType(MediaConfigConst.VIDEO_HLS_TYPE)
					.ascSortNum(index.incrementAndGet())
					.isLink(isLink)
					.isUploaded(false) // 비디오 처리는 다소 오래거리기 때문에 RabbitMQ로 처리
					.build();
			}
			snsPostContentList.add(newSnsPostContent);
		}));


		// 링크 파일 -> 이미지를 다운받아서 저장
		List<String> postContentList = new ArrayList<>(postContentLinkList);
		Collections.reverse(postContentList);

		postContentList.forEach((imgContentUrl -> {
			MultipartFile multipartFile = FileUtils.convertUrlToMultipartFile(imgContentUrl);

			if (multipartFile != null){
				multipartFileList.add(0, multipartFile);
				String contentUrl = r2CloudService.getPostImageContentUrlByR2(UUID.randomUUID() + MediaConfigConst.IMAGE_JPEG_FORMAT);
				String buckUrl = imageBucketPublicUrl;

				SnsPostContent snsPostContent = SnsPostContent.builder()
					.postContentType(
						PostContentType.IMAGE
					)
					.content(contentUrl)
					.bucketUrl(buckUrl)
					.ascSortNum(index.incrementAndGet())
					.isLink(true)
					.fileType(MediaConfigConst.IMAGE_JPEG_TYPE)
					.isUploaded(true)
					.build();

				snsPostContentList.add(0, snsPostContent);

				// @ANSWER: 이미지를 다운 받지 않고 링크로 저장하는 경우 -> 링크를 다운로드 하여 저장
				// snsPostContentList.add(SnsPostContent.builder()
				// 	.postContentType(postContent.getPostContentType())
				// 	.content(postContent.getContent())
				// 	.ascSortNum(index.incrementAndGet())
				// 	.isLink(true)
				// 	.build());
			}
		}));
	}

	private List<SnsTag> createPostTagRelation (List<String> tagList, List<SnsPostContent> snsPostContentList) {

		// QUERY 2: SELECT, EXITING SNS TAG LIST
		List<SnsTag> existingSnsTagEntityList = snsTagRepository.findAllByTagNameIn(tagList);

		List<String> newTagNameList = tagList.stream()
			.filter(tagName -> !existingSnsTagEntityList.stream().map(SnsTag::getTagName).toList().contains(tagName))
			.toList();

		Random random = new Random();

		// QUERY 3: BULK INSERT, NEW TAG LIST
		List<SnsTag> newSnsTagEntityList = newTagNameList.stream().map(tagName -> {
			int randomIndex = random.nextInt(snsPostContentList.size());
			SnsPostContent snsPostContent = snsPostContentList.get(randomIndex);

			String tagRepsBatchContent;
			PostContentType tagRepsBatchContentType;

			if (snsPostContent.getPostContentType() == PostContentType.VIDEO){
				tagRepsBatchContent = Objects.requireNonNullElse(snsPostContent.getBucketUrl(),"") + snsPostContent.getPreviewImg();
				tagRepsBatchContentType =  PostContentType.IMAGE;
			}
			else{
				tagRepsBatchContent = Objects.requireNonNullElse(snsPostContent.getBucketUrl(),"") + snsPostContent.getContent();
				tagRepsBatchContentType = snsPostContent.getPostContentType();
			}


			return SnsTag.builder()
				.tagName(tagName)
				.tagRepsBatchContent(tagRepsBatchContent)
				.tagRepsBatchContentType(tagRepsBatchContentType)
				.build();
		}).toList();

		snsTagJdbcRepository.saveAll(newSnsTagEntityList);

		List<SnsTag> snsPostTagEntityList = new ArrayList<>();
		snsPostTagEntityList.addAll(existingSnsTagEntityList);
		snsPostTagEntityList.addAll(newSnsTagEntityList);

		return snsPostTagEntityList;
	}

	private SnsPost updateUploadSnsPost (
		SnsPost snsPost,
		String title,
		String bodyText,
		Integer targetAudienceValue,
		String address,
		String buildName,
		Float latitude,
		Float longitude,
		SnsUser snsUser,
		List<SnsPostContent> snsPostContentList,
		List<SnsTag> snsPostTagEntityList,
		LocalDateTime createdAt,
		Boolean isApiRequestAddressToGis
	) {

		if (title.length() > PostConst.MAX_POST_TITLE_NUM) throw new BadRequestErrorException("제목 최대 길이는 " + PostConst.MAX_POST_TITLE_NUM + "입니다.");
		if (bodyText.length() > PostConst.MAX_POST_BODY_TEXT_NUM) throw new BadRequestErrorException("본문 최대 길이는 " + PostConst.MAX_POST_BODY_TEXT_NUM + "입니다.");

		snsPost.setSnsPostContents(snsPostContentList);
		snsPost.setSnsUser(snsUser);
		snsPost.setPostTitle(title);
		snsPost.setPostBodyText(bodyText);
		snsPost.setTags(snsPostTagEntityList.stream().map(snsTag -> PostTag.builder()
				.tagId(snsTag.getId())
				.tagName(snsTag.getTagName())
				.build()).toList());
		snsPost.setTgtAudType(switch (targetAudienceValue) {
			case TgtAudTypeValue.PUBLIC_SCOPE_ID_VALUE:
				yield TgtAudType.PUBLIC_SCOPE;
			case TgtAudTypeValue.FOLLOWERS_SCOPE_ID_VALUE:
				yield TgtAudType.FOLLOWERS_SCOPE;
			case TgtAudTypeValue.PRIVATE_SCOPE_ID_VALUE:
				yield TgtAudType.PRIVATE_SCOPE;
			default:
				throw new BadRequestErrorException("옳바르지 않은 공개 대상 타입 입니다.");
		});

		if ( (latitude != null && longitude != null) || (isApiRequestAddressToGis && StringValidUtil.isNotBlank(address))) {
			Float updateLatitude;
			Float updateLongitude;

			if ((latitude != null && longitude != null)){
				updateLatitude = latitude;
				updateLongitude = longitude;
			}
			else{
				GetAddressGeocodeRsp getAddressGeocodeRsp = mapService.getAddressGeocode(address);
				updateLatitude = getAddressGeocodeRsp.getLatitude();
				updateLongitude = getAddressGeocodeRsp.getLongitude();
			}

			snsPost.setAddress(address);
			snsPost.setBuildName(buildName);
			snsPost.setLatitude(updateLatitude);
			snsPost.setLongitude(updateLongitude);
			snsPost.setH3Index(h3Service.getLatLngToH3Cell(updateLatitude, updateLongitude));

			Point point = geometryFactory.createPoint(new Coordinate(updateLongitude, updateLatitude));
			point.setSRID(MapConst.MAP_COORDINATE_SYSTEM); // WGS 84 좌표계 설정
			snsPost.setGeom(point);

		}

		if (createdAt != null){
			snsPost.setCreatedAt(createdAt);
		}

		return snsPost;
	}

	private void registerReportType (SnsPostReport snsPostReport, SnsPostReportCreateReq snsPostReportCreateReq) {
		switch (snsPostReportCreateReq.getPostReportReasonType()) {
			case PostReportConst.POST_DISLIKE_REASON_TYPE -> {
				snsPostReport.setPostReportReasonType(PostReportReasonType.DISLIKE);
				snsPostReport.setReportReason(PostReportConst.POST_DISLIKE_REASON);
			}
			case PostReportConst.POST_INACCURATE_LOCATION_REASON_TYPE -> {
				snsPostReport.setPostReportReasonType(PostReportReasonType.INACCURATE_LOCATION);
				snsPostReport.setReportReason(PostReportConst.POST_INACCURATE_LOCATION_REASON);
			}
			case PostReportConst.POST_SPAM_OR_SCAM_REASON_TYPE -> {
				snsPostReport.setPostReportReasonType(PostReportReasonType.SPAM_OR_SCAM);
				snsPostReport.setReportReason(PostReportConst.POST_SPAM_OR_SCAM_REASON);
			}
			case PostReportConst.POST_SENSITIVE_CONTENT_REASON_TYPE -> {
				snsPostReport.setPostReportReasonType(PostReportReasonType.SENSITIVE_CONTENT);
				snsPostReport.setReportReason(PostReportConst.POST_SENSITIVE_CONTENT);
			}
			case PostReportConst.POST_SENSITIVE_COMMENT_REASON_TYPE -> {
				snsPostReport.setPostReportReasonType(PostReportReasonType.SENSITIVE_CONTENT);
				snsPostReport.setReportReason(PostReportConst.POST_SENSITIVE_COMMENT);
			}
			case PostReportConst.POST_HARMFUL_OR_ABUSIVE_REASON_TYPE -> {
				snsPostReport.setPostReportReasonType(PostReportReasonType.HARMFUL_OR_ABUSIVE);
				snsPostReport.setReportReason(PostReportConst.POST_HARMFUL_OR_ABUSIVE_REASON);
			}
			case PostReportConst.POST_OTHER_REASON_TYPE -> {
				snsPostReport.setPostReportReasonType(PostReportReasonType.OTHER);
				if (snsPostReportCreateReq.getPostReportReason() == null) {
					throw new BadRequestErrorException("신고 이유를 보내 주어야 됩니다.");
				}
				snsPostReport.setReportReason(snsPostReportCreateReq.getPostReportReason());
			}
			default -> throw new BadRequestErrorException("맞지 않는 신고입니다.");
		}
	}


}
