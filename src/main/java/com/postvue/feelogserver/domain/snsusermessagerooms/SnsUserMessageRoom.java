package com.postvue.feelogserver.domain.snsusermessagerooms;

import org.hibernate.annotations.DynamicInsert;

import com.postvue.feelogserver.core.config.SnowflakeId;
import com.postvue.feelogserver.domain.snsusermessagerooms.vo.MsgRoomType;
import com.postvue.feelogserver.global.common.tables.mixin.BaseMixinImpl;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "SNS_USER_MESSAGE_ROOMS_TB")
@Builder
@DynamicInsert
@NoArgsConstructor
@AllArgsConstructor
public class SnsUserMessageRoom extends BaseMixinImpl {
	@Id
	@SnowflakeId
	@Column(name = "sns_user_message_room_id", updatable = false)
	private Long id;

	@Enumerated(EnumType.STRING)
	@Column(name = "msg_room_type", nullable = false, updatable = false)
	private MsgRoomType msgRoomType;
}
