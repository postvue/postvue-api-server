package com.postvue.feelogserver.domain.snsusers;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.DynamicInsert;

import com.postvue.feelogserver.core.config.SnowflakeId;
import com.postvue.feelogserver.domain.snsusers.vo.SignUpType;
import com.postvue.feelogserver.domain.snsusers.vo.SnsAppRole;
import com.postvue.feelogserver.domain.snsusers.vo.SnsUserGender;
import com.postvue.feelogserver.domain.snsusers.vo.SnsUserState;
import com.postvue.feelogserver.global.common.tables.mixin.BaseMixinImpl;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@DynamicInsert
@Entity
@Getter
@Setter
@Table(name = "SNS_USERS_TB",
	uniqueConstraints = {
		@UniqueConstraint(columnNames = {"signupEmail", "sign_up_type"}, name = "IDX_UNIQUE_SIGNUP_TYPE_EMAIL_BY_SNS_USERS_TB"),
	},
	indexes = {
		// 해당 게시물의 유저 빠르게 찾기
		@Index(name = "IDX__USERNAME_UNIQUE_BY_SNS_USERS", columnList = "username", unique = true),
	}
)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SnsUser extends BaseMixinImpl implements Serializable {
	@Id
	@SnowflakeId
	@Column(name = "sns_user_id", updatable = false)
	private Long id;

	//@REFER: 매직넘버로 함, 수정 필요
	@Size(min = 1, max = 30, message = "최소 1글자, 최대 30글자까지 입력 가능합니다.")
	@Pattern(regexp = "^[\\p{L}0-9_](.*)?$", message = "맨 앞 공백 없이 알파벳, 숫자, 밑줄(_), 그 뒤 공백 포함 가능")
	@Column(name = "nickname", nullable = false, length = 255)
	private String nickname;

	@Pattern(
		regexp = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$",
		message = "옳바른 이메일 형식이 아닙니다."
	)
	@Column(name = "signup_email", length = 512, unique = true, updatable = false)
	private String signupEmail;

	@Pattern(
		regexp = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$",
		message = "옳바른 이메일 형식이 아닙니다."
	)
	@Column(name = "email", length = 512)
	private String email;

	//@ANSWER: 일단 5, 18개로 지정
	@Size(min = 5, max = 18, message = "최소 5글자, 최대 18글자까지 입력 가능합니다.")
	@Pattern(regexp = "^[a-zA-Z][a-zA-Z0-9_]*$", message = "첫 글자는 알파벳이어야 하며, 공백 없이 알파벳, 숫자, 밑줄(_) 만 허용됩니다.")
	@Column(name = "username", nullable = false, unique = true, updatable = false, length = 1024)
	private String username;

	@Column(name = "user_link", length = 1024)
	private String userLink;

	@Column(name = "user_description", length = 1024)
	private String userDescription;

	@Column(name = "hash_pw")
	private String hashPw;

	@Column(name = "social_id", updatable = false)
	private String socialId;

	@Enumerated(value = EnumType.STRING)
	@Column(name = "sns_user_gender")
	private SnsUserGender snsUserGender;

	@Column(name = "birth_date")
	private LocalDate birthDate;

	@Enumerated(value = EnumType.STRING)
	@Column(name = "sns_user_state")
	private SnsUserState snsUserState;

	@Enumerated(value = EnumType.STRING)
	@Column(name = "sns_app_role")
	private SnsAppRole snsAppRole;

	@Enumerated(value = EnumType.STRING)
	@Column(name = "sign_up_type")
	private SignUpType signUpType;

	@Column(name = "is_private_profile", nullable = false)
	@ColumnDefault(value = "false")
	private Boolean isPrivateProfile;

	@Column(name = "profile_path", length = 512)
	private String profilePath;

	@Column(name = "refresh_token")
	private String refreshToken; // refresh_token

	@Column(name = "deleted_at")
	private LocalDateTime deletedAt;

	// 알림 속성

	@Column(name = "has_follower_notification", nullable = false)
	@ColumnDefault(value = "true")
	private Boolean hasFollowerNotification;

	// 리프레쉬 토큰 업데이트
	public void updateRefreshToken(String refreshToken) {
		this.refreshToken = refreshToken;
	}

	// @ANSWER: 직접 조회 하는 방식으로 결정
	// @Column(name = "follower_num")
	// @ColumnDefault(value = "0")
	// private Integer followerNum;
	//
	// @Column(name = "following_num")
	// @ColumnDefault(value = "0")
	// private Integer followingNum;

	// 리프레쉬 토큰 초기화
	public void deleteRefreshToken() {
		this.refreshToken = null;
	}

	// 탈퇴 후, 14일 전 재 가입
	public void rejoin() {
		this.deletedAt = null;
		this.snsUserState = SnsUserState.ACTIVE;
	}

	public void withdrawal() {
		this.deleteRefreshToken();
		this.snsUserState = SnsUserState.DELETED;
		this.deletedAt = LocalDateTime.now();
	}
}

