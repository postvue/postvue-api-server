package com.postvue.feelogserver.app.posts.service;

import java.io.File;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import org.apache.commons.io.FilenameUtils;
import org.jsoup.Jsoup;
import org.jsoup.UnsupportedMimeTypeException;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.postvue.feelogserver.app.cloud.service.MinioCloudService;
import com.postvue.feelogserver.app.cloud.service.R2CloudService;
import com.postvue.feelogserver.app.common.rsp.SnsPostFollowsGetRsp;
import com.postvue.feelogserver.app.externallib.ffmpeg.FfmpegProcessingService;
import com.postvue.feelogserver.app.maps.dto.GetAddressGeocodeRsp;
import com.postvue.feelogserver.app.maps.service.MapService;
import com.postvue.feelogserver.app.messagequeue.service.producer.VideoConversationProducer;
import com.postvue.feelogserver.app.notifications.service.NotificationService;
import com.postvue.feelogserver.app.posts.dto.common.Location;
import com.postvue.feelogserver.app.posts.dto.common.PostContent;
import com.postvue.feelogserver.app.posts.dto.req.create.SnsPostCmntCreateReq;
import com.postvue.feelogserver.app.posts.dto.req.create.SnsPostCmntUpdateReq;
import com.postvue.feelogserver.app.posts.dto.req.create.SnsPostComposeCreateReq;
import com.postvue.feelogserver.app.posts.dto.req.create.SnsPostReportCreateReq;
import com.postvue.feelogserver.app.posts.dto.req.create.SnsRepostCreateReq;
import com.postvue.feelogserver.app.posts.dto.rsp.create.SnsPostCreateRsp;
import com.postvue.feelogserver.app.posts.dto.rsp.delete.DeleteCommentRsp;
import com.postvue.feelogserver.app.posts.dto.rsp.get.GetPostCommentsRsp;
import com.postvue.feelogserver.app.posts.dto.rsp.get.GetPostImageDocResourceRsp;
import com.postvue.feelogserver.app.posts.dto.rsp.get.GetPostLikesRsp;
import com.postvue.feelogserver.app.posts.dto.rsp.get.GetPostRelationRsp;
import com.postvue.feelogserver.app.posts.dto.rsp.get.GetPostRepostsRsp;
import com.postvue.feelogserver.app.posts.dto.rsp.get.GetSearchPostsRsp;
import com.postvue.feelogserver.app.posts.dto.rsp.get.GetTasteForMeRsp;
import com.postvue.feelogserver.app.posts.dto.rsp.get.SnsPostCommentRsp;
import com.postvue.feelogserver.app.posts.dto.rsp.get.SnsPostInfoRsp;
import com.postvue.feelogserver.app.posts.dto.rsp.get.SnsPostRsp;
import com.postvue.feelogserver.app.posts.dto.rsp.put.PostNotInterestedRsp;
import com.postvue.feelogserver.app.posts.dto.rsp.put.SnsPostClipPutRsp;
import com.postvue.feelogserver.app.posts.dto.rsp.put.SnsPostLikePutRsp;
import com.postvue.feelogserver.app.posts.vo.NearFilterType;
import com.postvue.feelogserver.app.profiles.dto.rsp.get.GetProfilePostListRsp;
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
import com.postvue.feelogserver.global.constant.PageConfigConst;
import com.postvue.feelogserver.global.constant.PostReportConst;
import com.postvue.feelogserver.global.constant.MediaConfigConst;
import com.postvue.feelogserver.global.exception.BadRequestErrorException;
import com.postvue.feelogserver.global.exception.BaseException;
import com.postvue.feelogserver.global.exception.InternalServerErrorException;
import com.postvue.feelogserver.global.exception.UnauthorizedErrorException;
import com.postvue.feelogserver.global.util.converter.TagConverter;
import com.postvue.feelogserver.global.util.response.ObjectConvertRspUtil;
import com.postvue.feelogserver.global.util.validation.StringValidUtil;
import com.postvue.feelogserver.global.util.validation.UploadFileValidationUtils;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

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

	@Value("${file.imageSize}")
	private Integer imageFileSize;

	@Value("${file.videoSize}")
	private Integer videoFileSize;


	public GetTasteForMeRsp findTasteForMePosts(Long snsUserId, Integer page, Long cursorId) {
		Long cursor = Long.valueOf(PageConfigConst.ZERO_ID);
		List<SnsPostDao> snsPostList = new ArrayList<>();
		List<SnsPostDao> snsPostsByTag = snsPostRepository.selectTasteForMeByTag(snsUserId,
			page * PageConfigConst.STUFF_FOR_ME_BY_TAG_PAGE_SIZE,
			PageConfigConst.STUFF_FOR_ME_BY_TAG_PAGE_SIZE);

		int followPageNum = (int)(
			Math.random() * (PageConfigConst.FOLLOW_MAX_PAGE_NUM - PageConfigConst.FOLLOW_MIN_PAGE_NUM)
				+ PageConfigConst.FOLLOW_MIN_PAGE_NUM);

		int popularPageNum = PageConfigConst.POPULAR_PAGE_NUM;

		if (cursorId > 0) {
			List<SnsPostDao> snsPostsByFollow = snsPostRepository.selectTasteForMeByFollow(snsUserId, cursorId,
				followPageNum);
			if (!snsPostsByFollow.isEmpty()) {
				cursor = snsPostsByFollow.get(snsPostsByFollow.size() - 1).getCursorId();
				snsPostList.addAll(snsPostsByFollow);
			}
		}

		List<SnsPostDao> snsPostsByPopular = snsPostRepository.selectTasteForMeByPopular(snsUserId,
			page * popularPageNum, popularPageNum,
			LocalDateTime.now());

		snsPostList.addAll(snsPostsByTag);
		snsPostList.addAll(snsPostsByPopular);

		List<SnsPostRsp> snsPostRspList = new ArrayList<>(getPostGetRspList(snsPostList).stream().collect(
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

	public GetTasteForMeRsp findFollowForMePosts(Long snsUserId, Long cursorId) {
		Long cursor = Long.valueOf(PageConfigConst.ZERO_ID);
		List<SnsPostDao> snsPostList = new ArrayList<>();

		int followPageNum = (int)(PageConfigConst.FOLLOW_FOR_ME_PAGE_SIZE);

		if (cursorId > 0) {
			List<SnsPostDao> snsPostsByFollow = snsPostRepository.selectTasteForMeByFollow(snsUserId, cursorId,
				followPageNum);
			if (!snsPostsByFollow.isEmpty()) {
				cursor = snsPostsByFollow.get(snsPostsByFollow.size() - 1).getCursorId();
				snsPostList.addAll(snsPostsByFollow);
			}
		}

		List<SnsPostRsp> snsPostRspList = new ArrayList<>(getPostGetRspList(snsPostList).stream().collect(
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

	public List<SnsPostRsp> findTagForMePosts(Long snsUserId, Long snsPostId) {
		List<SnsPostDao> snsPosts = snsPostRepository.selectTagForMe(snsUserId, snsPostId,
			PageConfigConst.DEFAULT_PAGE_SIZE);

		return getPostGetRspList(snsPosts);
	}

	public List<SnsPostRsp> findNearForMePosts(Long snsUserId, Integer page, Float latitude, Float longitude,
		String nearFilter) {
		PostContentBusinessType postContentBusinessType = getPostContentBusinessType(
			nearFilter);

		if (nearFilter.equals(NearFilterType.NEAR_FILTER_ALL_TYPE)) {
			List<SnsPostDao> snsPosts = snsPostRepository.selectNearForMe(snsUserId,
				page * PageConfigConst.NEAR_FOR_ME_BY_TAG_PAGE_SIZE,
				PageConfigConst.NEAR_FOR_ME_BY_TAG_PAGE_SIZE, latitude, longitude, LocalDateTime.now());

			return getPostGetRspList(snsPosts);
		} else if (postContentBusinessType != null) {
			List<SnsPostDao> snsPosts = snsPostRepository.selectNearForMeBy(snsUserId,
				page * PageConfigConst.NEAR_FOR_ME_BY_TAG_PAGE_SIZE,
				PageConfigConst.NEAR_FOR_ME_BY_TAG_PAGE_SIZE, latitude, longitude,
				postContentBusinessType.label(),
				LocalDateTime.now()
			);

			return getPostGetRspList(snsPosts);
		} else {
			throw new BadRequestErrorException("해당 콘텐츠 타입은 없습니다.");
		}
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

	public GetProfilePostListRsp findProfilePosts(Long snsUserId, String username, Long snsPostId) {
		List<SnsPostDao> snsPosts = snsPostRepository.selectProfilePosts(snsUserId, username, snsPostId,
			PageConfigConst.DEFAULT_PAGE_SIZE);

		List<SnsPostRsp> snsPostRspList = getPostGetRspList(snsPosts);
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

	public SnsPostRsp findDetailPost(Long userId, Long postId) {
		SnsPostDao snsPostDao = snsPostRepository.selectDetailPost(userId, postId).orElseThrow();
		return convertToPostGetRsp(snsPostDao);
	}

	public SnsPostInfoRsp findPostInfo(Long userId, Long postId) {
		SnsPostInfoDao snsPostInfoDao = snsPostRepository.selectPostInfo(postId, userId).orElseThrow(
			() -> new BadRequestErrorException("해당 게시물은 없습니다.")
		);

		if (!Objects.equals(snsPostInfoDao.getSnsUserId(), userId)) {
			throw new UnauthorizedErrorException("권한이 없습니다.");
		}
		return convertToPostInfoRsp(snsPostInfoDao);
	}

	public GetPostCommentsRsp findPostComments(Long snsUserId, Long snsPostId, Long snsPostCommentReactionId) {
		Pageable pageable = PageRequest.of(PageConfigConst.PAGE_INIT_NUM, PageConfigConst.DEFAULT_PAGE_SIZE);
		List<SnsPostCommentRsp> snsPostCommentRspList = snsPostCommentReactionRepository.selectCommentByPostId(
				snsPostId,
				snsUserId,
				snsPostCommentReactionId, pageable)
			.stream()
			.map((this::convertToPostCommentGetRsp))
			.toList();
		if (!snsPostCommentRspList.isEmpty()) {
			return new GetPostCommentsRsp(
				snsPostCommentRspList.get(snsPostCommentRspList.size() - 1).getPostCommentId(), snsPostCommentRspList);
		} else {
			return new GetPostCommentsRsp(PageConfigConst.ZERO_ID, new ArrayList<>());
		}
	}

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

	public List<SnsPostCommentRsp> findPostRepliesByReplyCommentId(Long snsPostId, Long userId,
		Long replyCommentId) {
		return snsPostCommentReactionRepository.selectRepliesByReplyCommentId(
				snsPostId, userId, replyCommentId)
			.stream()
			.map((this::convertToPostCommentGetRsp))
			.toList();
	}

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

	public GetSearchPostsRsp findPostBySearchQueryByPopular(Long snsUserId, Integer page, String searchQuery,
		Boolean isFetchFavorite) {
		boolean isBookMarkFavoriteTerm = false;
		if (isFetchFavorite || Objects.equals(page, PageConfigConst.PAGE_INIT_NUM)) {
			isFetchFavorite = true;
			Optional<SnsUserFavoriteTermBookmark> favoriteTermBookmarkOptional = snsUserFavoriteTermBookmarkRepository.findBySnsUser_IdAndFavoriteTermName(
				snsUserId, searchQuery);
			isBookMarkFavoriteTerm = favoriteTermBookmarkOptional.isPresent();
		}
		List<SnsPostDao> snsPosts = snsPostRepository.selectPostBySearchQueryPopular(snsUserId,
			searchQuery,
			page * PageConfigConst.DEFAULT_PAGE_SIZE,
			PageConfigConst.DEFAULT_PAGE_SIZE, LocalDateTime.now());

		List<SnsPostRsp> snsPostRspList = getPostGetRspList(snsPosts);
		return GetSearchPostsRsp.builder()
			.snsPostRspList(snsPostRspList)
			.isFetchFavoriteState(isFetchFavorite)
			.isBookMarkedFavoriteTerm(isBookMarkFavoriteTerm)
			.build();
	}

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
		List<SnsPostDao> snsPosts = snsPostRepository.selectPostBySearchQueryRecently(snsUserId,
			page * PageConfigConst.DEFAULT_PAGE_SIZE,
			searchQuery,
			PageConfigConst.DEFAULT_PAGE_SIZE);

		List<SnsPostRsp> snsPostRspList = getPostGetRspList(snsPosts);
		return GetSearchPostsRsp.builder()
			.snsPostRspList(snsPostRspList)

			.isFetchFavoriteState(isFetchFavorite)
			.isBookMarkedFavoriteTerm(isBookMarkFavoriteTerm)
			.build();
	}

	public GetSearchPostsRsp findPostBySearchQueryByNear(
		Long snsUserId, Integer page, String searchQuery,
		Float latitude, Float longitude,
		Boolean isFetchFavorite) {
		boolean isBookMarkFavoriteTerm = false;
		if (isFetchFavorite || Objects.equals(page, PageConfigConst.PAGE_INIT_NUM)) {
			isFetchFavorite = true;
			Optional<SnsUserFavoriteTermBookmark> favoriteTermBookmarkOptional = snsUserFavoriteTermBookmarkRepository
				.findBySnsUser_IdAndFavoriteTermName(
					snsUserId, searchQuery);
			isBookMarkFavoriteTerm = favoriteTermBookmarkOptional.isPresent();
		}
		List<SnsPostDao> snsPosts = snsPostRepository.selectPostBySearchQueryNear(snsUserId,
			page * PageConfigConst.DEFAULT_PAGE_SIZE,
			searchQuery,
			PageConfigConst.DEFAULT_PAGE_SIZE,
			latitude,
			longitude
		);

		List<SnsPostRsp> snsPostRspList = getPostGetRspList(snsPosts);
		return GetSearchPostsRsp.builder()
			.snsPostRspList(snsPostRspList)

			.isFetchFavoriteState(isFetchFavorite)
			.isBookMarkedFavoriteTerm(isBookMarkFavoriteTerm)
			.build();
	}

	public GetPostRelationRsp findPostRelation(Long postId, Long userId, Long cursorId) {
		List<SnsTagPost> snsTagPostList = snsTagPostRepository.findBySnsPost_Id(postId);

		List<String> tagList = snsTagPostList.stream().map(snsTagPost -> snsTagPost.getSnsTag().getTagName()).toList();
		String relatedTagListString = TagConverter.convertTagListToTagListSqlString(tagList);

		List<SnsPostDao> snsPostDaos = snsPostRepository.selectPostRelation(relatedTagListString, userId, cursorId,
			PageConfigConst.POST_RELATION_PAGE_NUM);

		snsPostDaos = snsPostDaos.stream()
			.filter((snsPostDao -> !Objects.equals(snsPostDao.getPostId(), postId)))
			.toList();

		List<SnsPostRsp> snsPostRspList = getPostGetRspList(snsPostDaos);

		return ObjectConvertRspUtil.GenericObjectListRsp(snsPostRspList, GetPostRelationRsp::new,
			snsPostRspList.isEmpty() ? "" :
				snsPostRspList.get(snsPostRspList.size() - 1)
					.getPostId());
	}

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
		if (snsPostUserReactionOpt.isEmpty()) {
			SnsPost snsPost = snsPostRepository.findById(snsPostId).orElseThrow(
				() -> new BadRequestErrorException("해당 게시물은 없습니다.")
			);
			snsPostUserReaction = SnsPostUserReaction.builder()
				.snsPost(snsPost)
				.snsUser(snsUserRepository.findById(
					snsUserId
				).orElseThrow(() -> new BadRequestErrorException("해당 계정은 없습니다.")))
				.isLiked(false)
				.build();
			snsPost.setReactionCount((snsPost.getReactionCount() != null ? snsPost.getReactionCount() : 0) + 1);
		} else {
			snsPostUserReaction = snsPostUserReactionOpt.get();
		}
		snsPostUserReaction.setIsLiked(!snsPostUserReaction.getIsLiked());
		snsPostUserReaction.setIsLikedAt(LocalDateTime.now());
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
	// 	// @REFER: ? 왜 리액션 추가 했지?
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
		SnsPost snsOriginPost = snsPostRepository.findById(snsPostId).orElseThrow();

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
		snsOriginPost.setReactionCount(snsOriginPost.getReactionCount() + 1);

		return new SnsPostCreateRsp(snsPostUserReaction.getIsReposted(),
			convertToPostGetRspByPostCreated(snsReNewPost));
	}

	@Transactional
	public SnsPostCommentRsp savePostComment(Long snsPostId, Long snsUserId,
		SnsPostCmntCreateReq snsPostCmntCreateReq, MultipartFile file) {
		SnsUser snsUser = snsUserRepository.findById(snsUserId).orElseThrow();

		SnsPost snsPost = snsPostRepository.findById(snsPostId)
			.orElseThrow(() -> new BadRequestErrorException("해당 게시물은 없습니다."));

		SnsPostCommentReaction snsPostCmntReaction = SnsPostCommentReaction.builder()
			.isCommented(true)
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

		snsPost.setReactionCount((snsPost.getReactionCount() != null ? snsPost.getReactionCount() : 0) + 1);

		return convertToPostCommentGetRsp(newSnsPostCommentReaction);
	}

	@Transactional
	public SnsPostCommentRsp savePostCommentReply(Long snsPostId, Long snsUserId, Long snsPostCommentId,
		SnsPostCmntCreateReq snsPostCmntCreateReq, Boolean isThread, MultipartFile file) {
		SnsUser snsUser = snsUserRepository.findById(snsUserId).orElseThrow();

		SnsPost snsPost = SnsPost.builder().id(snsPostId).build();

		SnsPostCommentReaction snsPostCmntReaction = SnsPostCommentReaction.builder()
			.commentUser(snsUser)
			.sourceComment(SnsPostCommentReaction.builder()
				.id(snsPostCommentId)
				.build())
			.isSource(false)
			.commentMsg(snsPostCmntCreateReq.getPostCommentMsg())
			.snsPost(snsPost)
			.build();

		snsPostCmntReaction.setIsCommented(true);
		saveCmntReaction(file, snsPostCmntReaction);


		return convertToPostReplyByCommentRsp(snsPostCmntReaction, isThread);
	}

	private SnsPostCommentReaction saveCmntReaction(MultipartFile file, SnsPostCommentReaction snsPostCmntReaction) {
		if (file != null){
			System.out.println("호잉승");
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



			String contentUrl = r2CloudService.getPostCommentImageContentUrlByMinio(UUID.randomUUID() + "." + extension);

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
			.orElseThrow();

		// @REFER: 진짜 삭제하진 말고 deleted_at으로 관리
		snsTagPostRepository.deleteAllBySnsPostId(mySnsPost);
		mySnsPost.setDeletedAt(LocalDateTime.now());
		snsPostRepository.save(mySnsPost);
		// snsPostUserReactionRepository.deleteBySnsPostAndSnsUser_Id(mySnsPost, snsUserId);
		// snsPostRepository.delete(mySnsPost);

		return true;
	}

	@Transactional
	public DeleteCommentRsp deletePostComment(Long snsCommentId, Long snsUserId) {
		SnsPostCommentReaction snsPostCommentReaction = snsPostCommentReactionRepository
			.findBySnsPostCommentReactionIdAndCommentUser_SnsUserId(snsCommentId, snsUserId).orElseThrow();

		// @REFER: 삭제하지 말고, deletedAt 적용
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
			.findBySnsPostCommentReactionIdAndCommentUser_SnsUserId(snsCommentId, snsUserId).orElseThrow();
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

		SnsPostCommentLike snsPostCommentLike = snsPostCommentLikeRepository.findBySnsPostCommentReaction_IdAndSnsUser_Id(
			snsCommentId, snsUserId).orElse(
			SnsPostCommentLike.builder()
				.snsPost(SnsPost.builder().id(postId).build())
				.snsPostCommentReaction(SnsPostCommentReaction.builder().id(snsCommentId).build())
				.snsUser(SnsUser.builder().id(snsUserId).build())
				.isLiked(false)
				.build()
		);
		snsPostCommentLike.setIsLiked(!snsPostCommentLike.getIsLiked());
		snsPostCommentLike.setIsLikedAt(LocalDateTime.now());

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

	public List<SnsPostRsp> getMapPostRelation(Long snsUserId, String srchQry, Integer page) {
		return snsPostRepository.findAllMapPostRelationBySearchQuery(snsUserId, srchQry,
				page * PageConfigConst.DEFAULT_PAGE_SIZE, PageConfigConst.DEFAULT_PAGE_SIZE)
			.stream()
			.map((this::convertToPostGetRsp))
			.toList();
	}

	@Transactional
	public Boolean composePost (
		SnsPostComposeCreateReq snsPostComposeCreateReq,
		List<MultipartFile> files,
		Long userId
	) {
		createPostCompose(new SnsPost(),snsPostComposeCreateReq ,files,userId,false);
		return true;
	}

	@Transactional
	// @REFER: cloudflare 랑 minio랑 구분 되도록 필요
	public Boolean editPost (
		Long snsPostId,
		SnsPostComposeCreateReq snsPostComposeCreateReq,
		List<MultipartFile> files,
		Long userId
	) {
		SnsPost snsPost = snsPostRepository.findById(snsPostId).orElseThrow(
			() -> new BadRequestErrorException("해당 게시물은 없습니다.")
		);

		snsPost.getSnsPostContents().forEach((snsPostContent -> {
			if(snsPostContent.getIsLink()) return;
			if ( snsPostComposeCreateReq.getPostContentLinkList().stream().map(PostContent::getContent).toList().contains(snsPostContent.getContent())){
				String oldFileUrl = snsPostContent.getContent();
				//@REFER: 매직 넘버
				r2CloudService.renameImageInR2(oldFileUrl, String.format("delete/posts/%d/%s",snsPost.getId(),oldFileUrl));
			}
		}));
		createPostCompose(snsPost, snsPostComposeCreateReq, files, userId,true);
		return true;
	}

	@Transactional(rollbackOn = Exception.class)
	public void createPostCompose(
		SnsPost snsPost,
		SnsPostComposeCreateReq snsPostComposeCreateReq,
		List<MultipartFile> files,
		Long userId,
		Boolean isEdit
	) {
		int fileSize =  files != null ? files.size() : 0;

		// 업로드 수가 타당한지?
		isValidUploadNum(fileSize + snsPostComposeCreateReq.getPostContentLinkList().size());

		List<SnsPostContent> snsPostContentList = new ArrayList<>();
		AtomicInteger index = new AtomicInteger();

		// content url 생성 및 추가 추가
		addContentUrls(fileSize, files , snsPostComposeCreateReq, snsPostContentList, index);

		SnsUser snsUser = SnsUser.builder().id(userId).build();

		// 태그 생성
		List<SnsTag> snsPostTagEntityList = createPostTagRelation(snsPostComposeCreateReq, snsPostContentList);

		// 링크로 업로드 된 이미지나 비디오
		// QUERY 4: INSERT, NEW POST
		SnsPost _snsPost = updateUploadSnsPost(snsPost, snsPostComposeCreateReq, snsUser, snsPostContentList,snsPostTagEntityList);

		try {
			if (isEdit){
				snsTagPostJdbcRepository.deleteByPostId(_snsPost.getId());
				snsPostJdbcRepository.updatePost(_snsPost);
			}
			else{
				snsPostJdbcRepository.insertPost(_snsPost);

				// QUERY 5: INSERT SNS POST USER REACTION
				SnsPostUserReaction snsPostUserReaction = SnsPostUserReaction.builder()
					.snsPost(_snsPost)
					.snsUser(snsUser)
					.build();
				snsPostUserReactionRepository.save(snsPostUserReaction);
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

		if (fileSize > 0){
			// r2CloudService.uploadFileToR2(files, fileContentUrls);
			try {
				for (int i = 0; i < files.size(); i++) {
					// 이미지 인 경우 => cloudflare r2에 저장
					if(UploadFileValidationUtils.isImage(files.get(i).getContentType())){
						r2CloudService.uploadImageToR2(files.get(i), snsPostContentList.get(i).getContent());
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
							files.get(i).getOriginalFilename());
						files.get(i).transferTo(videoTempFile);

						// poster 이미지 생성및 업로드
						File posterImgFile = ffmpegProcessingService.generateVideoPoster(videoTempFile, MediaConfigConst.TEMP_IMAGE_NAME);
						minioCloudService.uploadImageJpegToMinio(posterImgFile, minioCloudService.getBucketKeyContentUrl(
							snsPostContentList.get(i).getPreviewImg()));
						posterImgFile.delete();

						// RabbitMQ에 등록
						videoConversationProducer.sendVideoConversionUploadToQueue(
							_snsPost.getId(),
							videoTempFile.getAbsolutePath(),
							snsPostContentList.get(i)
						);
					}
				}
			} catch (BaseException e) {
				throw e;
			} catch (Exception e) {
				System.out.println(e);
				throw new InternalServerErrorException("서버 오류로 업로드 실패", e);
			}
		}
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
		SnsPostReport snsPostReport = SnsPostReport.builder()
			.snsPost(snsPost)
			.snsPostCommentReaction(SnsPostCommentReaction.builder().id(snsPostCommentId).build())
			.reporterUser(SnsUser.builder().id(snsUserId).build())
			.reportedUser(snsPost.getSnsUser())
			.postReportStatus(PostReportStatus.PENDING)
			.build();

		registerReportType(snsPostReport, snsPostReportCreateReq);

		snsPostReportRepository.save(snsPostReport);
		return true;
	}

	public List<SnsPostRsp> getPostGetRspList(List<SnsPostDao> snsPostDaoList) {
		return snsPostDaoList.stream().map((this::convertToPostGetRsp)).toList();
	}

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
			.location(new Location(snsPost.getLatitude(), snsPost.getLongitude(), snsPost.getAddress()))
			.tags(snsPost.getTags().stream().map((PostTag::getTagName)).toList())
			.isFollowed(false)
			.followable(false)
			.isLiked(false)
			.isClipped(false)
			.postTitle(snsPost.getPostTitle())
			.postBodyText(snsPost.getPostBodyText())
			// @REFER: 제거
			// .postCategory(snsPost.getPostCategory().toString())
			.postContents(snsPost.getSnsPostContents()
				.stream()
				.map((snsPostContent -> new PostContent(snsPostContent.getPostContentType(),
					snsPostContent.getContent(),
					snsPostContent.getAscSortNum(),
					snsPostContent.getPreviewImg(),
					snsPostContent.getIsUploaded()
					)))
				.toList())
			.postedAt(snsPost.getCreatedAt())
			.build();
	}

	private SnsPostRsp convertToPostGetRsp(SnsPostDao snsPostDao) {
		return SnsPostRsp.builder()
			.postId(snsPostDao.getPostId().toString())
			.userId(snsPostDao.getSnsUserId().toString())
			.username(snsPostDao.getUsername())
			.profilePath(snsPostDao.getProfilePath())
			.location(new Location(snsPostDao.getLatitude(), snsPostDao.getLongitude(), snsPostDao.getAddress()))
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
					snsPostContent.getIsUploaded()
				)))
				.toList())
			.postedAt(snsPostDao.getPostedAt())
			.build();
	}

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

	private SnsPostInfoRsp convertToPostInfoRsp(SnsPostInfoDao snsPostInfoDao) {
		return SnsPostInfoRsp.builder()
			.postId(snsPostInfoDao.getPostId().toString())
			.userId(snsPostInfoDao.getSnsUserId().toString())
			.location(
				new Location(snsPostInfoDao.getLatitude(), snsPostInfoDao.getLongitude(), snsPostInfoDao.getAddress()))
			.tags(snsPostInfoDao.getStringToTags().stream().map(PostTagDao::getTagName).toList())
			.postTitle(snsPostInfoDao.getPostTitle())
			.postBodyText(snsPostInfoDao.getPostBodyText())
			.postContents(snsPostInfoDao.getStringToSnsPostContents()
				.stream()
				.map((snsPostContent -> new PostContent(snsPostContent.getPostContentType(),
					snsPostContent.getContent(),
					snsPostContent.getAscSortNum(),
					snsPostContent.getPreviewImg(),
					snsPostContent.getIsUploaded()
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

	private void addContentUrls (Integer fileSize, List<MultipartFile> files,
		SnsPostComposeCreateReq snsPostComposeCreateReq,
		List<SnsPostContent> snsPostContentList, AtomicInteger index){

		// 파일이 아닌 링크로 업로드 하는 경우
		snsPostComposeCreateReq.getPostContentLinkList().forEach((postContent -> {
			snsPostContentList.add(SnsPostContent.builder()
				.postContentType(postContent.getPostContentType())
				.content(postContent.getContent())
				.ascSortNum(index.incrementAndGet())
				.isLink(true)
				.build());
		}));

		// 파일이 한개 이상인 경우
		if(fileSize > 0){
			files.forEach((multipartFile -> {
				// 이미지 또는 비디오 파일인지 확인
				String contentType = multipartFile.getContentType();
				String originalFilename = multipartFile.getOriginalFilename();

				String extension = FilenameUtils.getExtension(originalFilename);

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
					String contentUrl = r2CloudService.getPostImageContentUrlByMinio(UUID.randomUUID() + "." + extension);
					String buckUrl = r2CloudService.bucketPublicUrl;

					System.out.println("버킷: " + buckUrl);
					newSnsPostContent = SnsPostContent.builder()
						.postContentType(
							PostContentType.IMAGE
						)
						.content(contentUrl)
						.bucketUrl(buckUrl)
						.ascSortNum(index.incrementAndGet())
						.isLink(isLink)
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
					String buckUrl = minioCloudService.bucketPublicUrl;
					newSnsPostContent = SnsPostContent.builder()
						.postContentType(
							PostContentType.VIDEO
						)
						.content(videoContentPath)
						.previewImg(posterPath)
						.bucketUrl(buckUrl)
						.ascSortNum(index.incrementAndGet())
						.isLink(isLink)
						.isUploaded(false) // 비디오 처리는 다소 오래거리기 때문에 RabbitMQ로 처리
						.build();
				}
				snsPostContentList.add(newSnsPostContent);
			}));
		}
	}

	private List<SnsTag> createPostTagRelation (SnsPostComposeCreateReq snsPostComposeCreateReq, List<SnsPostContent> snsPostContentList) {
		List<String> tagNameList = snsPostComposeCreateReq.getTagList();

		// QUERY 2: SELECT, EXITING SNS TAG LIST
		List<SnsTag> existingSnsTagEntityList = snsTagRepository.findAllByTagNameIn(tagNameList);

		List<String> newTagNameList = tagNameList.stream()
			.filter(tagName -> !existingSnsTagEntityList.stream().map(SnsTag::getTagName).toList().contains(tagName))
			.toList();

		System.out.println("컨텐츠: " + snsPostContentList.size());
		snsPostContentList.forEach(snsPostContent -> System.out.println(snsPostContent.getContent()));
		Random random = new Random();
		int randomIndexs = random.nextInt(snsPostContentList.size());
		System.out.println("컨텐츠1: " + snsPostContentList.get(randomIndexs).getContent());
		System.out.println("콘텐츠: " + snsPostContentList.get(randomIndexs).getPostContentType());
		// QUERY 3: BULK INSERT, NEW TAG LIST
		List<SnsTag> newSnsTagEntityList = newTagNameList.stream().map(tagName -> {
			int randomIndex = random.nextInt(snsPostContentList.size());
			return SnsTag.builder()
			.tagName(tagName)
			.tagRepsBatchContent(snsPostContentList.get(randomIndex).getContent())
			.tagRepsBatchContentType(snsPostContentList.get(randomIndex).getPostContentType())
			.build();
		}).toList();

		snsTagJdbcRepository.saveAll(newSnsTagEntityList);

		List<SnsTag> snsPostTagEntityList = new ArrayList<>();
		snsPostTagEntityList.addAll(existingSnsTagEntityList);
		snsPostTagEntityList.addAll(newSnsTagEntityList);

		return snsPostTagEntityList;
	}

	private SnsPost updateUploadSnsPost (
		SnsPost snsPost,SnsPostComposeCreateReq
		snsPostComposeCreateReq, SnsUser snsUser,
		List<SnsPostContent> snsPostContentList,
		List<SnsTag> snsPostTagEntityList) {
		snsPost.setSnsPostContents(snsPostContentList);
		snsPost.setSnsUser(snsUser);
		snsPost.setPostTitle(snsPostComposeCreateReq.getTitle());
		snsPost.setPostTitle(snsPostComposeCreateReq.getTitle());
		snsPost.setPostBodyText(snsPostComposeCreateReq.getBodyText());
		snsPost.setTags(snsPostTagEntityList.stream().map(snsTag -> PostTag.builder()
				.tagId(snsTag.getId())
				.tagName(snsTag.getTagName())
				.build()).toList());
		snsPost.setTgtAudType(switch (snsPostComposeCreateReq.getTargetAudienceValue()) {
			case TgtAudTypeValue.PUBLIC_SCOPE_ID_VALUE:
				yield TgtAudType.PUBLIC_SCOPE;
			case TgtAudTypeValue.FOLLOWERS_SCOPE_ID_VALUE:
				yield TgtAudType.FOLLOWERS_SCOPE;
			case TgtAudTypeValue.PRIVATE_SCOPE_ID_VALUE:
				yield TgtAudType.PRIVATE_SCOPE;
			default:
				throw new BadRequestErrorException("옳바르지 않은 공개 대상 타입 입니다.");
		});

		if (StringValidUtil.isNotBlank(snsPostComposeCreateReq.getAddress())) {
			GetAddressGeocodeRsp getAddressGeocodeRsp = mapService.getAddressGeocode(
				snsPostComposeCreateReq.getAddress());

			snsPost.setAddress(snsPostComposeCreateReq.getAddress());
			snsPost.setLatitude(getAddressGeocodeRsp.getLatitude());
			snsPost.setLongitude(getAddressGeocodeRsp.getLongitude());
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
