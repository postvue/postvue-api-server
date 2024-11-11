package com.postvue.feelogserver.global.util.password;

import java.util.regex.Pattern;

import com.postvue.feelogserver.global.constant.AccountConst;

public final class PasswordValidationUtil {
	private static final String ACCOUNT_PASSWORD_MIN_NUM = AccountConst.ACCOUNT_PASSWORD_MIN_NUM.toString();
	private static final String ACCOUNT_PASSWORD_MAX_NUM = AccountConst.ACCOUNT_PASSWORD_MAX_NUM.toString();
	private static final String ALLOWED_SPECIAL_CHAR_LIST = AccountConst.ALLOWED_SPECIAL_CHAR_LIST;
	private static final Pattern LENGTH_REGEX = Pattern.compile(
		"^.{" + ACCOUNT_PASSWORD_MIN_NUM + "," + ACCOUNT_PASSWORD_MAX_NUM + "}$");
	private static final Pattern FIRST_CHAR_IS_LETTER_REGEX = Pattern.compile("^[a-zA-Z].*");
	private static final Pattern VALID_CHARS_REGEX = Pattern.compile("^[a-zA-Z0-9!@_]+$");
	private static final Pattern SPECIAL_CHAR_REGEX = Pattern.compile("[" + ALLOWED_SPECIAL_CHAR_LIST + "]");

	public static boolean isValid(String password) {
		return isValidLength(password) &&
			isFirstCharLetter(password) &&
			hasValidCharacters(password) &&
			hasSpecialCharacter(password);
	}

	private static boolean isValidLength(String password) {
		return LENGTH_REGEX.matcher(password).matches();
	}

	private static boolean isFirstCharLetter(String password) {
		return FIRST_CHAR_IS_LETTER_REGEX.matcher(password).matches();
	}

	private static boolean hasValidCharacters(String password) {
		return VALID_CHARS_REGEX.matcher(password).matches();
	}

	private static boolean hasSpecialCharacter(String password) {
		return SPECIAL_CHAR_REGEX.matcher(password).find();
	}
}
