package com.postvue.feelogserver.app.messagequeue.service.consumer;

import java.io.IOException;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.postvue.feelogserver.app.openapis.req.DiscordWebhookRequest;
import com.postvue.feelogserver.app.openapis.service.DiscordService;
import com.postvue.feelogserver.domain.adminserviceerrormanagements.AdminServiceErrorManagement;
import com.postvue.feelogserver.domain.adminserviceerrormanagements.repository.AdminServiceErrorManagementRepository;
import com.postvue.feelogserver.endpoint.SnsPostEndpoint;
import com.postvue.feelogserver.global.admin.service.errormanage.RabbitMQErrorServiceInfo;
import com.postvue.feelogserver.global.constant.AdminConst;
import com.postvue.feelogserver.global.constant.LogTemplateConst;
import com.postvue.feelogserver.global.constant.RabbitMQConst;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.LongString;

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
		try {
			log.info("Received message in parking lot queue");

			Object consumerErrorInfoObj = message.getMessageProperties()
				.getHeaders()
				.get(RabbitMQConst.CONSUMER_ERROR_INFO);
			String errorMsg = null;

			if (consumerErrorInfoObj instanceof String) {
				errorMsg = (String)consumerErrorInfoObj;
			} else if (consumerErrorInfoObj instanceof LongString) {
				errorMsg = ((LongString)consumerErrorInfoObj).toString(); // ✅ LongString을 String으로 변환
			} else if (consumerErrorInfoObj != null) {
				errorMsg = consumerErrorInfoObj.toString(); // ✅ 기타 객체도 String으로 변환
			}

			// ✅ errorMsg가 2000자 이상이면 잘라서 저장
			if (errorMsg != null && errorMsg.length() > 2000) {
				errorMsg = errorMsg.substring(0, 2000);
			}

			adminServiceErrorManagementRepository.save(AdminServiceErrorManagement.builder()
				.serviceErrorType(RabbitMQErrorServiceInfo.SERVICE_ERROR_TYPE_NAME)
				.propMsgString1(errorMsg)
				.build());

			DiscordWebhookRequest request = new DiscordWebhookRequest(errorMsg);
			discordService.sendMessageToPostReportChannel(request);
		}
		catch (Exception e){
			log.error(e.getMessage());
			String errorTemplate = LogTemplateConst.getErrorLogTemplate(
				AdminConst.ADMIN_ERROR,
				"오류가 발생했습니다.",
				e.getMessage(), SnsPostEndpoint.class.toString(),
				"parkingLotConsume",
				null, HttpStatus.INTERNAL_SERVER_ERROR.value());

			adminServiceErrorManagementRepository.save(AdminServiceErrorManagement.builder()
				.serviceErrorType(RabbitMQErrorServiceInfo.SERVICE_ERROR_TYPE_NAME)
				.propMsgString1(errorTemplate)
				.build());
		}
		finally {
			channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
		}
	}
}
