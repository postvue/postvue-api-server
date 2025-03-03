package com.postvue.feelogserver.domain.snsusermessageroommembers;

import java.time.LocalDateTime;

import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.DynamicInsert;

import com.postvue.feelogserver.core.config.SnowflakeId;
import com.postvue.feelogserver.domain.snsusermessagerooms.SnsUserMessageRoom;
import com.postvue.feelogserver.domain.snsusermessagerooms.vo.MsgRoomType;
import com.postvue.feelogserver.domain.snsusers.SnsUser;
import com.postvue.feelogserver.global.common.tables.mixin.BaseMixinImpl;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "SNS_USER_MESSAGE_ROOM_MEMBERS_TB",
	indexes = {
		@Index(
			name = "IDX__ROOM_BY_SNS_USER_MESSAGE_ROOM_MEMBERS",
			columnList = "sns_user_message_room_id"),
		@Index(
			name = "IDX__SOURCE_TARGET_USER_BY_SNS_USER_MESSAGE_ROOM_MEMBERS",
			columnList = "source_user_id, target_user_id, sns_user_message_room_id")
	},
	uniqueConstraints = {
		@UniqueConstraint(columnNames = {"source_user_id", "target_user_id"}, name = "IDX_SOURCE_TARGET_USER_ID_BY_SNS_USER_MESSAGE_ROOM_MEMBERS")
	})
@Builder
@DynamicInsert
@NoArgsConstructor
@AllArgsConstructor
public class SnsUserMessageRoomMember extends BaseMixinImpl {
	@Id
	@SnowflakeId
	@Column(name = "sns_user_message_room_member_id", updatable = false)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "sns_user_message_room_id", nullable = false, updatable = false)
	private SnsUserMessageRoom snsUserMessageRoom;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "source_user_id", nullable = false, updatable = false)
	private SnsUser sourceUser;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "target_user_id", updatable = false)
	private SnsUser targetUser;

	@Enumerated(EnumType.STRING)
	@Column(name = "msg_room_type", nullable = false)
	private MsgRoomType msgRoomType;

	@Column(name = "read_at")
	private LocalDateTime readAt;

	@Column(name = "is_hidden", nullable = false)
	@ColumnDefault(value = "false")
	private Boolean isHidden;

	@Column(name = "is_blocked", nullable = false)
	@ColumnDefault(value = "false")
	private Boolean isBlocked;
}
