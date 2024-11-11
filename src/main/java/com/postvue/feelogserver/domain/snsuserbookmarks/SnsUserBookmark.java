// package com.postvue.feelogserver.domain.snsuserbookmarks;
//
// import java.io.Serializable;
//
// import com.postvue.feelogserver.core.config.SnowflakeId;
// import com.postvue.feelogserver.domain.snsposts.SnsPost;
// import com.postvue.feelogserver.domain.snsusers.SnsUser;
// import com.postvue.feelogserver.global.common.tables.mixin.BaseMixinImpl;
//
// import jakarta.persistence.Column;
// import jakarta.persistence.Entity;
// import jakarta.persistence.FetchType;
// import jakarta.persistence.Id;
// import jakarta.persistence.JoinColumn;
// import jakarta.persistence.ManyToOne;
// import jakarta.persistence.Table;
// import lombok.AllArgsConstructor;
// import lombok.Builder;
// import lombok.Getter;
// import lombok.NoArgsConstructor;
// import lombok.Setter;
//
// @Entity
// @Getter
// @Setter
// @Table(name = "SNS_USER_BOOKMARKS_TB")
// @Builder
// @NoArgsConstructor
// @AllArgsConstructor
// public class SnsUserBookmark extends BaseMixinImpl implements Serializable {
// 	@Id
// 	@SnowflakeId
// 	@Column(name = "sns_user_bookmark_id")
// 	private Long snsUserBookmarkId;
//
// 	@ManyToOne(fetch = FetchType.LAZY)
// 	@JoinColumn(name = "sns_user_id")
// 	private SnsUser snsUser;
//
// 	@ManyToOne(fetch = FetchType.LAZY)
// 	@JoinColumn(name = "sns_post_id")
// 	private SnsPost snsPost;
// }
