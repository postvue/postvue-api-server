package com.postvue.feelogserver.domain.snsusermessagereactions;

import java.time.LocalDateTime;

import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.DynamicInsert;

import com.postvue.feelogserver.core.config.SnowflakeId;
import com.postvue.feelogserver.domain.snsusermessages.SnsUserMessage;
import com.postvue.feelogserver.domain.snsusermessages.vo.SnsMsgReactionType;
import com.postvue.feelogserver.domain.snsusermessages.vo.SnsMsgReactionTypeValue;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
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
@Table(name = "SNS_USER_MESSAGE_REACTIONS_TB")
@Builder
@DynamicInsert
@NoArgsConstructor
@AllArgsConstructor
public class SnsUserMessageReaction {
	@Id
	@SnowflakeId
	@Column(name = "sns_user_message_reaction_id", updatable = false)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "sns_user_message_id", nullable = false)
	private SnsUserMessage snsUserMessage;

	@Column(name = "has_msg_reaction", nullable = false)
	@ColumnDefault(value = "false")
	private Boolean hasMsgReaction;

	@Enumerated(EnumType.STRING)
	@Column(name = "msg_reaction_type")
	@ColumnDefault(value = SnsMsgReactionTypeValue.DEFAULT_NOT_REACTION_VALUE)
	private SnsMsgReactionType snsMsgReactionType;

	@Column(name = "reacted_at")
	private LocalDateTime reactedAt;

	@Column(name = "is_read", nullable = false)
	@ColumnDefault(value = "false")
	private Boolean isRead;

	@Column(name = "read_at")
	private LocalDateTime readAt;
}
