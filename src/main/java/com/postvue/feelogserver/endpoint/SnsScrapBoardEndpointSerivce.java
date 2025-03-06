package com.postvue.feelogserver.endpoint;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.postvue.feelogserver.app.profiles.dto.req.create.CreateProfileScrapReq;
import com.postvue.feelogserver.app.profiles.dto.rsp.common.ScrapThumbnailRsp;
import com.postvue.feelogserver.app.profiles.service.ProfilesService;
import com.postvue.feelogserver.domain.snsscrapboard.SnsScrapBoard;
import com.postvue.feelogserver.domain.snsscrapboard.repository.SnsScrapBoardRepository;
import com.postvue.feelogserver.domain.snsusers.SnsUser;
import com.postvue.feelogserver.endpoint.converter.JpaFilterCustomConverter;
import com.postvue.feelogserver.endpoint.dto.SnsScrapBoardEndpointDto;
import com.postvue.feelogserver.global.exception.BadRequestErrorException;
import com.vaadin.hilla.Nonnull;
import com.vaadin.hilla.Nullable;
import com.vaadin.hilla.crud.filter.Filter;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SnsScrapBoardEndpointSerivce {
	private final SnsScrapBoardRepository snsScrapBoardRepository;
	private final JpaFilterCustomConverter jpaFilterCustomConverter;
	private final ProfilesService profilesService;

	@Transactional
	public List<@Nonnull SnsScrapBoard> listProcess(Pageable pageable, @Nullable Filter filter) {
		Specification<SnsScrapBoard> spec = filter != null
			? jpaFilterCustomConverter.toSpec(filter, SnsScrapBoard.class)
			: Specification.anyOf();
		return snsScrapBoardRepository.findAll(spec,pageable).toList();
	}

	@Transactional
	public SnsScrapBoard saveProcess(SnsScrapBoardEndpointDto value) {
		SnsScrapBoard snsScrapBoard = value.id() != null && Long.parseLong(value.id()) > 0
			? snsScrapBoardRepository.getReferenceById(Long.parseLong(value.id()))
			: new SnsScrapBoard();

		if (snsScrapBoard.getId() == null){

			ScrapThumbnailRsp scrapThumbnailRsp = profilesService.createProfileScrap(
				Long.parseLong(value.snsUser_id()),
				new CreateProfileScrapReq(value.scrapName(),value.targetAudience().toString()),
				null
			);

			snsScrapBoard.setSnsUser(SnsUser.builder().id(Long.parseLong(value.snsUser_id())).build());
			snsScrapBoard.setScrapName(value.scrapName());
			snsScrapBoard.setTargetAudience(value.targetAudience());
			snsScrapBoard.setId(Long.valueOf(scrapThumbnailRsp.getScrapId()));

			return snsScrapBoard;
		}
		else{
			// snsScrapBoard.setSnsUser(SnsUser.builder().id(Long.parseLong(value.snsUser_id())).build());
			snsScrapBoard.setScrapName(value.scrapName());
			snsScrapBoard.setTargetAudience(value.targetAudience());
			snsScrapBoard.setCreatedAt(value.createdAt());
			snsScrapBoard.setDeletedAt(value.deletedAt());
			snsScrapBoard.setLastUpdatedAt(value.lastUpdatedAt());

			return snsScrapBoardRepository.save(snsScrapBoard);
		}
	}

	@Transactional
	public void deleteProcess(Long id) {
		SnsScrapBoard snsScrapBoard = snsScrapBoardRepository.findById(id).orElseThrow(
			() -> new BadRequestErrorException("해당 스크랩 보드는 없습니다.")
		);
		profilesService.deleteScrapBoard(snsScrapBoard.getSnsUser().getId(),snsScrapBoard.getId());
	}
}
