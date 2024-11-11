// package com.postvue.feelogserver.domain.snsposts.repository;
//
// import java.util.List;
//
// import org.springframework.stereotype.Repository;
//
// import com.postvue.feelogserver.domain.snsblockusers.QSnsBlockUser;
// import com.postvue.feelogserver.domain.snsposts.QSnsPost;
// import com.postvue.feelogserver.domain.snsposts.dto.PostDao;
// import com.postvue.feelogserver.domain.snspostuserreactions.QSnsPostUserReaction;
// import com.postvue.feelogserver.domain.snsuserfollows.QSnsUserFollow;
// import com.postvue.feelogserver.domain.snsusers.QSnsUser;
// import com.querydsl.core.types.Projections;
// import com.querydsl.core.types.dsl.BooleanExpression;
// import com.querydsl.core.types.dsl.Expressions;
// import com.querydsl.core.types.dsl.NumberExpression;
// import com.querydsl.jpa.impl.JPAQueryFactory;
//
// import lombok.RequiredArgsConstructor;
//
// @Repository
// @RequiredArgsConstructor
// public class SnsPostCustomRepositoryImpl implements SnsPostCustomRepository {
// 	private final JPAQueryFactory queryFactory;
//
// 	@Override
// 	public List<PostDao> findNearbyPosts(Long snsUserId, Float userLatitude, Float userLongitude,
// 		String postContentBusinessType, int pageSize, int offset) {
// 		QSnsPost snsPost = QSnsPost.snsPost;
// 		QSnsUser snsUser = QSnsUser.snsUser;
// 		QSnsUserFollow snsFollow = QSnsUserFollow.snsUserFollow;
// 		QSnsPostUserReaction snsReactions = QSnsPostUserReaction.snsPostUserReaction;
// 		QSnsBlockUser snsBlockUsers = QSnsBlockUser.snsBlockUser;
//
// 		// 거리 계산에 필요한 인수
// 		NumberExpression<Float> latitudeDiff = snsPost.latitude.subtract(userLatitude)
// 			.multiply(snsPost.latitude.subtract(userLatitude));
// 		NumberExpression<Float> longitudeDiff = snsPost.longitude.subtract(userLongitude)
// 			.multiply(Expressions.numberTemplate(Float.class, "cos(radians({0}))", userLatitude));
//
// 		NumberExpression<Float> longitudeA = longitudeDiff.multiply(longitudeDiff);
//
// 		// 거리 표현식
// 		NumberExpression<Float> distanceExpression = Expressions.numberTemplate(Float.class,
// 			"111.32 * sqrt({0} + {1})", latitudeDiff, longitudeA);
//
// 		BooleanExpression distanceCondition = distanceExpression.loe(8.0f);
//
// 		return queryFactory
// 			.select(Projections.constructor(PostDao.class,
// 				snsPost.snsPostId,
// 				snsReactions.isLiked.coalesce(false),
// 				snsReactions.isReposted.coalesce(false),
// 				snsReactions.isClipped.coalesce(false),
// 				snsReactions.isBookmarked.coalesce(false),
// 				Expressions.cases()
// 					.when(snsPost.snsUser.snsUserId.eq(snsUserId)).then(false)
// 					.otherwise(true),
// 				snsFollow.followingUser.snsUserId,
// 				snsPost.latitude,
// 				snsPost.longitude,
// 				snsPost.address,
// 				snsPost.postTitle,
// 				snsPost.postBodyText,
// 				Expressions.stringTemplate("CAST({0} AS text)", snsPost.snsPostContents),
// 				Expressions.stringTemplate("CAST({0} AS text)", snsPost.tags),
// 				snsUser.profilePath,
// 				snsPost.snsUser.snsUserId,
// 				snsUser.username,
// 				snsPost.createdAt
// 			))
// 			.from(snsPost)
// 			.innerJoin(snsUser)
// 			.on(snsPost.snsUser.snsUserId.eq(snsUser.snsUserId))
// 			.leftJoin(snsFollow)
// 			.on(snsPost.snsUser.snsUserId.eq(snsFollow.followingUser.snsUserId)
// 				.and(snsFollow.followerUser.snsUserId.eq(snsUserId)))
// 			.leftJoin(snsReactions)
// 			.on(snsReactions.snsUser.snsUserId.eq(snsUserId).and(snsReactions.snsPost.snsPostId.eq(snsPost.snsPostId)))
// 			.leftJoin(snsBlockUsers)
// 			.on(snsBlockUsers.snsBlockedUser.snsUserId.eq(snsUserId)
// 				.and(snsBlockUsers.snsBlockedUser.snsUserId.eq(snsPost.snsUser.snsUserId)))
// 			.where(snsReactions.isShown.isTrue().or(snsReactions.isShown.isNull())
// 				.and(snsBlockUsers.snsBlockedUser.snsUserId.isNull())
// 				.and(distanceCondition)
// 			)
// 			.orderBy(distanceExpression.asc(),
// 				snsPost.createdAt.desc())
// 			.limit(pageSize)
// 			.offset(offset)
// 			.fetch();
// 	}
//
// }
