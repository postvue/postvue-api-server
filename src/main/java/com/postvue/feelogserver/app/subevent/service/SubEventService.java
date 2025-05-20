package com.postvue.feelogserver.app.subevent.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.postvue.feelogserver.app.recomm.dto.GetPostContent;
import com.postvue.feelogserver.app.recomm.dto.rsp.GetRecommFollowRsp;
import com.postvue.feelogserver.app.recomm.dto.rsp.GetRecommTagRsp;
import com.postvue.feelogserver.app.recomm.dto.rsp.SnsRecommTagDaoImpl;
import com.postvue.feelogserver.app.subevent.dto.GetShortArticleRsp;
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
import com.postvue.feelogserver.global.admin.service.shortarticle.ShortArticleServiceInfo;
import com.postvue.feelogserver.global.constant.PageConfigConst;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SubEventService {
	private final AdminServiceAdjustmentRepository adminServiceAdjustmentRepository;
	private final ObjectMapper objectMapper;
	@Transactional
	public List<GetShortArticleRsp> findShortArticleListV1(Integer page) {

		Pageable pageable = PageRequest.of(page * PageConfigConst.SHORT_ARTICLE_LIST_PAGE_SIZE,
			PageConfigConst.SHORT_ARTICLE_LIST_PAGE_SIZE);

		// 이미 가져온 ID와 겹치지 않는 요소 필터링
		List<String> shortArticleListByAdmin = adminServiceAdjustmentRepository.findAllByServiceTypeOrderByCreatedAtDesc(
				ShortArticleServiceInfo.SERVICE_TYPE_NAME, pageable).stream().map(AdminServiceAdjustment::getPropString1)
			.toList();

		return shortArticleListByAdmin.stream().map(s -> {
			try {
				System.out.println(s);
				JsonNode jsonNode = objectMapper.readTree(s);
				return GetShortArticleRsp.builder()
					.id(jsonNode.get("id").asLong())
					.articleName(jsonNode.get("article_name").asText())
					.imageNum(jsonNode.get("image_num").asInt())
					.build();
			} catch (JsonProcessingException e) {
				throw new RuntimeException(e);
			}
		}).toList();
	}
}
