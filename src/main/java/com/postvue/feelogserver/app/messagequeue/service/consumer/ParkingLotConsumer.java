package com.postvue.feelogserver.app.messagequeue.service.consumer;

import java.io.IOException;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import com.postvue.feelogserver.app.openapis.req.DiscordWebhookRequest;
import com.postvue.feelogserver.app.openapis.service.DiscordService;
import com.postvue.feelogserver.domain.adminserviceerrormanagements.AdminServiceErrorManagement;
import com.postvue.feelogserver.domain.adminserviceerrormanagements.repository.AdminServiceErrorManagementRepository;
import com.postvue.feelogserver.global.admin.service.errormanage.RabbitMQErrorServiceInfo;
import com.postvue.feelogserver.global.constant.RabbitMQConst;
import com.rabbitmq.client.Channel;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class ParkingLotConsumer {
	private final DiscordService discordService;
	private final AdminServiceErrorManagementRepository adminServiceErrorManagementRepository;

	@RabbitListener(queues = RabbitMQConst.RABBIT_MQ_PARKING_LOT_QUEUE)
	public void parkingLotConsume(Channel channel, Message message) throws
		IOException {
		log.info("Received message in parking lot queue");
		String errorMsg = (String) message.getMessageProperties().getHeaders().get(RabbitMQConst.CONSUMER_ERROR_INFO);

		adminServiceErrorManagementRepository.save(AdminServiceErrorManagement.builder()
			.serviceErrorType(RabbitMQErrorServiceInfo.SERVICE_ERROR_TYPE_NAME)
			.propMsgString1(errorMsg)
			.build());

		DiscordWebhookRequest request = new DiscordWebhookRequest(errorMsg);
		discordService.sendMessageToPostReportChannel(request);
		channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
	}
}
