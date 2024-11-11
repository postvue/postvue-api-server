package com.postvue.feelogserver.app.auth.dto.req.post;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class SignupUserInfoReq {
	@NotBlank
	private String username;

	@NotBlank
	private String nickname;

	@NotBlank
	@Pattern(regexp = "\\d{4}-\\d{2}-\\d{2}", message = "생년월일은 yyyy-MM-dd 형식이어야 합니다.")
	private String birthdate;

	@NotBlank
	private String gender;

	@Size(min = 5, max = 5, message = "5개 관심 태그 리스트를 보내주어야 됩니다.")
	private List<String> favoriteTagList;

	// @Valid
	// @NotNull
	// private SignupTermOfService signupTermOfService;
	//
	// public class SignupTermOfService {
	// 	@NotNull(message = "연령 동의는 필수입니다.")
	// 	@AssertTrue(message = "연령 동의는 반드시 true여야 합니다.")
	// 	private Boolean agreeToAgeTerm;
	//
	// 	@NotNull(message = "서비스 약관 동의는 필수입니다.")
	// 	@AssertTrue(message = "서비스 약관 동의는 반드시 동의 해주셔야 합니다.")
	// 	private Boolean agreeToServieTerm;
	//
	// 	@NotNull(message = "개인정보 보호정책 동의는 필수입니다.")
	// 	@AssertTrue(message = "개인정보 보호정책 동의 반드시 동의 해주셔야 합니다.")
	// 	private Boolean agreeToPrivacyPolicy;
	//
	// 	@NotNull(message = "제3자 제공 개인정보 보호정책 동의는 필수입니다.")
	// 	@AssertTrue(message = "제3자 제공 개인정보 보호정책 반드시 동의 해주셔야 합니다.")
	// 	private Boolean agreeToPrivacyPolicyToThirdPaties;
	//
	// 	@NotNull(message = "마케팅 정보 수신 동의는 필수입니다.")
	// 	private Boolean agreeToMarketingCommunications;
	// }

	// birthDate를 LocalDate로 변환하는 메서드
	public LocalDate convertBirthDateAsLocalDate() {
		try {
			// "yyyy-MM-dd" 형식으로 String을 LocalDate로 변환
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
			return LocalDate.parse(this.birthdate, formatter);
		} catch (DateTimeParseException e) {
			// 만약 날짜 형식이 잘못되었을 경우 예외 처리
			throw new IllegalArgumentException("생년월일 형식이 잘못되었습니다. yyyy-MM-dd 형식이어야 합니다.");
		}
	}
}

