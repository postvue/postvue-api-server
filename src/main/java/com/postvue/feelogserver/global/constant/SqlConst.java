package com.postvue.feelogserver.global.constant;

public final class SqlConst {
	public static final String SNS_POSTS_BY_POPULAR_NATIVE_SQL = "sns_posts_by_popular AS (SELECT SNS_POST.sns_post_id FROM sns_posts_tb AS SNS_POST ORDER BY (sns_post.reaction_count - 2 * POWER(EXTRACT(day FROM (sns_post.created_at - :currentDateTime)),2)) DESC LIMIT :pageSize offset :page)";
}
