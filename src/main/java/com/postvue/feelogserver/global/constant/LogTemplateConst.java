package com.postvue.feelogserver.global.constant;

import java.util.Arrays;

public class LogTemplateConst {
	public static final String LOG_TEMPLATE = "{'type':'%s', 'error_type':'%s', 'msg':'%s','error_msg':'%s' 'class_path':'%s', 'method_name':'%s', 'args':%s, 'status_code':'%d' }";

	public static String getErrorLogTemplate(String errorType, String errorMsg, String systemErrorMsg, String classPath,
		String methodName,
		Object[] args,
		int statusCode) {
		return String.format(LOG_TEMPLATE, LogTypeConst.ERROR, errorType, errorMsg, systemErrorMsg, classPath,
			methodName,
			Arrays.toString(args),
			statusCode);
	}

	public static String getErrorLogTemplate(LogTypeConst logType, String errorType, String errorMsg,
		String systemErrorMsg, String classPath,
		String methodName,
		Object[] args,
		int statusCode) {
		return String.format(LOG_TEMPLATE, logType, errorType, errorMsg, systemErrorMsg, classPath,
			methodName,
			Arrays.toString(args),
			statusCode);
	}
}
