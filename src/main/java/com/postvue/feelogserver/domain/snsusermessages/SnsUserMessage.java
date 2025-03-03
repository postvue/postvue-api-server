package com.postvue.feelogserver.domain.snsusermessages;

import java.io.Serializable;
import java.time.LocalDateTime;

import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import com.postvue.feelogserver.core.config.SnowflakeId;
import com.postvue.feelogserver.domain.snsusermessagerooms.SnsUserMessageRoom;
import com.postvue.feelogserver.domain.snsusermessages.vo.MsgMetaInfo;
import com.postvue.feelogserver.domain.snsusermessages.vo.MsgMediaType;
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
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "SNS_USER_MESSAGES_TB", indexes = {
	@Index(
		name = "IDX__SOURCE_TARGET_MSG_READ_BY_SNS_USER_MESSAGES",
		columnList = "source_user_id, sns_user_message_room_id, created_at")})
@Builder
@DynamicInsert
@NoArgsConstructor
@AllArgsConstructor
public class SnsUserMessage extends BaseMixinImpl implements Serializable {
	@Id
	@SnowflakeId
	@Column(name = "sns_user_message_id", updatable = false)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "source_user_id", nullable = false, updatable = false)
	private SnsUser sourceUser;


	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "sns_user_message_room_id", nullable = false, updatable = false)
	private SnsUserMessageRoom snsUserMessageRoom;

	@Column(name = "msg_text_content", length = 2048)
	private String msgTextContent;

	@Enumerated(EnumType.STRING)
	@Column(name = "msg_media_type")
	private MsgMediaType msgMediaType;

	@Column(name = "msg_media_content", length = 2048)
	private String msgMediaContent;

	@JdbcTypeCode(SqlTypes.JSON)
	@Column(name = "msg_meta_info", nullable = false)
	@ColumnDefault("'{\"ogTitle\": \"\", \"ogImage\": \"\", \"ogDescription\": \"\"}'")
	private MsgMetaInfo msgMetaInfo;


	// @ManyToOne(fetch = FetchType.LAZY)
	// @JoinColumn(name = "target_user_id", nullable = false)
	// private SnsUser targetUser;

	// @Enumerated(EnumType.STRING)
	// @Column(name = "msg_type", nullable = false)
	// private SnsMsgType msgType;
	//
	// @Column(name = "msg_content", length = 2048, nullable = false)
	// private String msgContent;

	// @Column(name = "has_msg_reaction", nullable = false)
	// @ColumnDefault(value = "false")
	// private Boolean hasMsgReaction;
	//
	// @Enumerated(EnumType.STRING)
	// @Column(name = "msg_reaction_type")
	// @ColumnDefault(value = SnsMsgReactionTypeValue.DEFAULT_NOT_REACTION_VALUE)
	// private SnsMsgReactionType snsMsgReactionType;

	// @Column(name = "reacted_at")
	// private LocalDateTime reactedAt;

	// @Column(name = "is_read", nullable = false)
	// @ColumnDefault(value = "false")
	// private Boolean isRead;

	// @Column(name = "unread_num", nullable = false)
	// private Integer unreadNum;

	// @JdbcTypeCode(SqlTypes.JSON)
	// @Column(nullable = false, name = "read_user_info_list")
	// @ColumnDefault(value = "'[]'")
	// private List<ReadUserInfo> readUserInfoList = new ArrayList<>();

	// @Column(name = "read_at")
	// private LocalDateTime readAt;

	@Column(name = "deleted_at")
	private LocalDateTime deletedAt;
}
