package com.postvue.feelogserver.global.constant;

import java.util.Arrays;

public class LogTemplateConst {
	public static final String LOG_INFO_TEMPLATE = "{'type':'%s', 'msg':'%s', 'created_at': '%s'}";
	public static final String LOG_SUCCESS_TEMPLATE = "{'type':'%s', 'msg':'%s', 'status_code':'%d', 'created_at': '%s'}";

	public static final String LOG_ERROR_TEMPLATE = "{'type':'%s', 'error_type':'%s', 'msg':'%s','error_msg':'%s', 'class_path':'%s', 'method_name':'%s', 'args':%s, 'status_code':'%d' }";

	public static String getLogInfoTemplate(String msg, String createdAt) {
		return String.format(LOG_INFO_TEMPLATE, LogTypeConst.INFO, msg, createdAt);
	}

	public static String getLogSuccessTemplate(LogTypeConst logTypeConst, String msg, int statusCode, String createdAt) {
		return String.format(LOG_SUCCESS_TEMPLATE, logTypeConst, msg, statusCode, createdAt);
	}

	public static String getErrorLogTemplate(String errorType, String errorMsg, String systemErrorMsg, String classPath,
		String methodName,
		Object[] args,
		int statusCode) {
		return String.format(LOG_ERROR_TEMPLATE, LogTypeConst.ERROR, errorType, errorMsg, systemErrorMsg, classPath,
			methodName,
			Arrays.toString(args),
			statusCode);
	}

	public static String getErrorLogTemplate(LogTypeConst logType, String errorType, String errorMsg,
		String systemErrorMsg, String classPath,
		String methodName,
		Object[] args,
		int statusCode) {
		return String.format(LOG_ERROR_TEMPLATE, logType, errorType, errorMsg, systemErrorMsg, classPath,
			methodName,
			Arrays.toString(args),
			statusCode);
	}
}
