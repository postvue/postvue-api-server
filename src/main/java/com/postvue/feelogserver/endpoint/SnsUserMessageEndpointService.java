package com.postvue.feelogserver.endpoint;

import java.util.Arrays;
import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.postvue.feelogserver.app.messagequeue.dto.VideoUploadConversionMessage;
import com.postvue.feelogserver.app.messages.service.MessagesService;
import com.postvue.feelogserver.domain.snsposts.vo.SnsPostContent;
import com.postvue.feelogserver.domain.snsusermessages.SnsUserMessage;
import com.postvue.feelogserver.domain.snsusermessages.repository.SnsUserMessageRepository;
import com.postvue.feelogserver.domain.snsusermessages.vo.MsgMetaInfo;
import com.postvue.feelogserver.endpoint.converter.JpaFilterCustomConverter;
import com.postvue.feelogserver.endpoint.dto.SnsUserMessageEndpointDto;
import com.postvue.feelogserver.global.exception.BadRequestErrorException;
import com.postvue.feelogserver.global.util.converter.JsonConverter;
import com.vaadin.hilla.Nullable;
import com.vaadin.hilla.crud.filter.Filter;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SnsUserMessageEndpointService {
	private final SnsUserMessageRepository snsUserMessageRepository;
	private final JpaFilterCustomConverter jpaFilterCustomConverter;
	private final MessagesService messagesService;
	private final ObjectMapper objectMapper;

	public List<SnsUserMessage> listProcess(Pageable pageable, @Nullable Filter filter) {
		Specification<SnsUserMessage> spec = filter != null
			? jpaFilterCustomConverter.toSpec(filter, SnsUserMessage.class)
			: Specification.anyOf();
		return snsUserMessageRepository.findAll(spec,pageable).toList();
	}

	@Transactional
	public SnsUserMessage saveProcess(SnsUserMessageEndpointDto value) throws JsonProcessingException {
		SnsUserMessage snsUserMessage;
		if (value.id() != null && Long.parseLong(value.id()) > 0){
			 snsUserMessage = snsUserMessageRepository.getReferenceById(Long.parseLong(value.id()));
		}
		else {
			throw new BadRequestErrorException("해당 메시지는 없습니다.");
		}

		snsUserMessage.setMsgTextContent(value.msgTextContent());
		snsUserMessage.setMsgMediaType(value.msgMediaType());
		snsUserMessage.setMsgMediaContent(value.msgMediaContent());
		snsUserMessage.setCreatedAt(value.createdAt());
		snsUserMessage.setLastUpdatedAt(value.lastUpdatedAt());
		snsUserMessage.setDeletedAt(value.deletedAt());

		MsgMetaInfo msgMetaInfo = objectMapper.readValue(value.msgMetaInfo(), MsgMetaInfo.class);
		snsUserMessage.setMsgMetaInfo(msgMetaInfo);

		return snsUserMessageRepository.save(snsUserMessage);
	}

	@Transactional
	public void deleteProcess(Long id) {
		SnsUserMessage snsUserMessage = snsUserMessageRepository.findById(id).orElseThrow(
			() -> new BadRequestErrorException("해당 메시지는 없습니다.")
		);
		messagesService.deleteMsgConversation("/amdin/message/channel",snsUserMessage.getId(),snsUserMessage.getSourceUser().getId());
	}
}
