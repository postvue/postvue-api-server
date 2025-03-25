package com.postvue.feelogserver.app.posts.controller;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

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
import com.postvue.feelogserver.app.posts.dto.rsp.get.SnsPostCommentRsp;
import com.postvue.feelogserver.app.posts.dto.rsp.get.SnsPostInfoRsp;
import com.postvue.feelogserver.app.posts.dto.rsp.get.SnsPostRsp;
import com.postvue.feelogserver.app.posts.dto.rsp.put.PostNotInterestedRsp;
import com.postvue.feelogserver.app.posts.dto.rsp.put.SnsPostClipPutRsp;
import com.postvue.feelogserver.app.posts.dto.rsp.put.SnsPostLikePutRsp;
import com.postvue.feelogserver.app.posts.service.PostsService;
import com.postvue.feelogserver.app.posts.vo.NearFilterType;
import com.postvue.feelogserver.core.security.CustomUserDetails;
import com.postvue.feelogserver.core.security.exception.JwtTokenExpiredException;
import com.postvue.feelogserver.core.security.exception.JwtTokenValidException;
import com.postvue.feelogserver.global.constant.PageConfigConst;
import com.postvue.feelogserver.global.constant.QueryParamConst;
import com.postvue.feelogserver.global.exception.UnauthorizedErrorException;
import com.postvue.feelogserver.global.http.response.serverresponse.ServerGetOkRsp;
import com.postvue.feelogserver.global.http.response.serverresponse.ServerPostCreatedRsp;
import com.postvue.feelogserver.global.http.response.serverresponse.ServerPutOkRsp;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/posts")
@RequiredArgsConstructor
public class PostsController {
	private final PostsService postsService;


	// @VERIFY1
	@GetMapping("/{postId}")
	public ServerGetOkRsp<SnsPostRsp> getPostDetail(
		@AuthenticationPrincipal CustomUserDetails userDetails,
		@PathVariable() Long postId) {

		Long snsUserId = (userDetails == null) ? null : Long.valueOf(userDetails.getUserId());
		return new ServerGetOkRsp<>(postsService.findDetailPost(snsUserId, postId));
	}

	@GetMapping("/{postId}/info")
	public ServerGetOkRsp<SnsPostInfoRsp> getPostInfo(
		@AuthenticationPrincipal CustomUserDetails userDetails,
		@PathVariable() Long postId) {

		Long snsUserId = (userDetails == null) ? null : Long.valueOf(userDetails.getUserId());
		return new ServerGetOkRsp<>(postsService.findPostInfo(snsUserId, postId));
	}

	@GetMapping("/taste_for_me")
	public ServerGetOkRsp<GetTasteForMeRsp> getTasteForMe(
		@AuthenticationPrincipal CustomUserDetails userDetails,
		@RequestParam(name = "cursor", defaultValue = PageConfigConst.LAST_POST_ID) Long cursorId,
		@RequestParam(name = "page", defaultValue = "0") Integer page) {

		Long snsUserId = (userDetails == null) ? null : Long.valueOf(userDetails.getUserId());
		return new ServerGetOkRsp<>(postsService.findTasteForMePosts(snsUserId, page, cursorId));
	}

	@GetMapping("/follow_for_me")
	public ServerGetOkRsp<GetTasteForMeRsp> getFollowForMe(
		@AuthenticationPrincipal CustomUserDetails userDetails,
		@RequestParam(name = "cursor", defaultValue = PageConfigConst.LAST_POST_ID) Long cursorId) {

		Long snsUserId = (userDetails == null) ? null : Long.valueOf(userDetails.getUserId());
		return new ServerGetOkRsp<>(postsService.findFollowForMePosts(snsUserId, cursorId));
	}

	@GetMapping("/tag_for_me")
	public ServerGetOkRsp<List<SnsPostRsp>> getTagForMe(
		@AuthenticationPrincipal CustomUserDetails userDetails,
		@RequestParam(name = "cursor", defaultValue = PageConfigConst.LAST_POST_ID) Long postId) {

		Long snsUserId = (userDetails == null) ? null : Long.valueOf(userDetails.getUserId());
		return new ServerGetOkRsp<>(postsService.findTagForMePosts(snsUserId, postId));
	}

	@GetMapping("/near_for_me")
	public ServerGetOkRsp<List<SnsPostRsp>> getNearForMe(
		@AuthenticationPrincipal CustomUserDetails userDetails,
		@RequestParam(name = "filter", defaultValue = NearFilterType.NEAR_FILTER_ALL_TYPE) String filter,
		@RequestParam(name = "page", defaultValue = PageConfigConst.PAGE_INIT_NUM_STRING) Integer page,
		@RequestParam(name = "lat") Float latitude,
		@RequestParam(name = "lon") Float longitude,
		@RequestParam(name = "startDate", required = false) OffsetDateTime offsetStartDate,
		@RequestParam(name = "endDate", required = false) OffsetDateTime offsetEndDate,
		@RequestParam(name = "distance", required = false) Integer distance
	) {
		LocalDateTime startDate = offsetStartDate !=null ? offsetStartDate.toLocalDateTime() : null;
		LocalDateTime endDate = offsetEndDate !=null ? offsetEndDate.toLocalDateTime() : null;
		Long snsUserId = (userDetails == null) ? null : Long.valueOf(userDetails.getUserId());
		return new ServerGetOkRsp<>(postsService.findNearForMePosts(
			snsUserId, page, latitude, longitude, filter, startDate, endDate, distance
		));
	}

	// @VERIFY1
	// 게시물 댓글 리스트 가져오기
	@GetMapping("/{postId}/comments")
	public ServerGetOkRsp<GetPostCommentsRsp> getPostComments(
		@AuthenticationPrincipal CustomUserDetails userDetails,
		@PathVariable() Long postId,
		@RequestParam(name = "cursor", defaultValue = PageConfigConst.LAST_POST_ID) Long cursorId) {

		Long snsUserId = (userDetails == null) ? null : Long.valueOf(userDetails.getUserId());
		return new ServerGetOkRsp<>(postsService.findPostComments(snsUserId, postId, cursorId));
	}

	@GetMapping("/{postId}/comments/{commentId}/replies")
	public ServerGetOkRsp<GetPostCommentsRsp> getPostCommentRepliesByComment(
		@AuthenticationPrincipal CustomUserDetails userDetails,
		@PathVariable() Long postId,
		@PathVariable() Long commentId,
		@RequestParam(name = "cursor", defaultValue = PageConfigConst.LAST_POST_ID) Long cursorId) {
		Long snsUserId = (userDetails == null) ? null : Long.valueOf(userDetails.getUserId());
		return new ServerGetOkRsp<>(
			postsService.findPostCommentsByComment(postId, snsUserId, commentId, cursorId, false));
	}

	@GetMapping("/{postId}/replies/{replyCommentId}/replies")
	public ServerGetOkRsp<List<SnsPostCommentRsp>> getPostCommentRepliesByReply(
		@AuthenticationPrincipal CustomUserDetails userDetails,
		@PathVariable() Long postId,
		@PathVariable("replyCommentId") Long replyCommentId) {
		Long snsUserId = (userDetails == null) ? null : Long.valueOf(userDetails.getUserId());
		return new ServerGetOkRsp<>(
			postsService.findPostRepliesByReplyCommentId(postId, snsUserId, replyCommentId));
	}

	@GetMapping("/{postId}/likes")
	public ServerGetOkRsp<GetPostLikesRsp> getPostLikes(
		@PathVariable() Long postId,
		@AuthenticationPrincipal CustomUserDetails userDetails,
		@RequestParam(name = "cursor", defaultValue = PageConfigConst.LAST_POST_ID) Long cursorId) {
		Long snsUserId = (userDetails == null) ? null : Long.valueOf(userDetails.getUserId());
		return new ServerGetOkRsp<>(postsService.findPostLikesByPost(postId, cursorId, snsUserId));
	}

	@GetMapping("/{postId}/reposts")
	public ServerGetOkRsp<GetPostRepostsRsp> getPostReposts(
		@AuthenticationPrincipal CustomUserDetails userDetails,
		@PathVariable() Long postId,
		@RequestParam(name = "cursor", defaultValue = PageConfigConst.LAST_POST_ID) Long cursorId) {
		Long snsUserId = (userDetails == null) ? null : Long.valueOf(userDetails.getUserId());
		return new ServerGetOkRsp<>(postsService.findPostIsRepostedListByPost(postId, cursorId, snsUserId));
	}

	@PostMapping("/{postId}/repost")
	public ServerPostCreatedRsp<SnsPostCreateRsp> createRepost(
		@AuthenticationPrincipal CustomUserDetails userDetails,
		@PathVariable() Long postId,
		@RequestBody SnsRepostCreateReq snsRepostCreateReq) throws Exception {
		Long snsUserId = (userDetails == null) ? null : Long.valueOf(userDetails.getUserId());
		return new ServerPostCreatedRsp<>(postsService.savePostRepost(postId, snsUserId, snsRepostCreateReq));
	}


	// @VERIFY1
	@PostMapping("/{postId}/comments")
	public ServerPostCreatedRsp<SnsPostCommentRsp> createPostComment(
		@AuthenticationPrincipal CustomUserDetails userDetails,
		@PathVariable() Long postId,
		@RequestPart("snsPostCmntCreateReq") SnsPostCmntCreateReq snsPostCmntCreateReq,
		@RequestPart(value = "file", required = false) MultipartFile file) {
		Long snsUserId = (userDetails == null) ? null : Long.valueOf(userDetails.getUserId());
		return new ServerPostCreatedRsp<>(postsService.savePostComment(postId, snsUserId, snsPostCmntCreateReq, file));
	}

	@PostMapping("/{postId}/comments/{commentId}")
	public ServerPostCreatedRsp<SnsPostCommentRsp> createPostCommentReply(
		@AuthenticationPrincipal CustomUserDetails userDetails,
		@PathVariable() Long postId,
		@PathVariable() Long commentId,
		@RequestParam(value = "isThread", defaultValue = "false", required = false) Boolean isThread,
		@RequestPart("snsPostCmntCreateReq") SnsPostCmntCreateReq snsPostCmntCreateReq,
		@RequestPart(value = "file", required = false) MultipartFile file) {
		Long snsUserId = (userDetails == null) ? null : Long.valueOf(userDetails.getUserId());
		return new ServerPostCreatedRsp<>(
			postsService.savePostCommentReply(postId, snsUserId, commentId, snsPostCmntCreateReq, isThread, file));
	}

	@PostMapping("/{postId}/interested")
	public ServerPutOkRsp<PostNotInterestedRsp> putPostInterested(
		@AuthenticationPrincipal CustomUserDetails userDetails,
		@PathVariable() Long postId) {
		Long snsUserId = (userDetails == null) ? null : Long.valueOf(userDetails.getUserId());
		return new ServerPutOkRsp<>(postsService.putPostInterested(postId, snsUserId, true));
	}

	@PostMapping("/{postId}/not/interested")
	public ServerPutOkRsp<PostNotInterestedRsp> putPostNotInterested(
		@AuthenticationPrincipal CustomUserDetails userDetails,
		@PathVariable() Long postId) {
		Long snsUserId = (userDetails == null) ? null : Long.valueOf(userDetails.getUserId());
		return new ServerPutOkRsp<>(postsService.putPostInterested(postId, snsUserId, false));
	}

	@PutMapping("/{postId}/clip")
	public ServerPutOkRsp<SnsPostClipPutRsp> putPostClip(
		@AuthenticationPrincipal CustomUserDetails userDetails,
		@PathVariable() Long postId) {
		Long snsUserId = (userDetails == null) ? null : Long.valueOf(userDetails.getUserId());
		return new ServerPutOkRsp<>(postsService.modifyPostClip(postId, snsUserId));
	}

	@PutMapping("/{postId}/like")
	public ServerPutOkRsp<SnsPostLikePutRsp> putPostLike(
		@AuthenticationPrincipal CustomUserDetails userDetails,
		@PathVariable() Long postId) {
		Long snsUserId = (userDetails == null) ? null : Long.valueOf(userDetails.getUserId());
		return new ServerPutOkRsp<>(postsService.modifyPostLike(postId, snsUserId));
	}

	@PutMapping("/{postId}/comments/{commentId}/like")
	public ServerPostCreatedRsp<SnsPostLikePutRsp> putPostCommentLike(
		@AuthenticationPrincipal CustomUserDetails userDetails,
		@PathVariable() Long postId,
		@PathVariable() Long commentId) {

		Long snsUserId = (userDetails == null) ? null : Long.valueOf(userDetails.getUserId());
		return new ServerPostCreatedRsp<>(
			postsService.modifyPostCommentLike(postId, commentId, snsUserId));
	}

	@DeleteMapping("/{postId}")
	public ServerPostCreatedRsp<Boolean> deletePost(
		@AuthenticationPrincipal CustomUserDetails userDetails,
		@PathVariable() Long postId) {
		Long snsUserId = (userDetails == null) ? null : Long.valueOf(userDetails.getUserId());
		return new ServerPostCreatedRsp<>(postsService.deletePostBySnsPostId(postId, snsUserId));
	}

	@DeleteMapping("/comments/{commentId}")
	public ServerPostCreatedRsp<DeleteCommentRsp> deletePostComment(
		@AuthenticationPrincipal CustomUserDetails userDetails,
		@PathVariable() Long commentId) {
		Long snsUserId = (userDetails == null) ? null : Long.valueOf(userDetails.getUserId());
		return new ServerPostCreatedRsp<>(
			postsService.deletePostComment(commentId, snsUserId));
	}

	@PatchMapping("/comments/{commentId}")
	public ServerPostCreatedRsp<SnsPostCommentRsp> patchPostComment(
		@AuthenticationPrincipal CustomUserDetails userDetails,
		@PathVariable() Long commentId,
		@RequestBody SnsPostCmntUpdateReq snsPostCmntUpdateReq) {
		Long snsUserId = (userDetails == null) ? null : Long.valueOf(userDetails.getUserId());
		return new ServerPostCreatedRsp<>(
			postsService.updatePostComment(commentId, snsUserId, snsPostCmntUpdateReq));
	}

	@GetMapping("/search/popular")
	public ServerGetOkRsp<GetSearchPostsRsp> getSearchPosts(
		@AuthenticationPrincipal CustomUserDetails userDetails,
		@RequestParam(name = "srch_qry") String srchQry,
		@RequestParam(name = "isFetchFavorite", defaultValue = QueryParamConst.QUERY_FALSE_VALUE, required = false) Boolean isFetchFavorite,
		@RequestParam(name = "page", defaultValue = PageConfigConst.PAGE_INIT_NUM_STRING) Integer page) {

		Long snsUserId = (userDetails == null) ? null : Long.valueOf(userDetails.getUserId());
		return new ServerGetOkRsp<>(
			postsService.findPostBySearchQueryByPopular(snsUserId, page, srchQry, isFetchFavorite));
	}

	@GetMapping("/search/live")
	public ServerGetOkRsp<GetSearchPostsRsp> getSearchPostsByRecently(
		@AuthenticationPrincipal CustomUserDetails userDetails,
		@RequestParam(name = "srch_qry") String srchQry,
		@RequestParam(name = "isFetchFavorite", defaultValue = QueryParamConst.QUERY_FALSE_VALUE, required = false) Boolean isFetchFavorite,
		@RequestParam(name = "page", defaultValue = PageConfigConst.PAGE_INIT_NUM_STRING) Integer page) {

		Long snsUserId = (userDetails == null) ? null : Long.valueOf(userDetails.getUserId());
		return new ServerGetOkRsp<>(
			postsService.findPostBySearchQueryByRecently(snsUserId, page, srchQry, isFetchFavorite));
	}

	@GetMapping("/search/near")
	public ServerGetOkRsp<GetSearchPostsRsp> getSearchPostsByNear(
		@AuthenticationPrincipal CustomUserDetails userDetails,
		@RequestParam(name = "srch_qry") String srchQry,
		@RequestParam(name = "latitude") Float latitude,
		@RequestParam(name = "longitude") Float longitude,
		@RequestParam(name = "isFetchFavorite", defaultValue = QueryParamConst.QUERY_FALSE_VALUE, required = false) Boolean isFetchFavorite,
		@RequestParam(name = "page", defaultValue = PageConfigConst.PAGE_INIT_NUM_STRING) Integer page
	) {

		Long snsUserId = (userDetails == null) ? null : Long.valueOf(userDetails.getUserId());
		return new ServerGetOkRsp<>(
			postsService.findPostBySearchQueryByNear(snsUserId, page, srchQry, latitude, longitude, isFetchFavorite));
	}

	@GetMapping("/resources/documents/images")
	public ServerGetOkRsp<List<GetPostImageDocResourceRsp>> fetchImages(
		@RequestParam(value = "source_url", required = true) String sourceUrl) {
		return new ServerGetOkRsp<>(postsService.getHtmlImageListParser(sourceUrl));
	}

	@GetMapping("/map_posts")
	public ServerGetOkRsp<List<SnsPostRsp>> getMapPostBySrchQuery(
		@AuthenticationPrincipal CustomUserDetails userDetails,
		@RequestParam(name = "srch_qry") String srchQry,
		@RequestParam(name = "latitude") Float latitude,
		@RequestParam(name = "longitude") Float longitude,
		@RequestParam(name = "page", defaultValue = "0") Integer page,
		@RequestParam(name = "startDate", required = false) OffsetDateTime offsetStartDate,
		@RequestParam(name = "endDate", required = false) OffsetDateTime offsetEndDate
	) {
		LocalDateTime startDate = offsetStartDate !=null ? offsetStartDate.toLocalDateTime() : null;
		LocalDateTime endDate = offsetEndDate !=null ? offsetEndDate.toLocalDateTime() : null;

		Long snsUserId = (userDetails == null) ? null : Long.valueOf(userDetails.getUserId());

		// if (snsUserId == null) {
		// 	throw new JwtTokenExpiredException(new Exception());
		// }
		return new ServerGetOkRsp<>(postsService.getMapPostRelation(snsUserId, srchQry, page, latitude, longitude, startDate, endDate));
	}

	@PostMapping("/compose")
	public ServerPostCreatedRsp<Boolean> composePost(
		@RequestPart("snsPostComposeCreateReq") SnsPostComposeCreateReq snsPostComposeCreateReq,
		@RequestPart(value = "files", required = false) List<MultipartFile> files,
		@AuthenticationPrincipal CustomUserDetails userDetails
	){
		Long snsUserId = (userDetails == null) ? null : Long.valueOf(userDetails.getUserId());
		// 유저 불러오기
		if(snsUserId == null){
			throw new UnauthorizedErrorException("인증되지 않았습니다.");
		}

		return new ServerPostCreatedRsp<>(postsService.composePost(snsPostComposeCreateReq,files,snsUserId));
	}

	@PutMapping("/compose/edit/{postId}")
	public ServerPostCreatedRsp<Boolean> editComposePost(
		@PathVariable("postId") Long postId,
		@RequestPart("snsPostComposeUpdateReq") SnsPostComposeUpdateReq snsPostComposeUpdateReq,
		@RequestPart(value = "files", required = false) List<MultipartFile> files,
		@AuthenticationPrincipal CustomUserDetails userDetails
	){
		Long snsUserId = (userDetails == null) ? null : Long.valueOf(userDetails.getUserId());
		// 유저 불러오기
		if(snsUserId == null){
			throw new UnauthorizedErrorException("인증되지 않았습니다.");
		}

		return new ServerPostCreatedRsp<>(postsService.editPost(postId, snsPostComposeUpdateReq,files,snsUserId));
	}

	@PostMapping("/{postId}/report")
	public ServerPostCreatedRsp<Boolean> postSnsPostReport(
		@AuthenticationPrincipal CustomUserDetails userDetails,
		@PathVariable("postId") Long postId,
		@RequestBody SnsPostReportCreateReq snsPostReportCreateReq
		) {
		Long snsUserId = (userDetails == null) ? null : Long.valueOf(userDetails.getUserId());
		if (snsUserId == null) {
			throw new JwtTokenValidException(new Exception());
		}
		return new ServerPostCreatedRsp<>(postsService.createPostReport(postId,snsUserId,snsPostReportCreateReq));
	}

	@PostMapping("/{postId}/comments/{commentId}/report")
	public ServerPostCreatedRsp<Boolean> postSnsPostCommentReport(
		@AuthenticationPrincipal CustomUserDetails userDetails,
		@PathVariable("postId") Long postId,
		@PathVariable("commentId") Long commentId,
		@RequestBody SnsPostReportCreateReq snsPostReportCreateReq
	) {
		Long snsUserId = (userDetails == null) ? null : Long.valueOf(userDetails.getUserId());
		if (snsUserId == null) {
			throw new JwtTokenValidException(new Exception());
		}
		return new ServerPostCreatedRsp<>(postsService.createPostCommentReport(postId, commentId, snsUserId, snsPostReportCreateReq));
	}

}


