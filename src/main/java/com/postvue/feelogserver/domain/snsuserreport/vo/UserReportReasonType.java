package com.postvue.feelogserver.domain.snsuserreport.vo;

public enum UserReportReasonType {
	INAPPROPRIATE_CONTENT, //부적절한 콘텐츠
	SPAM_OR_PROMOTIONAL_CONTENT, //스팸/광고성 콘텐츠
	FALSE_INFORMATION_FRAUD, //허위 정보/사기
	PRIVACY_VIOLATION, //개인정보 침해
	COPYRIGHT_INFRINGEMENT, //저작권 침해
	HARASSMENT_OR_BULLYING, // 괴롭힘/따돌림
	OTHER, //직접 입력
}
