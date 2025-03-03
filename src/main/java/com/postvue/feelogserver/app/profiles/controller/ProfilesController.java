package com.postvue.feelogserver.app.profiles.controller;

import java.util.List;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;


import com.postvue.feelogserver.app.common.rsp.SnsPostFollowsGetRsp;
import com.postvue.feelogserver.app.facade.service.PostProfileFacadeService;
import com.postvue.feelogserver.app.posts.service.PostsService;
import com.postvue.feelogserver.app.profiles.dto.req.create.AddPostToScrapListReq;
import com.postvue.feelogserver.app.profiles.dto.req.create.CreateProfileScrapReq;
import com.postvue.feelogserver.app.profiles.dto.req.create.SnsUserReportCreateReq;
import com.postvue.feelogserver.app.profiles.dto.req.put.PutMyPrivateProfileInfoReq;
import com.postvue.feelogserver.app.profiles.dto.req.put.PutMyProfileBirthdateInfoReq;
import com.postvue.feelogserver.app.profiles.dto.req.put.PutMyProfileEmailInfoReq;
import com.postvue.feelogserver.app.profiles.dto.req.put.PutMyProfileGenderInfoReq;
import com.postvue.feelogserver.app.profiles.dto.req.put.PutMyProfileInfoReq;
import com.postvue.feelogserver.app.profiles.dto.req.put.PutMyProfilePasswordInfoReq;
import com.postvue.feelogserver.app.profiles.dto.rsp.common.ScrapThumbnailRsp;
import com.postvue.feelogserver.app.profiles.dto.rsp.create.PostToScrapListRsp;
import com.postvue.feelogserver.app.profiles.dto.rsp.create.PostToScrapRsp;
import com.postvue.feelogserver.app.profiles.dto.rsp.get.GetExistenceByEmailRsp;
import com.postvue.feelogserver.app.profiles.dto.rsp.get.GetExistenceByUsernameRsp;
import com.postvue.feelogserver.app.profiles.dto.rsp.get.GetMyProfileInfoRsp;
import com.postvue.feelogserver.app.profiles.dto.rsp.get.GetProfileBlockedUserRsp;
import com.postvue.feelogserver.app.profiles.dto.rsp.get.GetProfileInfoRsp;
import com.postvue.feelogserver.app.profiles.dto.rsp.get.GetProfilePostListRsp;
import com.postvue.feelogserver.app.profiles.dto.rsp.get.GetProfileUserRsp;
import com.postvue.feelogserver.app.profiles.dto.rsp.get.GetScrapInfoRsp;
import com.postvue.feelogserver.app.profiles.dto.rsp.get.GetScrapPreviewRsp;
import com.postvue.feelogserver.app.profiles.dto.rsp.get.GetScrapRsp;
import com.postvue.feelogserver.app.profiles.dto.rsp.put.PutProfilePasswordInfoRsp;
import com.postvue.feelogserver.app.profiles.service.ProfileFollowsService;
import com.postvue.feelogserver.app.profiles.service.ProfilesService;
import com.postvue.feelogserver.core.security.CustomUserDetails;
import com.postvue.feelogserver.core.security.exception.JwtTokenExpiredException;
import com.postvue.feelogserver.core.security.exception.JwtTokenValidException;
import com.postvue.feelogserver.global.constant.PageConfigConst;
import com.postvue.feelogserver.global.exception.BadRequestErrorException;
import com.postvue.feelogserver.global.http.response.serverresponse.ServerDeleteRsp;
import com.postvue.feelogserver.global.http.response.serverresponse.ServerGetOkRsp;
import com.postvue.feelogserver.global.http.response.serverresponse.ServerPostCreatedRsp;
import com.postvue.feelogserver.global.util.password.PasswordValidationUtil;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/profiles")
@RequiredArgsConstructor
public class ProfilesController {
	private final ProfilesService profilesService;
	private final ProfileFollowsService profileFollowsService;
	private final PostsService postsService;
	private final PostProfileFacadeService postProfileFacadeService;

	// @GetMapping("/follows/{username}/info")
	// public ServerGetOkRsp<GetFollowProfileInfoRsp> getFollowProfileInfo(@PathVariable("username") String username) {
	// 	return new ServerGetOkRsp<>(profilesService.getFollowProfileInfo(username, userId));
	// }

	@GetMapping("/info/me")
	public ServerGetOkRsp<GetMyProfileInfoRsp> getMyProfileInfo(
		@AuthenticationPrincipal CustomUserDetails userDetails) {
		Long snsUserId = (userDetails == null) ? null : Long.valueOf(userDetails.getUserId());

		if (snsUserId == null) {
			throw new BadRequestErrorException("계정을 로그인해주세요");
		}
		return new ServerGetOkRsp<>(profilesService.getMyProfileInfo(snsUserId));
	}

	// @VERIFY1
	@PutMapping("/me/info")
	public ServerGetOkRsp<GetMyProfileInfoRsp> putMyProfileInfo(
		@AuthenticationPrincipal CustomUserDetails userDetails,
		@RequestPart("putMyProfileInfoReq") PutMyProfileInfoReq putMyProfileInfoReq,
		@RequestPart(value = "profileImgFile", required = false) MultipartFile profileImgFile) {
		Long snsUserId = (userDetails == null) ? null : Long.valueOf(userDetails.getUserId());
		return new ServerGetOkRsp<>(profilesService.putMyProfileInfo(snsUserId, putMyProfileInfoReq, profileImgFile));
	}

	// @VERIFY1
	@PutMapping("/me/info/email")
	public ServerGetOkRsp<GetMyProfileInfoRsp> putMyProfileEmailInfo(
		@AuthenticationPrincipal CustomUserDetails userDetails,
		@Valid @RequestBody PutMyProfileEmailInfoReq putMyProfileEmailInfoReq) {
		Long snsUserId = (userDetails == null) ? null : Long.valueOf(userDetails.getUserId());
		return new ServerGetOkRsp<>(profilesService.putMyProfileEmailInfo(snsUserId, putMyProfileEmailInfoReq));
	}

	// @VERIFY1
	@PutMapping("/me/info/birthdate")
	public ServerGetOkRsp<GetMyProfileInfoRsp> putMyProfileBirthdateInfo(
		@AuthenticationPrincipal CustomUserDetails userDetails,
		@Valid @RequestBody PutMyProfileBirthdateInfoReq putMyProfileBirthdateInfoReq) {
		Long snsUserId = (userDetails == null) ? null : Long.valueOf(userDetails.getUserId());
		return new ServerGetOkRsp<>(profilesService.putMyProfileBirthdateInfo(snsUserId, putMyProfileBirthdateInfoReq));
	}

	// @VERIFY1
	@PutMapping("/me/info/gender")
	public ServerGetOkRsp<GetMyProfileInfoRsp> putMyProfileGenderInfo(
		@AuthenticationPrincipal CustomUserDetails userDetails,
		@Valid @RequestBody PutMyProfileGenderInfoReq putMyProfileGenderInfoReq) {
		Long snsUserId = (userDetails == null) ? null : Long.valueOf(userDetails.getUserId());
		return new ServerGetOkRsp<>(profilesService.putMyProfileGenderInfo(snsUserId, putMyProfileGenderInfoReq));
	}

	// @VERIFY1
	@PutMapping("/me/info/private-profile")
	public ServerGetOkRsp<GetMyProfileInfoRsp> putMyPrivateProfile(
		@AuthenticationPrincipal CustomUserDetails userDetails,
		@RequestBody PutMyPrivateProfileInfoReq putMyPrivateProfileInfoReq) {

		Long snsUserId = (userDetails == null) ? null : Long.valueOf(userDetails.getUserId());

		return new ServerGetOkRsp<>(
			profilesService.putMyPrivateProfile(snsUserId, putMyPrivateProfileInfoReq));
	}

	// @VERIFY1
	@PutMapping("/me/info/password")
	public ServerGetOkRsp<PutProfilePasswordInfoRsp> putMyProfilePasswordInfo(
		@AuthenticationPrincipal CustomUserDetails userDetails,
		@Valid @RequestBody PutMyProfilePasswordInfoReq putMyProfilePasswordInfoReq, HttpServletResponse response) {

		Long snsUserId = (userDetails == null) ? null : Long.valueOf(userDetails.getUserId());

		if (!PasswordValidationUtil.isValid((putMyProfilePasswordInfoReq.getPassword())))
			throw new BadRequestErrorException("옳바른 비밀번호가 아닙니다.");
		return new ServerGetOkRsp<>(
			profilesService.putMyProfilePasswordInfo(snsUserId, putMyProfilePasswordInfoReq, response));
	}

	// @VERIFY1
	// 해당 유저 Id 존재하는 지 조회 api
	@GetMapping("/existence/{username}")
	public ServerGetOkRsp<GetExistenceByUsernameRsp> getProfileExist(@PathVariable("username") String username) {
		return new ServerGetOkRsp<>(profilesService.getExistenceByUsername(username));
	}

	// @VERIFY1
	@GetMapping("/existence/emails/{email}")
	public ServerGetOkRsp<GetExistenceByEmailRsp> getProfileEmailExist(
		@PathVariable("email") String email) {
		return new ServerGetOkRsp<>(profilesService.getExistenceByEmail(email));
	}

	// @VERIFY1
	@GetMapping("/{username}/info")
	public ServerGetOkRsp<GetProfileInfoRsp> getProfileInfo(
		@AuthenticationPrincipal CustomUserDetails userDetails,
		@PathVariable("username") String username) {
		Long snsUserId = (userDetails == null) ? null : Long.valueOf(userDetails.getUserId());
		return new ServerGetOkRsp<>(profilesService.getProfileInfo(username, snsUserId));
	}

	// @VERIFY1
	@GetMapping("/me/followings")
	public ServerGetOkRsp<List<SnsPostFollowsGetRsp>> getMyProfileFollowings(
		@AuthenticationPrincipal CustomUserDetails userDetails,
		@RequestParam(value = "page", defaultValue = PageConfigConst.PAGE_INIT_NUM_STRING) Integer page) {
		Long snsUserId = (userDetails == null) ? null : Long.valueOf(userDetails.getUserId());
		return new ServerGetOkRsp<>(profileFollowsService.getMyProfileFollowing(snsUserId, page));
	}


	@GetMapping("/search/users/{username}")
	public ServerGetOkRsp<GetProfileUserRsp> getProfileUserListByUsername(
		@AuthenticationPrincipal CustomUserDetails userDetails,
		@PathVariable("username") String username,
		@RequestParam(value = "cursor", defaultValue = PageConfigConst.LAST_POST_ID) Long cursorId,
		@RequestParam(value = "hasFollowInfo", required = false) Boolean hasFollowInfo
	) {
		Long snsUserId = (userDetails == null) ? null : Long.valueOf(userDetails.getUserId());
		if (snsUserId == null) {
			throw new JwtTokenValidException(new Exception());
		}
		return new ServerGetOkRsp<>(profilesService.getProfileUserListByUsername(username, snsUserId, cursorId, hasFollowInfo));
	}

	// @VERIFY1
	@GetMapping("/{username}/followers")
	public ServerGetOkRsp<List<SnsPostFollowsGetRsp>> getProfileFollower(
		@AuthenticationPrincipal CustomUserDetails userDetails,
		@PathVariable("username") String username,
		@RequestParam(value = "page", defaultValue = PageConfigConst.PAGE_INIT_NUM_STRING) Integer page) {
		Long snsUserId = (userDetails == null) ? null : Long.valueOf(userDetails.getUserId());
		return new ServerGetOkRsp<>(profileFollowsService.getProfileFollower(snsUserId, username, page));
	}

	// @VERIFY1
	@GetMapping("/{username}/followings")
	public ServerGetOkRsp<List<SnsPostFollowsGetRsp>> getProfileFollowing(
		@AuthenticationPrincipal CustomUserDetails userDetails,
		@PathVariable("username") String username,
		@RequestParam(value = "page", defaultValue = PageConfigConst.PAGE_INIT_NUM_STRING) Integer page) {
		Long snsUserId = (userDetails == null) ? null : Long.valueOf(userDetails.getUserId());
		return new ServerGetOkRsp<>(profileFollowsService.getProfileFollowing(snsUserId, username, page));
	}

	@GetMapping("/scraps")
	public ServerGetOkRsp<List<ScrapThumbnailRsp>> getProfileScraps(
		@AuthenticationPrincipal CustomUserDetails userDetails,
		@RequestParam(value = "page", defaultValue = PageConfigConst.PAGE_INIT_NUM_STRING) Integer page) {
		Long snsUserId = (userDetails == null) ? null : Long.valueOf(userDetails.getUserId());

		return new ServerGetOkRsp<>(profilesService.getScrapLists(snsUserId, page));
	}

	// @VERIFY1
	// 스크랩 생성
	@PostMapping("/scraps")
	public ServerPostCreatedRsp<ScrapThumbnailRsp> postProfileScraps(
		@AuthenticationPrincipal CustomUserDetails userDetails,
		@RequestBody CreateProfileScrapReq createProfileScrapReq,
		@RequestParam(value = "postId", required = false) Long postId) {
		Long snsUserId = (userDetails == null) ? null : Long.valueOf(userDetails.getUserId());

		return new ServerPostCreatedRsp<>(profilesService.createProfileScrap(snsUserId, createProfileScrapReq, postId));
	}

	@GetMapping("/search/scraps/{scrapName}")
	public ServerGetOkRsp<List<ScrapThumbnailRsp>> getProfileScrapsBySearchQuery(
		@AuthenticationPrincipal CustomUserDetails userDetails,
		@PathVariable("scrapName") String scrapName,
		@RequestParam(value = "page", defaultValue = PageConfigConst.PAGE_INIT_NUM_STRING) Integer page) {
		Long snsUserId = (userDetails == null) ? null : Long.valueOf(userDetails.getUserId());
		return new ServerGetOkRsp<>(profilesService.getScrapListsBySearchQuery(snsUserId, scrapName, page));
	}

	// @VERIFY1
	@GetMapping("/scraps/previews")
	public ServerGetOkRsp<List<GetScrapPreviewRsp>> getProfileScrapPreviews(
		@AuthenticationPrincipal CustomUserDetails userDetails,
		@RequestParam(value = "postId", required = false) Long postId) {
		Long snsUserId = (userDetails == null) ? null : Long.valueOf(userDetails.getUserId());
		return new ServerGetOkRsp<>(profilesService.getScrapPreviewList(snsUserId, postId));
	}

	// @VERIFY1
	@GetMapping("/scraps/{scrapId}/info")
	public ServerGetOkRsp<GetScrapInfoRsp> getProfileScrapInfo(
		@AuthenticationPrincipal CustomUserDetails userDetails,
		@PathVariable("scrapId") Long scrapId) {
		Long snsUserId = (userDetails == null) ? null : Long.valueOf(userDetails.getUserId());
		return new ServerGetOkRsp<>(profilesService.getScrapInfo(snsUserId, scrapId));
	}

	// @VERIFY1
	@GetMapping("/scraps/{scrapId}")
	public ServerGetOkRsp<GetScrapRsp> getProfileScraps(
		@AuthenticationPrincipal CustomUserDetails userDetails,
		@PathVariable("scrapId") Long scrapId,
		@RequestParam(value = "cursor", defaultValue = PageConfigConst.LAST_POST_ID, required = true) Long cursorId) {
		Long snsUserId = (userDetails == null) ? null : Long.valueOf(userDetails.getUserId());
		return new ServerGetOkRsp<>(profilesService.getScrapRsp(snsUserId, scrapId, cursorId));
	}

	// @VERIFY1
	@GetMapping("/clips")
	public ServerGetOkRsp<GetProfilePostListRsp> getProfileClips(
		@AuthenticationPrincipal CustomUserDetails userDetails,
		@RequestParam(value = "cursor", defaultValue = PageConfigConst.LAST_POST_ID) Long cursorId) {
		Long snsUserId = (userDetails == null) ? null : Long.valueOf(userDetails.getUserId());
		return new ServerGetOkRsp<>(profilesService.getMyClipListRsp(snsUserId, cursorId));
	}

	// @VERIFY1
	@GetMapping("/{username}/posts")
	public ServerGetOkRsp<GetProfilePostListRsp> getProfilePosts(
		@AuthenticationPrincipal CustomUserDetails userDetails,
		@PathVariable("username") String username,
		@RequestParam(name = "cursor", defaultValue = PageConfigConst.LAST_POST_ID) Long postId) {
		Long snsUserId = (userDetails == null) ? null : Long.valueOf(userDetails.getUserId());
		return new ServerGetOkRsp<>(postsService.findProfilePosts(snsUserId, username, postId));
	}

	// @VERIFY1
	@GetMapping("/blocked-users")
	public ServerGetOkRsp<List<GetProfileBlockedUserRsp>> getProfileBlockedUserList(
		@AuthenticationPrincipal CustomUserDetails userDetails,
		@RequestParam(value = "page", defaultValue = PageConfigConst.PAGE_INIT_NUM_STRING) Integer page) {
		Long snsUserId = (userDetails == null) ? null : Long.valueOf(userDetails.getUserId());
		return new ServerGetOkRsp<>(profilesService.getProfileBlockedUserList(snsUserId, page));
	}

	// 하나의 포스트에 대해서 여러개의 스크랩에 저장하는 기능
	@PostMapping("/scraps/posts/{postId}")
	public ServerPostCreatedRsp<PostToScrapListRsp> addPostToScrapList(
		@AuthenticationPrincipal CustomUserDetails userDetails,
		@PathVariable("postId") Long postId,
		@RequestBody AddPostToScrapListReq addPostToScrapListReq) {

		Long snsUserId = (userDetails == null) ? null : Long.valueOf(userDetails.getUserId());
		return new ServerPostCreatedRsp<>(
			postProfileFacadeService.createPostToScrapList(snsUserId, postId, addPostToScrapListReq.getScrapIdList(),false));
	}

	// 하나의 포스트에 대해서 하나의 스크랩에 저장
	@PostMapping("/scraps/{scrapId}/posts/{postId}")
	public ServerPostCreatedRsp<PostToScrapRsp> addPostToScrap(
		@AuthenticationPrincipal CustomUserDetails userDetails,
		@PathVariable("scrapId") Long scrapId,
		@PathVariable("postId") Long postId) {
		Long snsUserId = (userDetails == null) ? null : Long.valueOf(userDetails.getUserId());
		return new ServerPostCreatedRsp<>(profilesService.createPostToScrap(snsUserId, scrapId, postId));
	}

	@DeleteMapping("/scraps/{scrapId}/posts/{postId}")
	public ServerDeleteRsp<PostToScrapRsp> deletePostToScrap(
		@AuthenticationPrincipal CustomUserDetails userDetails,
		@PathVariable("scrapId") Long scrapId,
		@PathVariable("postId") Long postId) {
		Long snsUserId = (userDetails == null) ? null : Long.valueOf(userDetails.getUserId());
		return new ServerDeleteRsp<>(profilesService.deletePostToScrap(snsUserId, scrapId, postId));
	}

	// @VERIFY1
	@PostMapping("/follows/{followId}")
	public ServerPostCreatedRsp<Boolean> postProfileFollow(
		@AuthenticationPrincipal CustomUserDetails userDetails,
		@PathVariable("followId") Long followId) {
		Long snsUserId = (userDetails == null) ? null : Long.valueOf(userDetails.getUserId());
		return new ServerPostCreatedRsp<>(profileFollowsService.createFollow(snsUserId, followId));
	}

	// @VERIFY1
	@DeleteMapping("/follows/{followId}")
	public ServerDeleteRsp<Boolean> deleteProfileFollow(
		@AuthenticationPrincipal CustomUserDetails userDetails,
		@PathVariable("followId") Long followId) {
		Long snsUserId = (userDetails == null) ? null : Long.valueOf(userDetails.getUserId());
		return new ServerDeleteRsp<>(profileFollowsService.deleteFollow(snsUserId, followId));
	}

	// @VERIFY1
	@PostMapping("/blocks/{blockedUserId}")
	public ServerPostCreatedRsp<Boolean> addUserToBlockList(
		@AuthenticationPrincipal CustomUserDetails userDetails,
		@PathVariable("blockedUserId") Long blockedUserId) {
		Long snsUserId = (userDetails == null) ? null : Long.valueOf(userDetails.getUserId());
		return new ServerPostCreatedRsp<>(profilesService.createUserToBlockList(snsUserId, blockedUserId));
	}

	// @VERIFY1
	@DeleteMapping("/blocks/{blockedUserId}")
	public ServerDeleteRsp<Boolean> deleteBlocUser(
		@AuthenticationPrincipal CustomUserDetails userDetails,
		@PathVariable("blockedUserId") Long blockedUserId) {
		Long snsUserId = (userDetails == null) ? null : Long.valueOf(userDetails.getUserId());
		return new ServerDeleteRsp<>(profilesService.deleteBlockUser(snsUserId, blockedUserId));
	}

	// @VERIFY1
	// 스크랩 정보 수정
	@PutMapping("/scraps/{scrapId}")
	public ServerPostCreatedRsp<ScrapThumbnailRsp> putProfileScraps(
		@PathVariable("scrapId") Long scrapId,
		@AuthenticationPrincipal CustomUserDetails userDetails,
		@RequestBody CreateProfileScrapReq createProfileScrapReq) {
		Long snsUserId = (userDetails == null) ? null : Long.valueOf(userDetails.getUserId());
		return new ServerPostCreatedRsp<>(profilesService.updateProfileScrap(scrapId, snsUserId, createProfileScrapReq));
	}

	// @VERIFY1
	@DeleteMapping("/scraps/{scrapId}")
	public ServerDeleteRsp<Boolean> deleteProfileScrapBoard(
		@AuthenticationPrincipal CustomUserDetails userDetails,
		@PathVariable("scrapId") Long scrapId) {
		Long snsUserId = (userDetails == null) ? null : Long.valueOf(userDetails.getUserId());
		return new ServerDeleteRsp<>(profilesService.deleteScrapBoard(snsUserId,scrapId));
	}

	@PostMapping("/report/{userId}")
	public ServerPostCreatedRsp<Boolean> postReportUser(
		@AuthenticationPrincipal CustomUserDetails userDetails,
		@PathVariable("userId") Long userId,
		@Valid @RequestBody SnsUserReportCreateReq snsUserReportCreateReq
	) {
		Long myUserId = (userDetails == null) ? null : Long.valueOf(userDetails.getUserId());
		return new ServerPostCreatedRsp<>(profilesService.createUserReport(myUserId, userId, snsUserReportCreateReq));
	}

}
