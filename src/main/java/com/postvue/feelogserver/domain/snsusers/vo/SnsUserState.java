package com.postvue.feelogserver.domain.snsusers.vo;

public enum SnsUserState {
	ACTIVE, //활성
	DORMANT, // 휴면
	SUSPENDED, // 정지
	PENDING, // 대기 중
	DELETED, //탈퇴, 최대 7일 까지 데이터 유지
	FULL_DELETED // 완전 삭제 (유저는 새로운 아이디로 가입 됨)
}
