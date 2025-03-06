package com.postvue.feelogserver.endpoint;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.postvue.feelogserver.app.profiles.dto.rsp.create.PostToScrapRsp;
import com.postvue.feelogserver.app.profiles.service.ProfilesService;
import com.postvue.feelogserver.domain.snsposts.SnsPost;
import com.postvue.feelogserver.domain.snsscrap.SnsScrap;
import com.postvue.feelogserver.domain.snsscrap.repository.SnsScrapRepository;
import com.postvue.feelogserver.domain.snsscrapboard.SnsScrapBoard;
import com.postvue.feelogserver.domain.snsusers.SnsUser;
import com.postvue.feelogserver.endpoint.converter.JpaFilterCustomConverter;
import com.postvue.feelogserver.endpoint.dto.SnsScrapEndpointDto;
import com.postvue.feelogserver.global.exception.BadRequestErrorException;
import com.vaadin.hilla.Nullable;
import com.vaadin.hilla.crud.filter.Filter;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SnsScrapEndpointService {
	private final SnsScrapRepository snsScrapRepository;
	private final JpaFilterCustomConverter jpaFilterCustomConverter;
	private final ProfilesService profilesService;


	public List<SnsScrapEndpointDto> listProcess(Pageable pageable, @Nullable Filter filter) {
		Specification<SnsScrap> spec = filter != null
			? jpaFilterCustomConverter.toSpec(filter, SnsScrap.class)
			: Specification.anyOf();


		return snsScrapRepository.findAll(spec, pageable).map((SnsScrapEndpointDto::fromEntity)).toList();
	}

	@Transactional
	public SnsScrap saveProcess(SnsScrapEndpointDto value) {
		SnsScrap snsScrap = value.id() != null && Long.parseLong(value.id()) > 0
			? snsScrapRepository.getReferenceById(Long.parseLong(value.id()))
			: new SnsScrap();

		if (snsScrap.getId() == null){
			PostToScrapRsp postToScrapRsp = profilesService.createPostToScrap(
				Long.parseLong(value.snsUser_id()),
				Long.parseLong(value.snsScrapBoard_id()),
				Long.parseLong(value.snsPost_id())
			);

			snsScrap.setId(Long.valueOf(postToScrapRsp.getScrapPostId()));
			snsScrap.setSnsUser(SnsUser.builder().id(Long.parseLong(value.snsUser_id())).build());
			snsScrap.setSnsPost(SnsPost.builder().id(Long.parseLong(value.snsPost_id())).build());
			snsScrap.setSnsScrapBoard(SnsScrapBoard.builder().id(Long.parseLong(value.snsScrapBoard_id())).build());

			return snsScrap;
		}
		else{
			// snsScrap.setSnsUser(SnsUser.builder().id(Long.parseLong(value.snsUser_id())).build());
			// snsScrap.setSnsPost(SnsPost.builder().id(Long.parseLong(value.snsPost_id())).build());
			// snsScrap.setSnsScrapBoard(SnsScrapBoard.builder().id(Long.parseLong(value.snsScrapBoard_id())).build());
			snsScrap.setCreatedAt(value.createdAt());
			snsScrap.setDeletedAt(value.deletedAt());

			return snsScrapRepository.save(snsScrap);
		}
	}


	@Transactional
	public void deleteProcess(Long id) {
		SnsScrap snsScrap = snsScrapRepository.findById(id).orElseThrow(
			() -> new BadRequestErrorException("해당 게시물은 없습니다.")
		);
		profilesService.deletePostToScrap(snsScrap.getSnsUser().getId(),snsScrap.getSnsScrapBoard().getId(),snsScrap.getSnsPost().getId());
	}
}
