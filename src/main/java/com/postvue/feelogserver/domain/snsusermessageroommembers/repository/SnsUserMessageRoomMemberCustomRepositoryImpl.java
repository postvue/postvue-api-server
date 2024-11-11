// package com.postvue.feelogserver.domain.snsusermessageroommembers.repository;
//
// import java.time.LocalDateTime;
//
// import org.springframework.stereotype.Repository;
//
// import com.postvue.feelogserver.domain.snsusermessageroommembers.QSnsUserMessageRoomMember;
// import com.querydsl.jpa.impl.JPAQueryFactory;
//
// import lombok.RequiredArgsConstructor;
//
// @Repository
// @RequiredArgsConstructor
// public class SnsUserMessageRoomMemberCustomRepositoryImpl
// 	implements SnsUserMessageRoomMemberCustomRepository {
//
// 	private final JPAQueryFactory queryFactory;
//
// 	@Override
// 	public long updateMessageRoomMemberReadAt(Long snsUserId, Long snsUserMessageRoomId,
// 		LocalDateTime currentDateTime) {
// 		QSnsUserMessageRoomMember snsUserMessageRoomMember = QSnsUserMessageRoomMember.snsUserMessageRoomMember;
// 		return queryFactory.update(snsUserMessageRoomMember)
// 			.set(snsUserMessageRoomMember.readAt, currentDateTime)
// 			.where(snsUserMessageRoomMember.sourceUser.snsUserId.eq(snsUserId)
// 				.and(snsUserMessageRoomMember.snsUserMessageRoom.snsUserMessageRoomId.eq(snsUserMessageRoomId)))
// 			.execute();
// 	}
// }
