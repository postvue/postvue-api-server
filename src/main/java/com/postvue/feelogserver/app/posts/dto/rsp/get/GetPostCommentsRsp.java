package com.postvue.feelogserver.app.posts.dto.rsp.get;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class GetPostCommentsRsp {
	String cursorId;
	List<SnsPostCommentRsp> snsPostCommentRspList;
}
