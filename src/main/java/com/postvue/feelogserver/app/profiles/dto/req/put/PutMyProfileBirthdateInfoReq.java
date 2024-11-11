package com.postvue.feelogserver.app.profiles.dto.req.put;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;

@Getter
public class PutMyProfileBirthdateInfoReq {
	@NotEmpty(message = "생년월일 비어 있습니다.")
	@Pattern(regexp = "\\d{4}-\\d{2}-\\d{2}", message = "생년월일은 yyyy-MM-dd 형식이어야 합니다.")
	private String birthdate;

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
